package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.action.ActionTemplate;
import com.github.finley243.adventureengine.actor.*;
import com.github.finley243.adventureengine.combat.AttackType;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.item.LootTable;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.textgen.TextContext;
import org.w3c.dom.Element;

import java.util.*;

public class ActorTemplateLoader {

    private static final String NAME_ACTOR_TEMPLATE = "actor";

    private final ScriptPipeline scriptPipeline;
    private final LootTableLoader lootTableLoader;
    private final Registry<SenseType> senseTypeRegistry;
    private final Registry<ActionTemplate> actionRegistry;
    private final Registry<Effect> effectRegistry;
    private final Registry<Faction> factionRegistry;
    private final Registry<AttackType> attackTypeRegistry;
    private final Registry<Scene> sceneRegistry;
    private final Registry<LootTable> lootTableRegistry;

    public ActorTemplateLoader(ScriptPipeline scriptPipeline, LootTableLoader lootTableLoader, Registry<SenseType> senseTypeRegistry, Registry<ActionTemplate> actionRegistry, Registry<Effect> effectRegistry, Registry<Faction> factionRegistry, Registry<AttackType> attackTypeRegistry, Registry<Scene> sceneRegistry, Registry<LootTable> lootTableRegistry) {
        this.scriptPipeline = scriptPipeline;
        this.lootTableLoader = lootTableLoader;
        this.senseTypeRegistry = senseTypeRegistry;
        this.actionRegistry = actionRegistry;
        this.effectRegistry = effectRegistry;
        this.factionRegistry = factionRegistry;
        this.attackTypeRegistry = attackTypeRegistry;
        this.sceneRegistry = sceneRegistry;
        this.lootTableRegistry = lootTableRegistry;
    }

    public Map<String, ActorTemplate> load(Element element) {
        List<TemplateNode> templateNodes = new ArrayList<>();
        for (Element child : LoadUtils.directChildrenWithName(element, NAME_ACTOR_TEMPLATE)) {
            TemplateNode templateNode = parseTemplateNode(child);
            templateNodes.add(templateNode);
        }
        List<TemplateNode> sortedNodes = LoadUtils.topologicalSort(templateNodes, TemplateNode::id, node -> node.parentId() != null ? List.of(node.parentId()) : List.of());
        Map<String, ActorTemplate> actorTemplateMap = new HashMap<>();
        for (TemplateNode node : sortedNodes) {
            ActorTemplate actorTemplate = parseActorTemplate(node.element(), actorTemplateMap);
            actorTemplateMap.put(node.id(), actorTemplate);
        }
        return actorTemplateMap;
    }

    private TemplateNode parseTemplateNode(Element element) {
        String id = LoadUtils.attribute(element, "id", null);
        String parentID = LoadUtils.attribute(element, "parent", null);
        return new TemplateNode(id, parentID, element);
    }

