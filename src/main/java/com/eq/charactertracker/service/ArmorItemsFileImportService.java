package com.eq.charactertracker.service;

import com.eq.charactertracker.constants.ServerType;
import com.eq.charactertracker.entity.ArmorEntity;
import com.eq.charactertracker.entity.ServerEntity;
import com.eq.charactertracker.model.ServerDTO;
import com.eq.charactertracker.model.imports.ArmorItemsFileImportResult;
import com.eq.charactertracker.repo.ArmorRepo;
import com.eq.charactertracker.util.CharacterArmorUtil;
import com.eq.charactertracker.util.ItemBitmaskUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// Imports/updates LIVE armor rows (matched by ext_id, the real EQ item id) from an offline
// EQEmu-style `items` table dump (pipe-delimited, header row with column names -- see
// items.txt). Any row with a non-zero `slots` bitmask (i.e. equippable somewhere -- armor
// and weapons alike, ammo included) is upserted as an ArmorEntity; rows with slots=0 aren't
// equippable and are skipped. Expansion isn't in this dump; it's backfilled separately from
// Allakhazam's Expansion field. Fields items.txt doesn't carry (accuracy, avoidance, combat
// effect, stun resist, damage/spell shielding, strikethrough) are left untouched.
//
// items.txt's worneffect/focuseffect/clickeffect columns are spell ids into the same id
// space as Lucy_Data (confirmed against real items: worneffect 9615 = "Ferocity VII"), and
// items.txt rarely carries the resolved name itself (~1.6% of rows) -- Lucy_Data.name is the
// fallback lookup. Assumes Lucy_Data has `id`/`name` columns matching EQEmu's spells_new
// convention; adjust LUCY_DATA_ID_COLUMN/LUCY_DATA_NAME_COLUMN below if that's not the case.
@Service
@RequiredArgsConstructor
@Slf4j
public class ArmorItemsFileImportService {


    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_CLASSES = "classes";
    private static final String COL_SLOTS = "slots";
    private static final String COL_AC = "ac";
    private static final String COL_STR = "astr";
    private static final String COL_STA = "asta";
    private static final String COL_AGI = "aagi";
    private static final String COL_DEX = "adex";
    private static final String COL_WIS = "awis";
    private static final String COL_INT = "aint";
    private static final String COL_CHA = "acha";
    private static final String COL_HP = "hp";
    private static final String COL_MANA = "mana";
    private static final String COL_ENDURANCE = "endurance";
    private static final String COL_FIRE = "fr";
    private static final String COL_COLD = "cr";
    private static final String COL_MAGIC = "mr";
    private static final String COL_DISEASE = "dr";
    private static final String COL_POISON = "pr";
    private static final String COL_ATTACK = "attack";
    private static final String COL_HP_REGEN = "regen";
    private static final String COL_MANA_REGEN = "manaregen";
    private static final String COL_ENDURANCE_REGEN = "enduranceregen";
    private static final String COL_HEROIC_STR = "heroic_str";
    private static final String COL_HEROIC_STA = "heroic_sta";
    private static final String COL_HEROIC_AGI = "heroic_agi";
    private static final String COL_HEROIC_DEX = "heroic_dex";
    private static final String COL_HEROIC_WIS = "heroic_wis";
    private static final String COL_HEROIC_INT = "heroic_int";
    private static final String COL_HEROIC_CHA = "heroic_cha";
    private static final String COL_WORN_EFFECT = "worneffect";
    private static final String COL_WORN_NAME = "wornname";
    private static final String COL_FOCUS_EFFECT = "focuseffect";
    private static final String COL_FOCUS_NAME = "focusname";
    private static final String COL_CLICK_EFFECT = "clickeffect";
    private static final String COL_CLICK_NAME = "clickname";

    private static final String[] REQUIRED_COLUMNS = {
            COL_ID, COL_NAME, COL_CLASSES, COL_SLOTS, COL_AC, COL_STR, COL_STA, COL_AGI, COL_DEX, COL_WIS, COL_INT,
            COL_CHA, COL_HP, COL_MANA, COL_ENDURANCE, COL_FIRE, COL_COLD, COL_MAGIC, COL_DISEASE, COL_POISON,
            COL_ATTACK, COL_HP_REGEN, COL_MANA_REGEN, COL_ENDURANCE_REGEN, COL_HEROIC_STR, COL_HEROIC_STA,
            COL_HEROIC_AGI, COL_HEROIC_DEX, COL_HEROIC_WIS, COL_HEROIC_INT, COL_HEROIC_CHA, COL_WORN_EFFECT,
            COL_WORN_NAME, COL_FOCUS_EFFECT, COL_FOCUS_NAME, COL_CLICK_EFFECT, COL_CLICK_NAME
    };

