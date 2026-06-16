package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.actor.Skill;
import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.combat.DamageType;
import com.github.finley243.adventureengine.combat.AttackType;
import com.github.finley243.adventureengine.combat.WeaponClass;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.AreaLink;
import org.w3c.dom.Element;

import java.util.*;

public class CombatTypeLoader {

    private static final String NAME_DAMAGE_TYPE = "damageType";
    private static final String NAME_WEAPON_CLASS = "weaponClass";
    private static final String NAME_ATTACK_TYPE = "attackType";

    private static final String NAME_DAMAGE_TYPE_ID = "id";

    private static final String NAME_WEAPON_CLASS_ID = "id";
    private static final String NAME_WEAPON_CLASS_NAME = "name";
    private static final String NAME_WEAPON_CLASS_IS_RANGED = "isRanged";
    private static final String NAME_WEAPON_CLASS_IS_LOUD = "isLoud";
    private static final String NAME_WEAPON_CLASS_SKILL = "skill";
    private static final String NAME_WEAPON_CLASS_RANGE = "range";
    private static final String NAME_WEAPON_CLASS_ATTACK_TYPE = "attackType";

    private static final String NAME_ATTACK_TYPE_ID = "id";
    private static final String NAME_ATTACK_TYPE_CATEGORY = "category";
    private static final String NAME_ATTACK_TYPE_PROMPT = "prompt";
    private static final String NAME_ATTACK_TYPE_PHRASE = "attackPhrase";
    private static final String NAME_ATTACK_TYPE_PHRASE_OVERALL = "attackOverallPhrase";
    private static final String NAME_ATTACK_TYPE_PHRASE_AUDIBLE = "attackPhraseAudible";
    private static final String NAME_ATTACK_TYPE_PHRASE_AUDIBLE_OVERALL = "attackOverallPhraseAudible";
    private static final String NAME_ATTACK_TYPE_AMMO_CONSUMED = "ammoConsumed";
    private static final String NAME_ATTACK_TYPE_ACTION_POINTS = "actionPoints";
    private static final String NAME_ATTACK_TYPE_CONSUME_TYPE = "weaponConsumeType";
    private static final String NAME_ATTACK_TYPE_NONIDEAL_RANGE = "nonIdealRange";
    private static final String NAME_ATTACK_TYPE_RANGE = "range";
    private static final String NAME_ATTACK_TYPE_RATE = "rate";
    private static final String NAME_ATTACK_TYPE_DAMAGE = "damage";
    private static final String NAME_ATTACK_TYPE_DAMAGE_MULT = "damageMult";
    private static final String NAME_ATTACK_TYPE_DAMAGE_TYPE = "damageType";
    private static final String NAME_ATTACK_TYPE_ARMOR_MULT = "armorMult";
    private static final String NAME_ATTACK_TYPE_TARGET_EFFECT = "targetEffect";
    private static final String NAME_ATTACK_TYPE_OVERRIDE_EFFECTS = "overrideEffects";
    private static final String NAME_ATTACK_TYPE_HIT_CHANCE = "hitChance";
    private static final String NAME_ATTACK_TYPE_HIT_CHANCE_OVERALL = "hitChanceOverall";
    private static final String NAME_ATTACK_TYPE_HIT_CHANCE_MULT = "hitChanceMult";
    private static final String NAME_ATTACK_TYPE_IS_LOUD_OVERRIDE = "isLoudOverride";

    private static final boolean DEFAULT_WEAPON_CLASS_IS_RANGED = false;
    private static final boolean DEFAULT_WEAPON_CLASS_IS_LOUD = false;

    private static final AttackType.AttackCategory DEFAULT_ATTACK_TYPE_CATEGORY = AttackType.AttackCategory.SINGLE;
    private static final int DEFAULT_ATTACK_TYPE_AMMO_CONSUMED = 1;
    private static final int DEFAULT_ATTACK_TYPE_ACTION_POINTS = 1;
    private static final AttackType.WeaponConsumeType DEFAULT_ATTACK_TYPE_CONSUME_TYPE = AttackType.WeaponConsumeType.NONE;
    private static final boolean DEFAULT_ATTACK_TYPE_NONIDEAL_RANGE = false;
    private static final float DEFAULT_ATTACK_TYPE_DAMAGE_MULT = 0.0f;
    private static final boolean DEFAULT_ATTACK_TYPE_OVERRIDE_EFFECTS = false;
    private static final float DEFAULT_ATTACK_TYPE_HIT_CHANCE_MULT = 0.0f;

