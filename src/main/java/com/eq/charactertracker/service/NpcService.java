package com.eq.charactertracker.service;

import com.eq.charactertracker.base.BaseEntity;
import com.eq.charactertracker.base.BaseServiceImpl;
import com.eq.charactertracker.constants.NpcTier;
import com.eq.charactertracker.entity.ArmorEntity;
import com.eq.charactertracker.entity.ItemEntity;
import com.eq.charactertracker.entity.NpcEntity;
import com.eq.charactertracker.entity.ZoneEntity;
import com.eq.charactertracker.entity.quest.ExpansionEntity;
import com.eq.charactertracker.model.ArmorDTO;
import com.eq.charactertracker.model.Item;
import com.eq.charactertracker.model.NpcCreateRequest;
import com.eq.charactertracker.model.NpcDTO;
import com.eq.charactertracker.model.ZoneDTO;
import com.eq.charactertracker.repo.ArmorRepo;
import com.eq.charactertracker.repo.BaseRepo;
import com.eq.charactertracker.repo.ItemRepo;
import com.eq.charactertracker.repo.NpcRepo;
import com.eq.charactertracker.repo.ZoneRepo;
import com.eq.charactertracker.repo.quest.ExpansionRepo;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class NpcService extends BaseServiceImpl<NpcEntity, NpcDTO> {

    private final NpcRepo npcRepo;
    private final ArmorRepo armorRepo;
    private final ZoneRepo zoneRepo;
    private final ExpansionRepo expansionRepo;
    // TODO: no ItemService exists yet -- calling ItemRepo directly, matching THFItemService.
    private final ItemRepo itemRepo;

    public NpcService(ModelMapper modelMapper, NpcRepo npcRepo, ArmorRepo armorRepo, ZoneRepo zoneRepo,
                       ExpansionRepo expansionRepo, ItemRepo itemRepo) {
        super(modelMapper, npcRepo);
        this.npcRepo = npcRepo;
        this.armorRepo = armorRepo;
        this.zoneRepo = zoneRepo;
        this.expansionRepo = expansionRepo;
        this.itemRepo = itemRepo;
    }

    // Requires at least one filter -- an unbounded "return every npc" query has no
    // pagination behind it yet, so a no-filter call is rejected rather than risking an
    // unbounded result set as the table grows.
    public List<NpcDTO> search(String name, Long zoneId, Long expansionId, NpcTier tier) {
        String normalizedName = blankToNull(name);
        if (normalizedName == null && zoneId == null && expansionId == null && tier == null) {
            throw new IllegalArgumentException("At least one of name, zoneId, expansionId, or tier is required");
        }
        String namePattern = normalizedName == null ? null : "%" + normalizedName.toLowerCase() + "%";
        List<NpcEntity> npcs = npcRepo.search(namePattern, zoneId, expansionId, tier);
        return npcs.stream().map(this::entityToDTO).toList();
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }

    // Overrides the inherited entityToDTO -- ModelMapper's MergingCollectionConverter fails
    // when converting NpcEntity.zones (Collection<ZoneEntity>) into NpcDTO.zones
    // (Collection<ZoneDTO>) because each ZoneEntity carries its own nested ExpansionEntity,
    // and the merging converter tries to assign that raw entity straight into
    // ZoneDTO.setExpansion(ExpansionDTO) instead of converting it. Single-object
    // modelMapper.map() calls handle nested conversion fine, so zones/drops are mapped
    // element-by-element here instead of via bulk collection conversion.
    @Override
    protected NpcDTO entityToDTO(NpcEntity entity) {
        NpcDTO dto = new NpcDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setTier(entity.getTier());
        dto.setRandomLootTier(entity.getRandomLootTier());
        dto.setNeedsReview(entity.isNeedsReview());
        dto.setEncounter(entity.isEncounter());
        if (entity.getZones() != null) {
            dto.setZones(entity.getZones().stream()
                    .map(zone -> getModelMapper().map(zone, ZoneDTO.class))
                    .toList());
        }
        if (entity.getDrops() != null) {
            dto.setDrops(entity.getDrops().stream()
                    .map(armor -> getModelMapper().map(armor, ArmorDTO.class))
                    .toList());
        }
        if (entity.getItemDrops() != null) {
            dto.setItemDrops(entity.getItemDrops().stream()
                    .map(item -> getModelMapper().map(item, Item.class))
                    .toList());
        }
        return dto;
    }

    public List<NpcDTO> getByTier(Integer tier) {
        return npcRepo.findByRandomLootTier(tier).stream().map(this::entityToDTO).toList();
    }

    // The raid-import review queue -- npcs auto-created from Raids / "Conqueror of"
    // achievement rows, which need a human to confirm they're real creatures (and not
    // encounter/event step names) before they're trusted.
    public List<NpcDTO> getNpcsNeedingReview() {
        return npcRepo.findByNeedsReviewTrue().stream().map(this::entityToDTO).toList();
    }

    // Explicit id-resolution rather than routing through the generic inherited create()/update()
    // -- a nested zones/drops array in a BaseServiceImpl payload isn't guaranteed to resolve to
    // the existing rows via ModelMapper (same risk flagged for ServerDTO.currentExpansion), so
    // zone/armor ids are looked up and attached here instead.
    @Transactional
    public NpcDTO createNpc(NpcCreateRequest request) {
        NpcEntity npc = applyRequest(new NpcEntity(), request);
        npc = npcRepo.save(npc);
        return entityToDTO(npc);
    }

    // Reuses NpcCreateRequest rather than a separate update DTO, matching ZoneService's
    // create/update split. randomLootTier and tier are only overwritten when the request
    // actually supplies them -- randomLootTier in particular is normally managed via
    // linkSameTier(), so a plain field edit here shouldn't silently null it back out.
    @Transactional
    public NpcDTO updateNpc(Long id, NpcCreateRequest request) {
        NpcEntity npc = npcRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Unknown npc id: " + id));
        npc = applyRequest(npc, request);
        npc = npcRepo.save(npc);
        return entityToDTO(npc);
    }

    private NpcEntity applyRequest(NpcEntity npc, NpcCreateRequest request) {
        npc.setName(request.getName());
        if (request.getTier() != null) {
            npc.setTier(request.getTier());
        }
        if (request.getRandomLootTier() != null) {
            npc.setRandomLootTier(request.getRandomLootTier());
        }
        if (request.getNeedsReview() != null) {
            npc.setNeedsReview(request.getNeedsReview());
        }
        if (request.getEncounter() != null) {
            npc.setEncounter(request.getEncounter());
        }
        if (request.getZoneIds() != null) {
            npc.setZones(new HashSet<>(resolveByIds(zoneRepo, request.getZoneIds(), "zone")));
        }
        if (request.getArmorIds() != null) {
            npc.setDrops(new HashSet<>(resolveByIds(armorRepo, request.getArmorIds(), "armor")));
        }
        if (request.getItemIds() != null) {
            npc.setItemDrops(new HashSet<>(resolveItemsByIds(request.getItemIds())));
        }
        return npc;
    }

    // ItemEntity doesn't implement BaseEntity/ItemRepo doesn't extend BaseRepo yet (see
    // THFItemService TODOs), so it can't go through the generic resolveByIds below.
    private List<ItemEntity> resolveItemsByIds(List<Long> ids) {
        List<ItemEntity> found = itemRepo.findAllById(ids);
        if (found.size() != ids.size()) {
            Set<Long> foundIds = found.stream().map(ItemEntity::getId).collect(Collectors.toSet());
            List<Long> missing = ids.stream().filter(id -> !foundIds.contains(id)).toList();
            throw new IllegalArgumentException("Unknown item id(s): " + missing);
        }
        return found;
    }

    @Transactional
    public NpcDTO addDrop(Long npcId, Long armorId, Long expansionId) {
        return addDrops(npcId, List.of(armorId), expansionId);
    }

    // Linking a drop also backfills the armor's expansion from the npc's zone, since the
    // npc-zone relationship establishes what era the drop belongs to. Only fills a currently
    // null armor.expansion -- existing import data is trusted over this inference. If the npc's
    // zones span more than one expansion, which one applies is ambiguous, so the caller must
    // pass expansionId explicitly rather than have it guessed.
    @Transactional
    public NpcDTO addDrops(Long npcId, List<Long> armorIds, Long expansionId) {
        NpcEntity npc = npcRepo.findById(npcId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown npc id: " + npcId));
        List<ArmorEntity> armorToAdd = resolveByIds(armorRepo, armorIds, "armor");

        Long resolvedExpansionId = expansionId != null ? expansionId : resolveNpcExpansionId(npc);
        ExpansionEntity expansion = resolvedExpansionId != null
                ? expansionRepo.findById(resolvedExpansionId)
                        .orElseThrow(() -> new IllegalArgumentException("Unknown expansion id: " + resolvedExpansionId))
                : null;

        if (npc.getDrops() == null) {
            npc.setDrops(new HashSet<>());
        }
        Set<Long> existingIds = npc.getDrops().stream().map(ArmorEntity::getId).collect(Collectors.toSet());
        for (ArmorEntity armor : armorToAdd) {
            if (!existingIds.contains(armor.getId())) {
                npc.getDrops().add(armor);
            }
            if (armor.getExpansion() == null && expansion != null) {
                armor.setExpansion(expansion);
                armorRepo.save(armor);
            }
        }
        npc = npcRepo.save(npc);
        return entityToDTO(npc);
    }

    // Null if the npc has no zones, or none of its zones have a matched expansion yet -- there's
    // simply nothing to propagate in that case, which isn't an error.
    private Long resolveNpcExpansionId(NpcEntity npc) {
        if (npc.getZones() == null || npc.getZones().isEmpty()) {
            return null;
        }
        Set<Long> expansionIds = npc.getZones().stream()
                .map(ZoneEntity::getExpansion)
                .filter(Objects::nonNull)
                .map(ExpansionEntity::getId)
                .collect(Collectors.toSet());
        if (expansionIds.size() > 1) {
            throw new IllegalArgumentException(
                    "Npc " + npc.getId() + " spans multiple expansions " + expansionIds +
                            " -- specify expansionId explicitly");
        }
        return expansionIds.isEmpty() ? null : expansionIds.iterator().next();
    }

    // Npcs with no resolvable expansion (no zones, or zones not yet matched to one) are
    // ignored rather than blocking the link -- there's nothing to conflict with. Npcs whose
    // expansion is known are compared, and any two known expansions differing rejects the link.
    private void requireCompatibleExpansions(List<NpcEntity> aGroup, List<NpcEntity> bGroup) {
        Set<Long> expansionIds = Stream.concat(aGroup.stream(), bGroup.stream())
                .map(this::resolveNpcExpansionId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (expansionIds.size() > 1) {
            throw new IllegalArgumentException(
                    "Cannot share a loot tier across npcs from different expansions: " + expansionIds);
        }
    }

    // Transitive group merge, not a pairwise tag copy: if npcA and npcB are declared "same
    // tier" and either already belongs to a tier with other members, the whole group merges
    // into one. See conversation for the exact rules per case.
    @Transactional
    public List<NpcDTO> linkSameTier(Long npcAId, Long npcBId) {
        NpcEntity a = npcRepo.findById(npcAId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown npc id: " + npcAId));
        NpcEntity b = npcRepo.findById(npcBId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown npc id: " + npcBId));

        Integer tierA = a.getRandomLootTier();
        Integer tierB = b.getRandomLootTier();

        // Shared loot pools don't cross expansions in practice -- checked across every npc
        // that would land in the merged group, not just a and b, so merging into an existing
        // multi-member tier is validated against the whole group too.
        List<NpcEntity> aGroup = tierA != null ? npcRepo.findByRandomLootTier(tierA) : List.of(a);
        List<NpcEntity> bGroup = tierB != null ? npcRepo.findByRandomLootTier(tierB) : List.of(b);
        requireCompatibleExpansions(aGroup, bGroup);

        Integer targetTier;
        if (tierA != null) {
            targetTier = tierA;
        } else if (tierB != null) {
            targetTier = tierB;
        } else {
            targetTier = npcRepo.findMaxRandomLootTier() + 1;
        }

        if (tierB != null && !tierB.equals(targetTier)) {
            bGroup.forEach(npc -> npc.setRandomLootTier(targetTier));
            npcRepo.saveAll(bGroup);
        } else {
            b.setRandomLootTier(targetTier);
            npcRepo.save(b);
        }
        if (tierA == null) {
            a.setRandomLootTier(targetTier);
            npcRepo.save(a);
        }

        return npcRepo.findByRandomLootTier(targetTier).stream().map(this::entityToDTO).toList();
    }

    // Clears this npc's tier. If that leaves exactly one other npc in the group, that npc's
    // tier is cleared too -- a group of 1 isn't a shared loot pool, so it collapses back to
    // "no tier" rather than lingering as a solo tier number.
    @Transactional
    public NpcDTO unlinkTier(Long npcId) {
        NpcEntity npc = npcRepo.findById(npcId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown npc id: " + npcId));

        Integer tier = npc.getRandomLootTier();
        if (tier == null) {
            return entityToDTO(npc);
        }

        npc.setRandomLootTier(null);
        npc = npcRepo.save(npc);

        List<NpcEntity> remaining = npcRepo.findByRandomLootTier(tier);
        if (remaining.size() == 1) {
            NpcEntity last = remaining.get(0);
            last.setRandomLootTier(null);
            npcRepo.save(last);
        }

        return entityToDTO(npc);
    }

    @Transactional
    public NpcDTO removeDrop(Long npcId, Long armorId) {
        NpcEntity npc = npcRepo.findById(npcId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown npc id: " + npcId));

        if (npc.getDrops() != null) {
            npc.getDrops().removeIf(a -> a.getId().equals(armorId));
            npc = npcRepo.save(npc);
        }
        return entityToDTO(npc);
    }

    @Transactional
    public NpcDTO addItemDrop(Long npcId, Long itemId) {
        return addItemDrops(npcId, List.of(itemId));
    }

    @Transactional
    public NpcDTO addItemDrops(Long npcId, List<Long> itemIds) {
        NpcEntity npc = npcRepo.findById(npcId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown npc id: " + npcId));
        List<ItemEntity> itemsToAdd = resolveItemsByIds(itemIds);

        if (npc.getItemDrops() == null) {
            npc.setItemDrops(new HashSet<>());
        }
        Set<Long> existingIds = npc.getItemDrops().stream().map(ItemEntity::getId).collect(Collectors.toSet());
        for (ItemEntity item : itemsToAdd) {
            if (!existingIds.contains(item.getId())) {
                npc.getItemDrops().add(item);
            }
        }
        npc = npcRepo.save(npc);
        return entityToDTO(npc);
    }

    @Transactional
    public NpcDTO removeItemDrop(Long npcId, Long itemId) {
        NpcEntity npc = npcRepo.findById(npcId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown npc id: " + npcId));

        if (npc.getItemDrops() != null) {
            npc.getItemDrops().removeIf(i -> i.getId().equals(itemId));
            npc = npcRepo.save(npc);
        }
        return entityToDTO(npc);
    }

    private <T extends BaseEntity> List<T> resolveByIds(BaseRepo<T, Long> repo, List<Long> ids, String label) {
        List<T> found = repo.findAllById(ids);
        if (found.size() != ids.size()) {
            Set<Long> foundIds = found.stream().map(BaseEntity::getId).collect(Collectors.toSet());
            List<Long> missing = ids.stream().filter(id -> !foundIds.contains(id)).toList();
            throw new IllegalArgumentException("Unknown " + label + " id(s): " + missing);
        }
        return found;
    }
}
