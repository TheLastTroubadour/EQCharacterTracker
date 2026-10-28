package com.eq.charactertracker.service;

import com.eq.charactertracker.constants.QuestType;
import com.eq.charactertracker.entity.CharacterEntity;
import com.eq.charactertracker.entity.quest.CharacterQuestProgressEntity;
import com.eq.charactertracker.entity.quest.QuestTemplateEntity;
import com.eq.charactertracker.entity.quest.RequirementProgressEntity;
import com.eq.charactertracker.entity.quest.RequirementTemplateEntity;
import com.eq.charactertracker.entity.quest.SubQuestTemplateEntity;
import com.eq.charactertracker.model.CharacterDTO;
import com.eq.charactertracker.model.quest.CharacterQuestProgressResponse;
import com.eq.charactertracker.model.quest.QuestCompletionResponse;
import com.eq.charactertracker.model.quest.RequirementProgressResponse;
import com.eq.charactertracker.model.quest.SubQuestProgressResponse;
import com.eq.charactertracker.model.quest.TeamQuestProgressResponse;
import com.eq.charactertracker.repo.quest.CharacterQuestProgressRepo;
import com.eq.charactertracker.repo.quest.QuestTemplateRepo;
import com.eq.charactertracker.repo.quest.RequirementProgressRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// TODO: rename *Response classes to *DTO and consider wiring to BaseServiceImpl pattern
@Service
@RequiredArgsConstructor
public class QuestProgressService {

    private final ModelMapper modelMapper;
    private final CharacterQuestProgressRepo characterQuestProgressRepo;
    private final RequirementProgressRepo requirementProgressRepo;
    // TODO: route through QuestTemplateService instead of calling QuestTemplateRepo directly
    private final QuestTemplateRepo questTemplateRepo;
    private final CharacterService characterService;

    @Transactional
    public CharacterQuestProgressResponse startQuest(Long characterId, Long questTemplateId) {
        if (characterQuestProgressRepo.findByCharacter_IdAndQuestTemplate_Id(characterId, questTemplateId).isPresent()) {
            throw new IllegalArgumentException("Quest already started for this character");
        }

        CharacterDTO character = characterService.getCharacterById(characterId);

        QuestTemplateEntity template = questTemplateRepo.findById(questTemplateId)
                .orElseThrow(() -> new IllegalArgumentException("Quest template not found: " + questTemplateId));

        return toResponse(createProgress(character, template));
    }

    // PROGRESSION quests apply to every class -- they're stored with a null
    // characterClass, not scoped to one, unlike EPIC which is always class-specific.
    // Both auto-start for every matching character/server -- no manual "start quest"
    // step needed, mirroring how spells are seeded on character creation. Idempotent:
    // for a template the character hasn't started, creates the whole progress record;
    // for one they already have, tops up any requirements added to the template since
    // they started (so a step added after the fact is actually toggleable, not just
    // visible-but-unchecked).
    @Transactional
    public void seedQuestsForCharacter(CharacterDTO character) {
        List<QuestTemplateEntity> templates = new ArrayList<>();
        templates.addAll(questTemplateRepo.findByServerTypeAndQuestTypeAndCharacterClassIsNull(
                character.getServer().getType(), QuestType.PROGRESSION));
        templates.addAll(questTemplateRepo.findByServerTypeAndCharacterClassAndQuestType(
                character.getServer().getType(), character.getCharacterClass(), QuestType.EPIC));

        for (QuestTemplateEntity template : templates) {
            characterQuestProgressRepo.findByCharacter_IdAndQuestTemplate_Id(character.getId(), template.getId())
                    .ifPresentOrElse(
                            progress -> fillMissingRequirementProgress(progress, template),
                            () -> createProgress(character, template));
        }
    }

    private void fillMissingRequirementProgress(CharacterQuestProgressEntity progress, QuestTemplateEntity template) {
        Set<Long> existingRequirementTemplateIds = progress.getRequirementProgress().stream()
                .map(rp -> rp.getRequirementTemplate().getId())
                .collect(Collectors.toSet());

        boolean added = false;
        for (SubQuestTemplateEntity subQuest : template.getSubQuests()) {
            for (RequirementTemplateEntity requirement : subQuest.getRequirements()) {
                if (existingRequirementTemplateIds.contains(requirement.getId())) {
                    continue;
                }
                RequirementProgressEntity reqProgress = new RequirementProgressEntity();
                reqProgress.setCharacterQuestProgress(progress);
                reqProgress.setRequirementTemplate(requirement);
                reqProgress.setCompleted(false);
                progress.getRequirementProgress().add(reqProgress);
                added = true;
            }
        }
        if (added) {
            characterQuestProgressRepo.save(progress);
        }
    }