    private final ScriptParser scriptParser;

    public CombatTypeLoader(ScriptParser scriptParser) {
        this.scriptParser = scriptParser;
    }

    public Map<String, DamageType> loadDamageTypes(Element element) {
        return LoadUtils.loadAll(element, NAME_DAMAGE_TYPE, this::parseDamageType, DamageType::ID);
    }

    public Map<String, WeaponClass> loadWeaponClasses(Element element, Registry<AttackType> attackTypeRegistry, Registry<Skill> skillRegistry) {
        return LoadUtils.loadAll(element, NAME_WEAPON_CLASS, e -> parseWeaponClass(e, attackTypeRegistry, skillRegistry), WeaponClass::ID);
    }

    public Map<String, AttackType> loadAttackTypes(Element element, Pathfinder pathfinder, Registry<DamageType> damageTypeRegistry, Registry<Effect> effectRegistry) {
        return LoadUtils.loadAll(element, NAME_ATTACK_TYPE, e -> parseAttackType(e, pathfinder, damageTypeRegistry, effectRegistry), AttackType::getID);
    }

    private DamageType parseDamageType(Element element) {
        String ID = LoadUtils.attribute(element, NAME_DAMAGE_TYPE_ID, null);
        String name = element.getTextContent();
        return new DamageType(ID, name);
    }

    private WeaponClass parseWeaponClass(Element element, Registry<AttackType> attackTypeRegistry, Registry<Skill> skillRegistry) {
        String ID = LoadUtils.attribute(element, NAME_WEAPON_CLASS_ID, null);
        String name = LoadUtils.singleTag(element, NAME_WEAPON_CLASS_NAME, null);
        boolean isRanged = LoadUtils.attributeBool(element, NAME_WEAPON_CLASS_IS_RANGED, DEFAULT_WEAPON_CLASS_IS_RANGED);
        boolean isLoud = LoadUtils.attributeBool(element, NAME_WEAPON_CLASS_IS_LOUD, DEFAULT_WEAPON_CLASS_IS_LOUD);
        String skillID = LoadUtils.attribute(element, NAME_WEAPON_CLASS_SKILL, null);
        Skill skill = skillRegistry.getFromID(skillID);
        if (skill == null) throw new GameDataException("WeaponClass has invalid skill");
        Set<AreaLink.DistanceCategory> primaryRanges = LoadUtils.setOfEnumTags(element, NAME_WEAPON_CLASS_RANGE, AreaLink.DistanceCategory.class);
        Set<String> attackTypeIDs = LoadUtils.setOfTags(element, NAME_WEAPON_CLASS_ATTACK_TYPE);
        Set<AttackType> attackTypes = new HashSet<>();
        for (String attackTypeID : attackTypeIDs) {
            AttackType attackType = attackTypeRegistry.getFromID(attackTypeID);
            if (attackType == null) throw new GameDataException("WeaponClass has invalid attack type");
            attackTypes.add(attackType);
        }
        return new WeaponClass(ID, name, isRanged, isLoud, skill, primaryRanges, attackTypes);
    }