    private static final String LUCY_DATA_ID_COLUMN = "id";
    private static final String LUCY_DATA_NAME_COLUMN = "name";
    private static final long NO_EFFECT = -1;

    private final ModelMapper modelMapper;
    private final ArmorRepo armorRepo;
    private final ServerService serverService;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public ArmorItemsFileImportResult importArmorFromFile(String filePath) {
        ServerDTO liveServer = serverService.findByType(ServerType.LIVE).stream().findFirst()
                .orElseThrow();

        List<ArmorEntity> liveArmor = armorRepo.findByServer_Type(ServerType.LIVE);
        Map<Long, ArmorEntity> armorByExtId = liveArmor.stream()
                .collect(Collectors.toMap(ArmorEntity::getExtId, a -> a, (a, b) -> a));

        ArmorItemsFileImportResult result = new ArmorItemsFileImportResult();
        Map<Long, String> effectNameCache = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IllegalStateException("Items file is empty: " + filePath);
            }
            Map<String, Integer> columnIndex = indexColumns(headerLine);
            for (String required : REQUIRED_COLUMNS) {
                requireColumn(columnIndex, required);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                result.setTotalRows(result.getTotalRows() + 1);
                String[] fields = line.split("\\|", -1);
                Long extId = parseLong(fields[columnIndex.get(COL_ID)]);
                if (extId == null || extId == 0) {
                    // id=0 is an internal engine placeholder (e.g. paperdoll hand slots), not a real item
                    continue;
                }
                long slotsBitmask = parseLong(fields[columnIndex.get(COL_SLOTS)]) == null ? 0 : parseLong(fields[columnIndex.get(COL_SLOTS)]);
                if (slotsBitmask == 0) {
                    result.setSkippedNoSlot(result.getSkippedNoSlot() + 1);
                    continue;
                }

                ArmorEntity armor = armorByExtId.get(extId);
                boolean isNew = armor == null;
                if (isNew) {
                    armor = new ArmorEntity();
                    armor.setExtId(extId);
                    armor.setServer(modelMapper.map(liveServer, ServerEntity.class));
                }
                armor.setName(fields[columnIndex.get(COL_NAME)]);
                applyFields(armor, fields, columnIndex, effectNameCache);
                CharacterArmorUtil.setNullFieldsToZero(armor);
                armor = armorRepo.save(armor);
                armorByExtId.put(extId, armor);

                if (isNew) {
                    result.setCreated(result.getCreated() + 1);
                } else {
                    result.setUpdated(result.getUpdated() + 1);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to read items file: " + filePath, e);
        }

        log.info("Armor items-file import: {} rows, {} created, {} updated, {} skipped (no slot)",
                result.getTotalRows(), result.getCreated(), result.getUpdated(), result.getSkippedNoSlot());
        return result;
    }

