package com.eq.charactertracker.service;

import com.eq.charactertracker.constants.NpcTier;
import com.eq.charactertracker.constants.ServerType;
import com.eq.charactertracker.entity.NpcEntity;
import com.eq.charactertracker.entity.ZoneEntity;
import com.eq.charactertracker.entity.quest.ExpansionEntity;
import com.eq.charactertracker.model.imports.HunterImportResult;
import com.eq.charactertracker.repo.NpcRepo;
import com.eq.charactertracker.repo.ZoneRepo;
import com.eq.charactertracker.repo.quest.ExpansionRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

// Imports mq2-scripts/hunter_raid_extract.lua's output
// (expansion|category|zoneAchievementName|description, pipe-delimited).
//
// Hunter/Hunts rows are real per-zone NPC kills. Raids rows are only used when the
// achievement name is "Conqueror of <zone>" -- the rest of that category (tradeskill
// rollups, explorer/slayer meta-achievements, seasonal events) carries no reliable
// target data. Even the "Conqueror of" rows are noisy: classic-era ones list clean
// raid-boss names, but later expansions list encounter/event step names ("Complete
// Phase 1", "The Roots of Evil") instead of mobs. So every npc created from a raid row
// is tier RAID_NAMED with needsReview = true, for a human to confirm or delete -- see
// NpcEntity.needsReview.
//
// Every zone achievement tree also mixes in region-rollup achievements whose
// "objectives" are just other achievement names (e.g. "Hunter of Faydwer" listing
// "Hunter of Butcherblock Mountains", or a "Conqueror of" listing another "Conqueror
// of") rather than an actual mob -- confirmed empirically against a real export. A
// description starting with "Hunter of ", "Hunts of ", or "Conqueror of " is always
// one of those rollup references, never a real mob name, so it's filtered out rather
// than imported as an NPC.
@Service
@RequiredArgsConstructor
@Slf4j
public class HunterImportService {

    private static final Set<String> HUNTER_CATEGORIES = Set.of("Hunter", "Hunts");
    private static final String RAIDS_CATEGORY = "Raids";
    private static final String CONQUEROR_PREFIX = "Conqueror of ";
    // Used both to derive a zone name from the achievement name (strip the prefix) and to
    // recognise a rollup-reference description (a desc that starts with one of these points
    // at another achievement, not a mob).
    private static final String[] ZONE_ACHIEVEMENT_PREFIXES = {"Hunter of ", "Hunts of ", CONQUEROR_PREFIX};

    private final ZoneRepo zoneRepo;
    private final NpcRepo npcRepo;
    private final ExpansionRepo expansionRepo;

    @Transactional
    public HunterImportResult importFromFile(String filePath) {
        HunterImportResult result = new HunterImportResult();
        List<ExpansionEntity> liveExpansions = expansionRepo.findByServerTypeOrderByReleaseOrderAsc(ServerType.LIVE);
        Map<String, ZoneEntity> zoneCache = new HashMap<>();
        // Tracks (zoneId|name) pairs already handled this run, to avoid redundant DB lookups
        // for the same npc-in-this-zone appearing on multiple lines -- NOT keyed by name
        // alone, since the same name can be a different creature in a different zone.
        Set<String> processedNpcZonePairs = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                result.setTotalRows(result.getTotalRows() + 1);
                processLine(line, result, liveExpansions, zoneCache, processedNpcZonePairs);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to read hunter export file: " + filePath, e);
        }

