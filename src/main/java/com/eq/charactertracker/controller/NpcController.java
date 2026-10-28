package com.eq.charactertracker.controller;

import com.eq.charactertracker.constants.NpcTier;
import com.eq.charactertracker.model.NpcCreateRequest;
import com.eq.charactertracker.model.NpcDTO;
import com.eq.charactertracker.model.imports.HunterImportResult;
import com.eq.charactertracker.service.HunterImportService;
import com.eq.charactertracker.service.NpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("npc")
public class NpcController {

    private final HunterImportService hunterImportService;
    private final NpcService npcService;

    @PostMapping("/import-from-hunter-file")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public HunterImportResult importFromHunterFile(@RequestParam("filePath") String filePath) {
        return hunterImportService.importFromFile(filePath);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public NpcDTO createNpc(@RequestBody NpcCreateRequest request) {
        return npcService.createNpc(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<NpcDTO> search(@RequestParam(required = false) String name,
                                @RequestParam(required = false) Long zoneId,
                                @RequestParam(required = false) Long expansionId,
                                @RequestParam(required = false) NpcTier tier) {
        return npcService.search(name, zoneId, expansionId, tier);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public NpcDTO getById(@PathVariable Long id) {
        return npcService.getById(id);
    }

    @GetMapping("/needs-review")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public List<NpcDTO> getNpcsNeedingReview() {
        return npcService.getNpcsNeedingReview();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public NpcDTO updateNpc(@PathVariable Long id, @RequestBody NpcCreateRequest request) {
        return npcService.updateNpc(id, request);
    }

    @GetMapping("/tier/{tier}")
    @ResponseStatus(HttpStatus.OK)
    public List<NpcDTO> getByTier(@PathVariable Integer tier) {
        return npcService.getByTier(tier);
    }

    @PostMapping("/{npcId}/drops/{armorId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public NpcDTO addDrop(@PathVariable Long npcId, @PathVariable Long armorId,
                           @RequestParam(required = false) Long expansionId) {
        return npcService.addDrop(npcId, armorId, expansionId);
    }

    @PostMapping("/{npcId}/drops")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public NpcDTO addDrops(@PathVariable Long npcId, @RequestBody List<Long> armorIds,
                            @RequestParam(required = false) Long expansionId) {
        return npcService.addDrops(npcId, armorIds, expansionId);
    }

    @DeleteMapping("/{npcId}/tier")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public NpcDTO unlinkTier(@PathVariable Long npcId) {
        return npcService.unlinkTier(npcId);
    }

    @DeleteMapping("/{npcId}/drops/{armorId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public NpcDTO removeDrop(@PathVariable Long npcId, @PathVariable Long armorId) {
        return npcService.removeDrop(npcId, armorId);
    }

    @PostMapping("/{npcId}/item-drops/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public NpcDTO addItemDrop(@PathVariable Long npcId, @PathVariable Long itemId) {
        return npcService.addItemDrop(npcId, itemId);
    }

    @PostMapping("/{npcId}/item-drops")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public NpcDTO addItemDrops(@PathVariable Long npcId, @RequestBody List<Long> itemIds) {
        return npcService.addItemDrops(npcId, itemIds);
    }

    @DeleteMapping("/{npcId}/item-drops/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public NpcDTO removeItemDrop(@PathVariable Long npcId, @PathVariable Long itemId) {
        return npcService.removeItemDrop(npcId, itemId);
    }

    @PostMapping("/{npcAId}/link-tier/{npcBId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public List<NpcDTO> linkSameTier(@PathVariable Long npcAId, @PathVariable Long npcBId) {
        return npcService.linkSameTier(npcAId, npcBId);
    }
}