    private void applyFields(ArmorEntity armor, String[] fields, Map<String, Integer> columnIndex, Map<Long, String> effectNameCache) {
        armor.setAc(parseInt(fields, columnIndex, COL_AC));
        armor.setStrength(parseInt(fields, columnIndex, COL_STR));
        armor.setStamina(parseInt(fields, columnIndex, COL_STA));
        armor.setAgility(parseInt(fields, columnIndex, COL_AGI));
        armor.setDexterity(parseInt(fields, columnIndex, COL_DEX));
        armor.setWisdom(parseInt(fields, columnIndex, COL_WIS));
        armor.setIntelligence(parseInt(fields, columnIndex, COL_INT));
        armor.setCharisma(parseInt(fields, columnIndex, COL_CHA));
        armor.setHitPoints(parseInt(fields, columnIndex, COL_HP));
        armor.setManaPoints(parseInt(fields, columnIndex, COL_MANA));
        armor.setEndurancePoints(parseInt(fields, columnIndex, COL_ENDURANCE));
        armor.setFireResist(parseInt(fields, columnIndex, COL_FIRE));
        armor.setColdResist(parseInt(fields, columnIndex, COL_COLD));
        armor.setMagicResist(parseInt(fields, columnIndex, COL_MAGIC));
        armor.setDiseaseResist(parseInt(fields, columnIndex, COL_DISEASE));
        armor.setPoisonResist(parseInt(fields, columnIndex, COL_POISON));
        armor.setAttack(parseInt(fields, columnIndex, COL_ATTACK));
        armor.setHitPointsRegen(parseInt(fields, columnIndex, COL_HP_REGEN));
        armor.setManaPointsRegen(parseInt(fields, columnIndex, COL_MANA_REGEN));
        armor.setEnduranceRegen(parseInt(fields, columnIndex, COL_ENDURANCE_REGEN));
        armor.setHeroicStrength(parseInt(fields, columnIndex, COL_HEROIC_STR));
        armor.setHeroicStamina(parseInt(fields, columnIndex, COL_HEROIC_STA));
        armor.setHeroicAgility(parseInt(fields, columnIndex, COL_HEROIC_AGI));
        armor.setHeroicDexterity(parseInt(fields, columnIndex, COL_HEROIC_DEX));
        armor.setHeroicWisdom(parseInt(fields, columnIndex, COL_HEROIC_WIS));
        armor.setHeroicIntelligence(parseInt(fields, columnIndex, COL_HEROIC_INT));
        armor.setHeroicCharisma(parseInt(fields, columnIndex, COL_HEROIC_CHA));

        long classesBitmask = parseLong(fields[columnIndex.get(COL_CLASSES)]) == null ? 0 : parseLong(fields[columnIndex.get(COL_CLASSES)]);
        long slotsBitmask = parseLong(fields[columnIndex.get(COL_SLOTS)]) == null ? 0 : parseLong(fields[columnIndex.get(COL_SLOTS)]);
        armor.setClasses(ItemBitmaskUtil.decodeClasses(classesBitmask));
        armor.setSlots(ItemBitmaskUtil.decodeSlots(slotsBitmask));

        armor.setFocusEffect(resolveFocusEffects(fields, columnIndex, effectNameCache));
        armor.setClickEffect(resolveClickEffects(fields, columnIndex, effectNameCache));
    }

    private Set<String> resolveFocusEffects(String[] fields, Map<String, Integer> columnIndex, Map<Long, String> effectNameCache) {
        Set<String> names = new LinkedHashSet<>();
        addEffectName(names, fields, columnIndex, COL_WORN_EFFECT, COL_WORN_NAME, effectNameCache);
        addEffectName(names, fields, columnIndex, COL_FOCUS_EFFECT, COL_FOCUS_NAME, effectNameCache);
        return names;
    }

    private Set<String> resolveClickEffects(String[] fields, Map<String, Integer> columnIndex, Map<Long, String> effectNameCache) {
        Set<String> names = new LinkedHashSet<>();
        addEffectName(names, fields, columnIndex, COL_CLICK_EFFECT, COL_CLICK_NAME, effectNameCache);
        return names;
    }

    private void addEffectName(Set<String> names, String[] fields, Map<String, Integer> columnIndex,
                                String idColumn, String nameColumn, Map<Long, String> effectNameCache) {
        Long effectId = parseLong(fields[columnIndex.get(idColumn)]);
        if (effectId == null || effectId == NO_EFFECT || effectId == 0) {
            return;
        }
        String providedName = fields[columnIndex.get(nameColumn)];
        if (providedName != null && !providedName.isBlank()) {
            names.add(providedName);
            return;
        }
        String resolved = effectNameCache.computeIfAbsent(effectId, this::lookupLucyDataName);
        if (resolved != null) {
            names.add(resolved);
        }
    }

    private String lookupLucyDataName(Long effectId) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT " + LUCY_DATA_NAME_COLUMN + " FROM Lucy_Data WHERE " + LUCY_DATA_ID_COLUMN + " = ?",
                    String.class, effectId);
        } catch (EmptyResultDataAccessException e) {
            log.warn("No Lucy_Data match for effect id {}", effectId);
            return null;
        }
    }

    private Map<String, Integer> indexColumns(String headerLine) {
        String[] headers = headerLine.split("\\|", -1);
        Map<String, Integer> columnIndex = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            columnIndex.put(headers[i], i);
        }
        return columnIndex;
    }

    private void requireColumn(Map<String, Integer> columnIndex, String name) {
        if (!columnIndex.containsKey(name)) {
            throw new IllegalStateException("Items file is missing expected column: " + name);
        }
    }

    private int parseInt(String[] fields, Map<String, Integer> columnIndex, String columnName) {
        Long value = parseLong(fields[columnIndex.get(columnName)]);
        return value == null ? 0 : value.intValue();
    }

    private Long parseLong(String value) {
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