    private ActorTemplate parseActorTemplate(Element element, Map<String, ActorTemplate> actorTemplateMap) {
        String id = LoadUtils.attribute(element, "id", null);
        String parentID = LoadUtils.attribute(element, "parent", null);
        ActorTemplate parent = actorTemplateMap.get(parentID);
        if (parent == null && parentID != null) throw new GameDataException("ActorTemplate has invalid parent");
        Element nameElement = LoadUtils.singleChildWithName(element, "name");
        String name = nameElement != null ? nameElement.getTextContent() : null;
        Boolean nameIsProper = nameElement != null && LoadUtils.attributeBool(nameElement, "proper", false);
        TextContext.Pronoun pronoun;
        try {
            pronoun = LoadUtils.attributeEnum(nameElement, "pronoun", TextContext.Pronoun.class, TextContext.Pronoun.THEY);
        } catch (IllegalArgumentException e) {
            throw new GameDataException("ActorTemplate has invalid pronoun");
        }
        String factionID = LoadUtils.attribute(element, "faction", null);
        Faction faction = factionRegistry.getFromID(factionID);
        if (faction == null) throw new GameDataException("ActorTemplate has invalid faction");
        Boolean isEnforcer = LoadUtils.attributeBool(element, "isEnforcer", null);

        Integer actionPoints = LoadUtils.attributeInt(element, "actionPoints", null);
        Integer movePoints = LoadUtils.attributeInt(element, "movePoints", null);
        Integer startingLevel = LoadUtils.attributeInt(element, "startLevel", null);
        Script levelUpThresholdExpression = LoadUtils.loadScriptExpression(LoadUtils.singleChildWithName(element, "levelUpThreshold"), scriptPipeline, "Actor(" + id + ") - level up threshold");
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
        LootTable lootTable = lootTableLoader.parseLootTable(LoadUtils.singleChildWithName(element, "inventory"), lootTableRegistry::getFromID, true);
        String dialogueStartID = LoadUtils.attribute(element, "dialogueStart", null);
        Scene dialogueStart = sceneRegistry.getFromID(dialogueStartID);
        if (dialogueStartID != null && dialogueStart == null) throw new GameDataException("ActorTemplate has invalid dialogue start scene: " + dialogueStartID);
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
        Set<String> senseTypeIDs = LoadUtils.setOfTags(element, "senseType");
        Set<SenseType> senseTypes = new HashSet<>();
        for (String senseTypeID : senseTypeIDs) {
            SenseType senseType = senseTypeRegistry.getFromID(senseTypeID);
            if (senseType == null) throw new GameDataException("ActorTemplate has invalid senseType: " + senseTypeID);
            senseTypes.add(senseType);
        }
        List<String> unarmedAttackTypeIDs = LoadUtils.listOfTags(element, "attackType");
        List<AttackType> unarmedAttackTypes = new ArrayList<>();
        for (String unarmedAttackTypeID : unarmedAttackTypeIDs) {
            AttackType unarmedAttackType = attackTypeRegistry.getFromID(unarmedAttackTypeID);
            if (unarmedAttackType == null) throw new GameDataException("ActorTemplate has invalid unarmed attack type: " + unarmedAttackTypeID);
            unarmedAttackTypes.add(unarmedAttackType);
        }
        Map<String, List<Script>> scripts = LoadUtils.loadScriptsWithTriggers(element, scriptPipeline, "Actor(" + id + ")");
        List<String> startingEffectIDs = LoadUtils.listOfTags(element, "startEffect");
        List<Effect> startingEffects = new ArrayList<>();
        for (String startingEffectID : startingEffectIDs) {
            Effect effect = effectRegistry.getFromID(startingEffectID);
            if (effect == null) throw new GameDataException("ActorTemplate has invalid starting effect: " + startingEffectID);
            startingEffects.add(effect);
        }

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

        List<ActionCustom.CustomActionHolder> customActions = LoadUtils.loadCustomActions(element, "action", scriptPipeline, actionRegistry, "ActorTemplate(" + id + ")");
        List<ActionCustom.CustomActionHolder> customInventoryActions = LoadUtils.loadCustomActions(element, "itemAction", scriptPipeline, actionRegistry, "ActorTemplate(" + id + ")");

        return new ActorTemplate(id, parent, name, nameIsProper, pronoun, faction, isEnforcer, actionPoints, movePoints, startingLevel, levelUpThresholdExpression, hp, damageResistances, damageMults, limbs, equipSlots, attributes, skills, senseTypes, unarmedAttackTypes, startingEffects, lootTable, dialogueStart, scripts, barks, customActions, customInventoryActions);
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
        List<String> hitEffectIDs = LoadUtils.listOfTags(element, "hitEffect");
        List<Effect> hitEffects = new ArrayList<>();
        for (String hitEffectID : hitEffectIDs) {
            Effect effect = effectRegistry.getFromID(hitEffectID);
            if (effect == null) throw new GameDataException("Limb has invalid hit effect: " + hitEffectID);
            hitEffects.add(effect);
        }
        return new Limb(ID, name, hitChance, damageMult, apparelSlot, hitEffects);
    }

    private record TemplateNode(String id, String parentId, Element element) {}

}
