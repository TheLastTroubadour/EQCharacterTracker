package com.eq.charactertracker.service;

import com.eq.charactertracker.base.BaseServiceImpl;
import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.ServerType;
import com.eq.charactertracker.entity.quest.ExpansionEntity;
import com.eq.charactertracker.entity.quest.QuestTemplateEntity;
import com.eq.charactertracker.entity.quest.RequirementTemplateEntity;
import com.eq.charactertracker.entity.quest.SubQuestTemplateEntity;
import com.eq.charactertracker.model.quest.QuestTemplateDTO;
import com.eq.charactertracker.model.quest.QuestTemplateRequest;
import com.eq.charactertracker.model.quest.RequirementTemplateRequest;
import com.eq.charactertracker.model.quest.SubQuestTemplateRequest;
import com.eq.charactertracker.repo.quest.ExpansionRepo;
import com.eq.charactertracker.repo.quest.QuestTemplateRepo;
import com.eq.charactertracker.repo.quest.RequirementProgressRepo;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuestTemplateService extends BaseServiceImpl<QuestTemplateEntity, QuestTemplateDTO> {

    private final QuestTemplateRepo questTemplateRepo;
    private final ExpansionRepo expansionRepo;
    private final RequirementProgressRepo requirementProgressRepo;

    public QuestTemplateService(ModelMapper modelMapper, QuestTemplateRepo questTemplateRepo, ExpansionRepo expansionRepo,
                                 RequirementProgressRepo requirementProgressRepo) {
        super(modelMapper, questTemplateRepo);
        this.questTemplateRepo = questTemplateRepo;
        this.expansionRepo = expansionRepo;
        this.requirementProgressRepo = requirementProgressRepo;
    }

    @Transactional
    public QuestTemplateEntity importTemplate(QuestTemplateRequest request) {
        QuestTemplateEntity template = questTemplateRepo
                .findByNameAndServerTypeAndCharacterClass(request.getName(), request.getServerType(), request.getCharacterClass())
                .orElse(new QuestTemplateEntity());

        return applyRequest(template, request);
    }

    // PUT-by-id counterpart to importTemplate: looks the template up by id instead of
    // by (name, serverType, characterClass), so it can also rename a quest -- import
    // can't, since name is part of how it finds the row to update.
    @Transactional
    public QuestTemplateDTO updateTemplate(Long id, QuestTemplateRequest request) {
        QuestTemplateEntity template = questTemplateRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quest template not found: " + id));

        return entityToDTO(applyRequest(template, request));
    }

    private QuestTemplateEntity applyRequest(QuestTemplateEntity template, QuestTemplateRequest request) {
        template.setName(request.getName());
        template.setServerType(request.getServerType());
        template.setCharacterClass(request.getCharacterClass());
        template.setOrdered(request.isOrdered());
        template.setQuestType(request.getQuestType());

        if (request.getExpansionShortName() != null) {
            ExpansionEntity expansion = expansionRepo
                    .findByShortNameAndServerType(request.getExpansionShortName(), request.getServerType())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Expansion not found: " + request.getExpansionShortName()));
            template.setExpansion(expansion);
        }

        // Upsert in place (matched by name for subquests, by type+description for
        // requirements) instead of wipe-and-rebuild -- characters may already have
        // progress pointing at these rows, and there's no DB cascade from
        // requirement_progress to a deleted requirement_template, so a naive
        // clear-and-recreate would either orphan progress or fail on the FK.
        // This also means edits to an existing template correctly show up for every
        // character already tracking it: added subquests/requirements appear as new
        // (not-yet-completed) steps, removed ones take their progress with them, and
        // in-place field edits (e.g. a description tweak) are just visible immediately
        // since the response is always built by reading the live template.
        upsertSubQuests(template, request.getSubQuests() != null ? request.getSubQuests() : List.of());

        return questTemplateRepo.save(template);
    }

    private void upsertSubQuests(QuestTemplateEntity template, List<SubQuestTemplateRequest> requests) {
        Map<String, SubQuestTemplateEntity> existingByName = template.getSubQuests().stream()
                .collect(Collectors.toMap(SubQuestTemplateEntity::getName, sq -> sq));

        Set<String> incomingNames = requests.stream().map(SubQuestTemplateRequest::getName).collect(Collectors.toSet());

        List<SubQuestTemplateEntity> toRemove = template.getSubQuests().stream()
                .filter(sq -> !incomingNames.contains(sq.getName()))
                .toList();
        for (SubQuestTemplateEntity subQuest : toRemove) {
            for (RequirementTemplateEntity requirement : subQuest.getRequirements()) {
                requirementProgressRepo.deleteByRequirementTemplate_Id(requirement.getId());
            }
        }
        template.getSubQuests().removeAll(toRemove);

        for (SubQuestTemplateRequest sqRequest : requests) {
            SubQuestTemplateEntity subQuest = existingByName.get(sqRequest.getName());
            if (subQuest == null) {
                subQuest = new SubQuestTemplateEntity();
                subQuest.setName(sqRequest.getName());
                subQuest.setQuestTemplate(template);
                template.getSubQuests().add(subQuest);
            }
            subQuest.setOrderIndex(sqRequest.getOrder());
            subQuest.setCanSkip(sqRequest.isCanSkip());

            upsertRequirements(subQuest, sqRequest.getRequirements() != null ? sqRequest.getRequirements() : List.of());
        }
    }

    private void upsertRequirements(SubQuestTemplateEntity subQuest, List<RequirementTemplateRequest> requests) {
        Map<String, RequirementTemplateEntity> existingByKey = subQuest.getRequirements().stream()
                .collect(Collectors.toMap(this::requirementKey, r -> r));

        Set<String> incomingKeys = requests.stream().map(this::requirementKey).collect(Collectors.toSet());

        List<RequirementTemplateEntity> toRemove = subQuest.getRequirements().stream()
                .filter(r -> !incomingKeys.contains(requirementKey(r)))
                .toList();
        for (RequirementTemplateEntity requirement : toRemove) {
            requirementProgressRepo.deleteByRequirementTemplate_Id(requirement.getId());
        }
        subQuest.getRequirements().removeAll(toRemove);

        for (RequirementTemplateRequest reqRequest : requests) {
            RequirementTemplateEntity requirement = existingByKey.get(requirementKey(reqRequest));
            if (requirement == null) {
                requirement = new RequirementTemplateEntity();
                requirement.setSubQuestTemplate(subQuest);
                subQuest.getRequirements().add(requirement);
            }
            requirement.setType(reqRequest.getType());
            requirement.setDescription(reqRequest.getDescription());
            requirement.setExternalItemId(reqRequest.getExternalItemId());
            requirement.setPhrase(reqRequest.getPhrase());
        }
    }

    private String requirementKey(RequirementTemplateEntity requirement) {
        return requirement.getType() + "|" + requirement.getDescription();
    }

    private String requirementKey(RequirementTemplateRequest requirement) {
        return requirement.getType() + "|" + requirement.getDescription();
    }

    @Transactional
    public List<QuestTemplateDTO> importTemplates(List<QuestTemplateRequest> requests) {
        return requests.stream().map(this::importTemplate).map(this::entityToDTO).toList();
    }

    public List<QuestTemplateDTO> getTemplates(ServerType serverType, CharacterClass characterClass, String name) {
        List<QuestTemplateEntity> templates;
        if (serverType != null && characterClass != null) {
            // characterClass IS NULL means "applies to every class" (PROGRESSION quests
            // are never class-scoped) -- include those alongside the exact-class matches
            // (EPIC and any class-specific content) so browsing by class doesn't hide them.
            templates = new ArrayList<>(questTemplateRepo.findByServerTypeAndCharacterClass(serverType, characterClass));
            templates.addAll(questTemplateRepo.findByServerTypeAndCharacterClassIsNull(serverType));
        } else if (serverType != null) {
            templates = questTemplateRepo.findByServerType(serverType);
        } else {
            templates = questTemplateRepo.findAll();
        }

        if (name != null && !name.isBlank()) {
            String needle = name.toLowerCase();
            templates = templates.stream()
                    .filter(t -> t.getName().toLowerCase().contains(needle))
                    .toList();
        }

        return templates.stream().map(this::entityToDTO).toList();
    }

    public QuestTemplateEntity getTemplate(Long id) {
        return questTemplateRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quest template not found: " + id));
    }
}
