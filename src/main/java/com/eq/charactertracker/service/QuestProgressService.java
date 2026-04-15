package com.eq.charactertracker.service;

import com.eq.charactertracker.entity.CharacterEntity;
import com.eq.charactertracker.entity.quest.CharacterQuestProgressEntity;
import com.eq.charactertracker.entity.quest.QuestTemplateEntity;
import com.eq.charactertracker.entity.quest.RequirementProgressEntity;
import com.eq.charactertracker.entity.quest.RequirementTemplateEntity;
import com.eq.charactertracker.entity.quest.SubQuestTemplateEntity;
import com.eq.charactertracker.model.quest.CharacterQuestProgressResponse;
import com.eq.charactertracker.model.quest.RequirementProgressResponse;
import com.eq.charactertracker.model.quest.SubQuestProgressResponse;
import com.eq.charactertracker.repo.CharacterRepo;
import com.eq.charactertracker.repo.quest.CharacterQuestProgressRepo;
import com.eq.charactertracker.repo.quest.QuestTemplateRepo;
import com.eq.charactertracker.repo.quest.RequirementProgressRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestProgressService {

    private final CharacterQuestProgressRepo characterQuestProgressRepo;
    private final RequirementProgressRepo requirementProgressRepo;
    private final QuestTemplateRepo questTemplateRepo;
    private final CharacterRepo characterRepo;

    @Transactional
    public CharacterQuestProgressResponse startQuest(Long characterId, Long questTemplateId) {
        if (characterQuestProgressRepo.findByCharacter_IdAndQuestTemplate_Id(characterId, questTemplateId).isPresent()) {
            throw new IllegalArgumentException("Quest already started for this character");
        }

        CharacterEntity character = characterRepo.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Character not found: " + characterId));

        QuestTemplateEntity template = questTemplateRepo.findById(questTemplateId)
                .orElseThrow(() -> new IllegalArgumentException("Quest template not found: " + questTemplateId));

        CharacterQuestProgressEntity progress = new CharacterQuestProgressEntity();
        progress.setCharacter(character);
        progress.setQuestTemplate(template);

        for (SubQuestTemplateEntity subQuest : template.getSubQuests()) {
            for (RequirementTemplateEntity requirement : subQuest.getRequirements()) {
                RequirementProgressEntity reqProgress = new RequirementProgressEntity();
                reqProgress.setCharacterQuestProgress(progress);
                reqProgress.setRequirementTemplate(requirement);
                reqProgress.setCompleted(false);
                progress.getRequirementProgress().add(reqProgress);
            }
        }

        return toResponse(characterQuestProgressRepo.save(progress));
    }

    @Transactional
    public RequirementProgressResponse toggleRequirement(Long progressId, Long requirementProgressId) {
        RequirementProgressEntity reqProgress = requirementProgressRepo.findById(requirementProgressId)
                .orElseThrow(() -> new IllegalArgumentException("Requirement progress not found: " + requirementProgressId));

        if (!reqProgress.getCharacterQuestProgress().getId().equals(progressId)) {
            throw new IllegalArgumentException("Requirement does not belong to this quest progress");
        }

        reqProgress.setCompleted(!reqProgress.isCompleted());
        reqProgress = requirementProgressRepo.save(reqProgress);

        RequirementProgressResponse response = new RequirementProgressResponse();
        response.setRequirementProgressId(reqProgress.getId());
        response.setRequirementTemplateId(reqProgress.getRequirementTemplate().getId());
        response.setType(reqProgress.getRequirementTemplate().getType());
        response.setDescription(reqProgress.getRequirementTemplate().getDescription());
        response.setExternalItemId(reqProgress.getRequirementTemplate().getExternalItemId());
        response.setCompleted(reqProgress.isCompleted());
        return response;
    }

    public List<CharacterQuestProgressResponse> getQuestProgressForCharacter(Long characterId) {
        return characterQuestProgressRepo.findByCharacter_Id(characterId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private CharacterQuestProgressResponse toResponse(CharacterQuestProgressEntity progress) {
        Map<Long, Boolean> completionByRequirementTemplateId = progress.getRequirementProgress().stream()
                .collect(Collectors.toMap(
                        rp -> rp.getRequirementTemplate().getId(),
                        RequirementProgressEntity::isCompleted
                ));

        Map<Long, Long> progressIdByRequirementTemplateId = progress.getRequirementProgress().stream()
                .collect(Collectors.toMap(
                        rp -> rp.getRequirementTemplate().getId(),
                        RequirementProgressEntity::getId
                ));

        List<SubQuestProgressResponse> subQuestResponses = progress.getQuestTemplate().getSubQuests().stream()
                .map(sq -> {
                    List<RequirementProgressResponse> reqResponses = sq.getRequirements().stream()
                            .map(req -> {
                                RequirementProgressResponse r = new RequirementProgressResponse();
                                r.setRequirementTemplateId(req.getId());
                                r.setRequirementProgressId(progressIdByRequirementTemplateId.get(req.getId()));
                                r.setType(req.getType());
                                r.setDescription(req.getDescription());
                                r.setExternalItemId(req.getExternalItemId());
                                r.setCompleted(completionByRequirementTemplateId.getOrDefault(req.getId(), false));
                                return r;
                            }).toList();

                    SubQuestProgressResponse sqr = new SubQuestProgressResponse();
                    sqr.setSubQuestTemplateId(sq.getId());
                    sqr.setName(sq.getName());
                    sqr.setOrderIndex(sq.getOrderIndex());
                    sqr.setCanSkip(sq.isCanSkip());
                    sqr.setCompleted(reqResponses.stream().allMatch(RequirementProgressResponse::isCompleted));
                    sqr.setRequirements(reqResponses);
                    return sqr;
                }).toList();

        CharacterQuestProgressResponse response = new CharacterQuestProgressResponse();
        response.setProgressId(progress.getId());
        response.setQuestName(progress.getQuestTemplate().getName());
        response.setOrdered(progress.getQuestTemplate().isOrdered());
        response.setCompleted(subQuestResponses.stream().allMatch(SubQuestProgressResponse::isCompleted));
        response.setSubQuests(subQuestResponses);
        return response;
    }
}
