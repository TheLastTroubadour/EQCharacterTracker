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
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestTemplateService extends BaseServiceImpl<QuestTemplateEntity, QuestTemplateDTO> {

    private final QuestTemplateRepo questTemplateRepo;
    private final ExpansionRepo expansionRepo;

    public QuestTemplateService(ModelMapper modelMapper, QuestTemplateRepo questTemplateRepo, ExpansionRepo expansionRepo) {
        super(modelMapper, questTemplateRepo);
        this.questTemplateRepo = questTemplateRepo;
        this.expansionRepo = expansionRepo;
    }

    @Transactional
    public QuestTemplateEntity importTemplate(QuestTemplateRequest request) {
        QuestTemplateEntity template = questTemplateRepo
                .findByNameAndServerTypeAndCharacterClass(request.getName(), request.getServerType(), request.getCharacterClass())
                .orElse(new QuestTemplateEntity());

        template.setName(request.getName());
        template.setServerType(request.getServerType());
        template.setCharacterClass(request.getCharacterClass());
        template.setOrdered(request.isOrdered());
        template.setProgressionQuest(request.isProgressionQuest());

        if (request.getExpansionShortName() != null) {
            ExpansionEntity expansion = expansionRepo
                    .findByShortNameAndServerType(request.getExpansionShortName(), request.getServerType())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Expansion not found: " + request.getExpansionShortName()));
            template.setExpansion(expansion);
        }

        template.getSubQuests().clear();

        if (request.getSubQuests() != null) {
            for (SubQuestTemplateRequest sq : request.getSubQuests()) {
                SubQuestTemplateEntity subQuest = new SubQuestTemplateEntity();
                subQuest.setName(sq.getName());
                subQuest.setOrderIndex(sq.getOrder());
                subQuest.setCanSkip(sq.isCanSkip());
                subQuest.setQuestTemplate(template);

                if (sq.getRequirements() != null) {
                    for (RequirementTemplateRequest req : sq.getRequirements()) {
                        RequirementTemplateEntity requirement = new RequirementTemplateEntity();
                        requirement.setType(req.getType());
                        requirement.setDescription(req.getDescription());
                        requirement.setExternalItemId(req.getExternalItemId());
                        requirement.setSubQuestTemplate(subQuest);
                        subQuest.getRequirements().add(requirement);
                    }
                }
                template.getSubQuests().add(subQuest);
            }
        }

        return questTemplateRepo.save(template);
    }

    @Transactional
    public List<QuestTemplateDTO> importTemplates(List<QuestTemplateRequest> requests) {
        return requests.stream().map(this::importTemplate).map(this::entityToDTO).toList();
    }

    public List<QuestTemplateDTO> getTemplates(ServerType serverType, CharacterClass characterClass) {
        List<QuestTemplateEntity> templates;
        if (serverType != null && characterClass != null) {
            templates = questTemplateRepo.findByServerTypeAndCharacterClass(serverType, characterClass);
        } else if (serverType != null) {
            templates = questTemplateRepo.findByServerType(serverType);
        } else {
            templates = questTemplateRepo.findAll();
        }
        return templates.stream().map(this::entityToDTO).toList();
    }

    public QuestTemplateEntity getTemplate(Long id) {
        return questTemplateRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quest template not found: " + id));
    }
}