    private CharacterQuestProgressEntity createProgress(CharacterDTO character, QuestTemplateEntity template) {
        CharacterQuestProgressEntity progress = new CharacterQuestProgressEntity();
        progress.setCharacter(modelMapper.map(character, CharacterEntity.class));
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

        return characterQuestProgressRepo.save(progress);
    }

    // Bulk-sets every requirement under a quest (all subquests included) to the same
    // completed state in one call, e.g. for "I already did this before I started
    // tracking it" -- pass completed=false to reset the whole quest instead.
    @Transactional
    public CharacterQuestProgressResponse setQuestCompletion(Long progressId, boolean completed) {
        CharacterQuestProgressEntity progress = characterQuestProgressRepo.findById(progressId)
                .orElseThrow(() -> new IllegalArgumentException("Quest progress not found: " + progressId));

        for (RequirementProgressEntity reqProgress : progress.getRequirementProgress()) {
            reqProgress.setCompleted(completed);
        }
        progress = characterQuestProgressRepo.save(progress);

        return toResponse(progress);
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
        response.setPhrase(reqProgress.getRequirementTemplate().getPhrase());
        response.setCompleted(reqProgress.isCompleted());
        return response;
    }

    // Self-heals on every read: catches up a character whose progress is missing or
    // stale (never seeded, or a PROGRESSION/EPIC template was added after they were
    // created) before returning anything. Idempotent, so calling it from every read
    // path below is safe -- no separate backfill script needed.
    public List<CharacterQuestProgressResponse> getQuestProgressForCharacter(Long characterId) {
        CharacterDTO character = characterService.getCharacterById(characterId);
        seedQuestsForCharacter(character);

        return characterQuestProgressRepo.findByCharacter_Id(characterId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // A class can have more than one EPIC-type template (e.g. Epic 1.5 and Epic 2.0 are
    // separate quest chains) -- "completed" means all of them are done for that class,
    // not just one. Not started and in-progress both read as "not completed".
    public boolean isEpicQuestCompleted(Long characterId) {
        CharacterDTO character = characterService.getCharacterById(characterId);
        seedQuestsForCharacter(character);

        List<QuestTemplateEntity> epicTemplates = questTemplateRepo.findByServerTypeAndCharacterClassAndQuestType(
                character.getServer().getType(), character.getCharacterClass(), QuestType.EPIC);

        if (epicTemplates.isEmpty()) {
            return false;
        }

        return epicTemplates.stream().allMatch(template ->
                characterQuestProgressRepo.findByCharacter_IdAndQuestTemplate_Id(characterId, template.getId())
                        .map(progress -> toResponse(progress).isCompleted())
                        .orElse(false));
    }

    public List<TeamQuestProgressResponse> getQuestProgressForUser(Long userId) {
        return characterService.getCharactersByUserId(userId).stream()
                .map(character -> {
                    seedQuestsForCharacter(character);

                    TeamQuestProgressResponse teamResponse = new TeamQuestProgressResponse();
                    teamResponse.setCharacterId(character.getId());
                    teamResponse.setCharacterName(character.getName());
                    teamResponse.setCharacterClass(character.getCharacterClass());
                    teamResponse.setEpicQuestCompleted(isEpicQuestCompleted(character.getId()));
                    teamResponse.setQuests(getQuestCompletionForCharacter(character.getId()));
                    return teamResponse;
                }).toList();
    }

    private List<QuestCompletionResponse> getQuestCompletionForCharacter(Long characterId) {
        return characterQuestProgressRepo.findByCharacter_Id(characterId).stream()
                .map(progress -> {
                    CharacterQuestProgressResponse full = toResponse(progress);

                    QuestCompletionResponse completion = new QuestCompletionResponse();
                    completion.setProgressId(progress.getId());
                    completion.setQuestTemplateId(progress.getQuestTemplate().getId());
                    completion.setQuestName(progress.getQuestTemplate().getName());
                    completion.setQuestType(progress.getQuestTemplate().getQuestType());
                    completion.setCompleted(full.isCompleted());
                    completion.setSubQuests(full.getSubQuests());
                    return completion;
                }).toList();
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
                                r.setPhrase(req.getPhrase());
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