        log.info("Hunter import: {} rows, {} npc-zone links created ({} raid-named, flagged for review), "
                        + "{} skipped (non-Hunter), {} skipped (non-Conqueror raid), {} skipped (empty desc), "
                        + "{} skipped (rollup ref)",
                result.getTotalRows(), result.getNpcZoneLinksCreated(), result.getRaidNamedCreated(),
                result.getSkippedNonHunterCategory(), result.getSkippedNonConquerorRaid(),
                result.getSkippedEmptyDescription(), result.getSkippedRollupReference());
        return result;
    }

    private void processLine(String line, HunterImportResult result, List<ExpansionEntity> liveExpansions,
                              Map<String, ZoneEntity> zoneCache, Set<String> processedNpcZonePairs) {
        String[] fields = line.split("\\|", -1);
        if (fields.length != 4) {
            return;
        }
        String expansionName = fields[0].trim();
        String category = fields[1].trim();
        String zoneAchievementName = fields[2].trim();
        String description = fields[3].trim();

        boolean isRaid = RAIDS_CATEGORY.equals(category);
        if (!isRaid && !HUNTER_CATEGORIES.contains(category)) {
            result.setSkippedNonHunterCategory(result.getSkippedNonHunterCategory() + 1);
            return;
        }
        // In the Raids category, only "Conqueror of <zone>" achievements name kill targets;
        // everything else there is a rollup/tradeskill/event with no usable data.
        if (isRaid && !zoneAchievementName.startsWith(CONQUEROR_PREFIX)) {
            result.setSkippedNonConquerorRaid(result.getSkippedNonConquerorRaid() + 1);
            return;
        }
        if (description.isEmpty() || description.equalsIgnoreCase("nil")) {
            result.setSkippedEmptyDescription(result.getSkippedEmptyDescription() + 1);
            return;
        }
        if (isRollupReference(description)) {
            result.setSkippedRollupReference(result.getSkippedRollupReference() + 1);
            return;
        }

        String zoneName = stripAchievementPrefix(zoneAchievementName);
        ExpansionEntity expansion = matchExpansion(expansionName, liveExpansions);
        if (expansion == null && !result.getUnmatchedExpansionNames().contains(expansionName)) {
            result.setZonesWithNoExpansionMatch(result.getZonesWithNoExpansionMatch() + 1);
            result.getUnmatchedExpansionNames().add(expansionName);
        }

        // Cache key includes expansion -- zone name alone isn't unique (see upsertZone), so a
        // name-only key here would wrongly reuse one expansion's zone entity for a same-named
        // zone under a different expansion later in the same file.
        String expansionKey = expansion != null ? String.valueOf(expansion.getId()) : "unmatched";
        String zoneCacheKey = expansionKey + "|" + zoneName.toLowerCase();
        ZoneEntity zone = zoneCache.computeIfAbsent(zoneCacheKey, k -> upsertZone(zoneName, expansion));

        // Raid rows are all flagged for manual review (isRaid doubles as the needsReview
        // flag) since the "Conqueror of" data mixes real mobs with encounter/event names.
        NpcTier tier = isRaid ? NpcTier.RAID_NAMED : NpcTier.GROUP_NAMED;
        String npcZoneKey = zone.getId() + "|" + description.toLowerCase();
        if (processedNpcZonePairs.add(npcZoneKey) && upsertNpcInZone(description, zone, tier, isRaid)) {
            result.setNpcZoneLinksCreated(result.getNpcZoneLinksCreated() + 1);
            if (isRaid) {
                result.setRaidNamedCreated(result.getRaidNamedCreated() + 1);
            }
        }
    }

    private boolean isRollupReference(String description) {
        for (String prefix : ZONE_ACHIEVEMENT_PREFIXES) {
            if (description.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private String stripAchievementPrefix(String zoneAchievementName) {
        for (String prefix : ZONE_ACHIEVEMENT_PREFIXES) {
            if (zoneAchievementName.startsWith(prefix)) {
                return zoneAchievementName.substring(prefix.length());
            }
        }
        return zoneAchievementName;
    }

    // Achievement category names for expansions don't reliably match ExpansionEntity.name
    // verbatim (some drop a leading "The", some don't -- confirmed inconsistent live), so
    // this compares with "The " stripped from both sides rather than requiring an exact match.
    private ExpansionEntity matchExpansion(String achievementExpansionName, List<ExpansionEntity> liveExpansions) {
        String normalizedTarget = normalize(achievementExpansionName);
        for (ExpansionEntity expansion : liveExpansions) {
            if (normalize(expansion.getName()).equals(normalizedTarget)) {
                return expansion;
            }
        }
        return null;
    }

    private String normalize(String name) {
        if (name == null) {
            return "";
        }
        String trimmed = name.trim();
        if (trimmed.regionMatches(true, 0, "The ", 0, 4)) {
            trimmed = trimmed.substring(4);
        }
        return trimmed.toLowerCase();
    }

    private ZoneEntity upsertZone(String zoneName, ExpansionEntity expansion) {
        Optional<ZoneEntity> existing = expansion != null
                ? zoneRepo.findByNameIgnoreCaseAndExpansion_Id(zoneName, expansion.getId())
                : zoneRepo.findByNameIgnoreCaseAndExpansionIsNull(zoneName);
        ZoneEntity zone = existing.orElseGet(ZoneEntity::new);
        zone.setName(zoneName);
        if (expansion != null) {
            zone.setExpansion(expansion);
        }
        return zoneRepo.save(zone);
    }

    // Returns true if a new npc-in-this-zone record was created, false if one already
    // existed (e.g. a re-run of an already-imported file). An existing npc is left as-is --
    // its tier/needsReview aren't overwritten, so a re-run won't re-flag a curated raid npc.
    private boolean upsertNpcInZone(String npcName, ZoneEntity zone, NpcTier tier, boolean needsReview) {
        Optional<NpcEntity> existing = npcRepo.findByNameIgnoreCaseAndZones_Id(npcName, zone.getId());
        if (existing.isPresent()) {
            return false;
        }
        NpcEntity npc = new NpcEntity();
        npc.setName(npcName);
        npc.setTier(tier);
        npc.setNeedsReview(needsReview);
        Set<ZoneEntity> zones = new HashSet<>();
        zones.add(zone);
        npc.setZones(zones);
        npcRepo.save(npc);
        return true;
    }
}
