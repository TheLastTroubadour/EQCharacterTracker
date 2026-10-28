package com.eq.charactertracker.model;

import com.eq.charactertracker.base.BaseAttributes;
import com.eq.charactertracker.entity.AugmentationEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Augmentation extends BaseAttributes {
    private Long id;
    private Long extId;
    private String server;
    private Short augType;

    public Augmentation(AugmentationEntity entity) {
        this.id = entity.getId();
        this.extId = entity.getExtId();
        this.server = entity.getServer() != null ? entity.getServer().getShortName() : null;
        this.augType = entity.getAugType();
        this.setName(entity.getName());
        this.setAc(entity.getAc());
        this.setStrength(entity.getStrength());
        this.setStamina(entity.getStamina());
        this.setAgility(entity.getAgility());
        this.setDexterity(entity.getDexterity());
        this.setWisdom(entity.getWisdom());
        this.setIntelligence(entity.getIntelligence());
        this.setCharisma(entity.getCharisma());
        this.setHitPoints(entity.getHitPoints());
        this.setManaPoints(entity.getManaPoints());
        this.setEndurancePoints(entity.getEndurancePoints());
        this.setFireResist(entity.getFireResist());
        this.setColdResist(entity.getColdResist());
        this.setMagicResist(entity.getMagicResist());
        this.setDiseaseResist(entity.getDiseaseResist());
        this.setPoisonResist(entity.getPoisonResist());
        this.setAttack(entity.getAttack());
        this.setAccuracy(entity.getAccuracy());
        this.setAvoidance(entity.getAvoidance());
        this.setCombatEffect(entity.getCombatEffect());
        this.setStunResist(entity.getStunResist());
        this.setDamageShield(entity.getDamageShield());
        this.setDotShielding(entity.getDotShielding());
        this.setEnduranceRegen(entity.getEnduranceRegen());
        this.setHitPointsRegen(entity.getHitPointsRegen());
        this.setManaPointsRegen(entity.getManaPointsRegen());
        this.setSpellShielding(entity.getSpellShielding());
        this.setShielding(entity.getShielding());
        this.setStrikethrough(entity.getStrikethrough());
        this.setFocusEffect(entity.getFocusEffect());
        this.setClickEffect(entity.getClickEffect());
        this.setHeroicStrength(entity.getHeroicStrength());
        this.setHeroicStamina(entity.getHeroicStamina());
        this.setHeroicAgility(entity.getHeroicAgility());
        this.setHeroicDexterity(entity.getHeroicDexterity());
        this.setHeroicWisdom(entity.getHeroicWisdom());
        this.setHeroicIntelligence(entity.getHeroicIntelligence());
        this.setHeroicCharisma(entity.getHeroicCharisma());
    }
}