    private AttackType parseAttackType(Element element, Pathfinder pathfinder, Registry<DamageType> damageTypeRegistry, Registry<Effect> effectRegistry) {
        String ID = LoadUtils.attribute(element, NAME_ATTACK_TYPE_ID, null);
        AttackType.AttackCategory category;
        try {
            category = LoadUtils.attributeEnum(element, NAME_ATTACK_TYPE_CATEGORY, AttackType.AttackCategory.class, DEFAULT_ATTACK_TYPE_CATEGORY);
        } catch (IllegalArgumentException e) {
            throw new GameDataException("AttackType has invalid category");
        }
        String prompt = LoadUtils.singleTag(element, NAME_ATTACK_TYPE_PROMPT, null);
        String attackPhrase = LoadUtils.singleTag(element, NAME_ATTACK_TYPE_PHRASE, null);
        String attackOverallPhrase = LoadUtils.singleTag(element, NAME_ATTACK_TYPE_PHRASE_OVERALL, null);
        String attackPhraseAudible = LoadUtils.singleTag(element, NAME_ATTACK_TYPE_PHRASE_AUDIBLE, null);
        String attackOverallPhraseAudible = LoadUtils.singleTag(element, NAME_ATTACK_TYPE_PHRASE_AUDIBLE_OVERALL, null);
        int ammoConsumed = LoadUtils.attributeInt(element, NAME_ATTACK_TYPE_AMMO_CONSUMED, DEFAULT_ATTACK_TYPE_AMMO_CONSUMED);
        int actionPoints = LoadUtils.attributeInt(element, NAME_ATTACK_TYPE_ACTION_POINTS, DEFAULT_ATTACK_TYPE_ACTION_POINTS);
        AttackType.WeaponConsumeType weaponConsumeType;
        try {
            weaponConsumeType = LoadUtils.attributeEnum(element, NAME_ATTACK_TYPE_CONSUME_TYPE, AttackType.WeaponConsumeType.class, DEFAULT_ATTACK_TYPE_CONSUME_TYPE);
        } catch (IllegalArgumentException e) {
            throw new GameDataException("AttackType has invalid weapon consume type");
        }
        boolean useNonIdealRange = LoadUtils.attributeBool(element, NAME_ATTACK_TYPE_NONIDEAL_RANGE, DEFAULT_ATTACK_TYPE_NONIDEAL_RANGE);
        Set<AreaLink.DistanceCategory> rangeOverride = LoadUtils.setOfEnumTags(element, NAME_ATTACK_TYPE_RANGE, AreaLink.DistanceCategory.class);
        Integer rateOverride = LoadUtils.attributeInt(element, NAME_ATTACK_TYPE_RATE, null);
        Script damageOverride = LoadUtils.loadScriptExpression(LoadUtils.singleChildWithName(element, NAME_ATTACK_TYPE_DAMAGE), scriptParser, "WeaponAttackType(" + ID + ") - damage");
        float damageMult = LoadUtils.attributeFloat(element, NAME_ATTACK_TYPE_DAMAGE_MULT, DEFAULT_ATTACK_TYPE_DAMAGE_MULT);
        String damageTypeOverrideID = LoadUtils.attribute(element, NAME_ATTACK_TYPE_DAMAGE_TYPE, null);
        DamageType damageTypeOverride = damageTypeRegistry.getFromID(damageTypeOverrideID);
        if (damageTypeOverride == null) throw new GameDataException("AttackType has invalid damage type override");
        Float armorMultOverride = LoadUtils.attributeFloat(element, NAME_ATTACK_TYPE_ARMOR_MULT, null);
        List<String> targetEffectIDs = LoadUtils.listOfTags(element, NAME_ATTACK_TYPE_TARGET_EFFECT);
        List<Effect> targetEffects = new ArrayList<>();
        for (String effectID : targetEffectIDs) {
            Effect effect = effectRegistry.getFromID(effectID);
            if (effect == null) throw new GameDataException("AttackType has invalid target effect");
            targetEffects.add(effect);
        }
        boolean overrideTargetEffects = LoadUtils.attributeBool(element, NAME_ATTACK_TYPE_OVERRIDE_EFFECTS, DEFAULT_ATTACK_TYPE_OVERRIDE_EFFECTS);
        Script hitChance = LoadUtils.loadScriptExpression(LoadUtils.singleChildWithName(element, NAME_ATTACK_TYPE_HIT_CHANCE), scriptParser, "WeaponAttackType(" + ID + ") - hit chance");
        Script hitChanceOverall = LoadUtils.loadScriptExpression(LoadUtils.singleChildWithName(element, NAME_ATTACK_TYPE_HIT_CHANCE_OVERALL), scriptParser, "WeaponAttackType(" + ID + ") - overall hit chance");
        float hitChanceMult = LoadUtils.attributeFloat(element, NAME_ATTACK_TYPE_HIT_CHANCE_MULT, DEFAULT_ATTACK_TYPE_HIT_CHANCE_MULT);
        Boolean isLoudOverride = LoadUtils.attributeBool(element, NAME_ATTACK_TYPE_IS_LOUD_OVERRIDE, null);
        return new AttackType(pathfinder, ID, category, prompt, attackPhrase, attackOverallPhrase, attackPhraseAudible, attackOverallPhraseAudible, ammoConsumed, actionPoints, weaponConsumeType, useNonIdealRange, rangeOverride, rateOverride, damageOverride, damageMult, damageTypeOverride, armorMultOverride, targetEffects, overrideTargetEffects, hitChance, hitChanceOverall, hitChanceMult, isLoudOverride);
    }

}
