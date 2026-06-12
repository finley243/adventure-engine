package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.GameDataException;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.actor.ActorTemplate;
import com.github.finley243.adventureengine.actor.Bark;
import com.github.finley243.adventureengine.actor.EquipSlot;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.item.LootTable;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.textgen.TextContext;
import org.w3c.dom.Element;

import java.util.*;

public class ActorTemplateLoader {

    private static final String NAME_ACTOR_TEMPLATE = "actor";

    private final ScriptParser scriptParser;
    private final LootTableLoader lootTableLoader;

    public ActorTemplateLoader(ScriptParser scriptParser, LootTableLoader lootTableLoader) {
        this.scriptParser = scriptParser;
        this.lootTableLoader = lootTableLoader;
    }

    public Map<String, ActorTemplate> load(Element element) {
        return LoadUtils.loadAll(element, NAME_ACTOR_TEMPLATE, this::parseActorTemplate, ActorTemplate::getID);
    }

    private ActorTemplate parseActorTemplate(Element element) {
        String id = LoadUtils.attribute(element, "id", null);
        String parentID = LoadUtils.attribute(element, "parent", null);
        Element nameElement = LoadUtils.singleChildWithName(element, "name");
        String name = nameElement != null ? nameElement.getTextContent() : null;
        Boolean nameIsProper = nameElement != null && LoadUtils.attributeBool(nameElement, "proper", false);
        TextContext.Pronoun pronoun;
        try {
            pronoun = LoadUtils.attributeEnum(nameElement, "pronoun", TextContext.Pronoun.class, TextContext.Pronoun.THEY);
        } catch (IllegalArgumentException e) {
            throw new GameDataException("ActorTemplate has invalid pronoun");
        }
        String faction = LoadUtils.attribute(element, "faction", null);
        Boolean isEnforcer = LoadUtils.attributeBool(element, "isEnforcer", null);

        Integer actionPoints = LoadUtils.attributeInt(element, "actionPoints", null);
        Integer movePoints = LoadUtils.attributeInt(element, "movePoints", null);
        Integer startingLevel = LoadUtils.attributeInt(element, "startLevel", null);
        Script levelUpThresholdExpression = LoadUtils.loadScriptExpression(LoadUtils.singleChildWithName(element, "levelUpThreshold"), scriptParser, "Actor(" + id + ") - level up threshold");
        Integer hp = LoadUtils.attributeInt(element, "hp", null);
        Map<String, Integer> damageResistances = new HashMap<>();
        Map<String, Float> damageMults = new HashMap<>();
        for (Element damageElement : LoadUtils.directChildrenWithName(element, "damage")) {
            String damageType = LoadUtils.attribute(damageElement, "type", null);
            Integer damageResistance = LoadUtils.attributeInt(damageElement, "resistance", null);
            Float damageMult = LoadUtils.attributeFloat(damageElement, "mult", null);
            if (damageResistance != null) {
                damageResistances.put(damageType, damageResistance);
            }
            if (damageMult != null) {
                damageMults.put(damageType, damageMult);
            }
        }
        List<Limb> limbs = parseLimbs(element);
        Map<String, EquipSlot> equipSlots = new HashMap<>();
        for (Element slotElement : LoadUtils.directChildrenWithName(element, "equipSlot")) {
            String slotID = LoadUtils.attribute(slotElement, "id", null);
            String slotName = slotElement.getTextContent();
            equipSlots.put(slotID, new EquipSlot(slotID, slotName));
        }
        LootTable lootTable = lootTableLoader.parseLootTable(LoadUtils.singleChildWithName(element, "inventory"), true);
        String dialogueStart = LoadUtils.attribute(element, "dialogueStart", null);
        Map<String, Integer> attributes = new HashMap<>();
        for (Element attributeElement : LoadUtils.directChildrenWithName(element, "attribute")) {
            String attribute = LoadUtils.attribute(attributeElement, "key", null);
            int value = LoadUtils.attributeInt(attributeElement, "value", 0);
            attributes.put(attribute, value);
        }
        Map<String, Integer> skills = new HashMap<>();
        for (Element skillElement : LoadUtils.directChildrenWithName(element, "skill")) {
            String skill = LoadUtils.attribute(skillElement, "key", null);
            int value = LoadUtils.attributeInt(skillElement, "value", 0);
            skills.put(skill, value);
        }
        Set<String> senseTypes = LoadUtils.setOfTags(element, "senseType");
        List<String> unarmedAttackTypes = LoadUtils.listOfTags(element, "attackType");
        Map<String, List<Script>> scripts = LoadUtils.loadScriptsWithTriggers(element, scriptParser, "Actor(" + id + ")");
        List<String> startingEffects = LoadUtils.listOfTags(element, "startEffect");

        Map<String, Bark> barks = new HashMap<>();
        for (Element barkElement : LoadUtils.directChildrenWithName(element, "bark")) {
            String barkTrigger = LoadUtils.attribute(barkElement, "trigger", null);
            Bark.BarkResponseType responseType;
            try {
                responseType = LoadUtils.attributeEnum(barkElement, "response", Bark.BarkResponseType.class, Bark.BarkResponseType.NONE);
            } catch (IllegalArgumentException e) {
                throw new GameDataException("ActorTemplate has invalid bark response type for bark: " + barkTrigger);
            }
            List<String> visiblePhrases = LoadUtils.listOfTags(barkElement, "visible");
            List<String> nonVisiblePhrases = LoadUtils.listOfTags(barkElement, "nonVisible");
            barks.put(barkTrigger, new Bark(responseType, visiblePhrases, nonVisiblePhrases));
        }

        List<ActionCustom.CustomActionHolder> customActions = LoadUtils.loadCustomActions(element, "action", scriptParser, "ActorTemplate(" + id + ")");
        List<ActionCustom.CustomActionHolder> customInventoryActions = LoadUtils.loadCustomActions(element, "itemAction", scriptParser, "ActorTemplate(" + id + ")");

        return new ActorTemplate(id, parentID, name, nameIsProper, pronoun, faction, isEnforcer, actionPoints, movePoints, startingLevel, levelUpThresholdExpression, hp, damageResistances, damageMults, limbs, equipSlots, attributes, skills, senseTypes, unarmedAttackTypes, startingEffects, lootTable, dialogueStart, scripts, barks, customActions, customInventoryActions);
    }

    private List<Limb> parseLimbs(Element element) {
        List<Limb> limbs = new ArrayList<>();
        if (element == null) return limbs;
        for (Element limbElement : LoadUtils.directChildrenWithName(element, "limb")) {
            limbs.add(parseLimb(limbElement));
        }
        return limbs;
    }

    private Limb parseLimb(Element element) {
        String ID = LoadUtils.attribute(element, "id", null);
        String name = LoadUtils.singleTag(element, "name", null);
        float hitChance = LoadUtils.attributeFloat(element, "hitChance", 1.0f);
        float damageMult = LoadUtils.attributeFloat(element, "damageMult", 1.0f);
        String apparelSlot = LoadUtils.attribute(element, "apparelSlot", null);
        List<String> hitEffects = LoadUtils.listOfTags(element, "hitEffect");
        return new Limb(ID, name, hitChance, damageMult, apparelSlot, hitEffects);
    }

}
