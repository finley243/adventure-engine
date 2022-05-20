package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.actor.*;
import com.github.finley243.adventureengine.actor.ai.behavior.*;
import com.github.finley243.adventureengine.actor.component.ApparelComponent;
import com.github.finley243.adventureengine.condition.*;
import com.github.finley243.adventureengine.dialogue.DialogueChoice;
import com.github.finley243.adventureengine.dialogue.DialogueLine;
import com.github.finley243.adventureengine.dialogue.DialogueTopic;
import com.github.finley243.adventureengine.effect.*;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.scene.SceneLine;
import com.github.finley243.adventureengine.script.*;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.world.Lock;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;
import com.github.finley243.adventureengine.world.environment.Room;
import com.github.finley243.adventureengine.item.ItemFactory;
import com.github.finley243.adventureengine.item.LootTable;
import com.github.finley243.adventureengine.item.LootTableEntry;
import com.github.finley243.adventureengine.item.template.*;
import com.github.finley243.adventureengine.world.object.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class DataLoader {

    public static void loadFromDir(Game game, File dir) throws ParserConfigurationException, IOException, SAXException {
        if(dir.isDirectory()) {
            File[] files = dir.listFiles();
            assert files != null;
            for(File file : files) {
                if (file.getName().substring(file.getName().lastIndexOf(".") + 1).equalsIgnoreCase("xml")) {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.parse(file);
                    Element rootElement = document.getDocumentElement();
                    List<Element> factions = LoadUtils.directChildrenWithName(rootElement, "faction");
                    for (Element factionElement : factions) {
                        Faction faction = loadFaction(factionElement);
                        game.data().addFaction(faction.getID(), faction);
                    }
                    List<Element> topics = LoadUtils.directChildrenWithName(rootElement, "topic");
                    for (Element topicElement : topics) {
                        DialogueTopic topic = loadTopic(topicElement);
                        game.data().addTopic(topic.getID(), topic);
                    }
                    List<Element> actors = LoadUtils.directChildrenWithName(rootElement, "actor");
                    for (Element actorElement : actors) {
                        ActorTemplate actor = loadActor(actorElement);
                        game.data().addActorTemplate(actor.getID(), actor);
                    }
                    List<Element> items = LoadUtils.directChildrenWithName(rootElement, "item");
                    for (Element itemElement : items) {
                        ItemTemplate item = loadItem(itemElement);
                        game.data().addItem(item.getID(), item);
                    }
                    List<Element> tables = LoadUtils.directChildrenWithName(rootElement, "lootTable");
                    for (Element tableElement : tables) {
                        LootTable table = loadLootTable(tableElement, false);
                        game.data().addLootTable(table.getID(), table);
                    }
                    List<Element> scenes = LoadUtils.directChildrenWithName(rootElement, "scene");
                    for (Element sceneElement : scenes) {
                        Scene scene = loadScene(sceneElement);
                        game.data().addScene(scene.getID(), scene);
                    }
                    List<Element> rooms = LoadUtils.directChildrenWithName(rootElement, "room");
                    for (Element roomElement : rooms) {
                        Room room = loadRoom(game, roomElement);
                        game.data().addRoom(room.getID(), room);
                    }
                    List<Element> scripts = LoadUtils.directChildrenWithName(rootElement, "script");
                    for (Element scriptElement : scripts) {
                        String scriptID = LoadUtils.attribute(scriptElement, "id", null);
                        Script script = loadScript(scriptElement);
                        game.data().addScript(scriptID, script);
                    }
                }
            }
        }
    }

    private static ActorTemplate loadActor(Element actorElement) throws ParserConfigurationException, IOException, SAXException {
        String id = actorElement.getAttribute("id");
        String parentID = actorElement.getAttribute("parent");
        Element nameElement = LoadUtils.singleChildWithName(actorElement, "name");
        String name = nameElement != null ? nameElement.getTextContent() : null;
        boolean nameIsProper = nameElement != null && LoadUtils.attributeBool(nameElement, "proper", false);
        Context.Pronoun pronoun = LoadUtils.attributeEnum(nameElement, "pronoun", Context.Pronoun.class, Context.Pronoun.THEY);
        String faction = LoadUtils.singleTag(actorElement, "faction", "default");
        int hp = LoadUtils.attributeInt(actorElement, "hp", 0);
        List<Limb> limbs = loadLimbs(actorElement);
        LootTable lootTable = loadLootTable(LoadUtils.singleChildWithName(actorElement, "inventory"), true);
        String topic = LoadUtils.attribute(actorElement, "topic", null);
        Map<Actor.Attribute, Integer> attributes = loadAttributes(actorElement);
        Map<Actor.Skill, Integer> skills = loadSkills(actorElement);
        Map<String, Script> scripts = loadScriptsWithTriggers(actorElement);
        Element vendorElement = LoadUtils.singleChildWithName(actorElement, "vendor");
        boolean isVendor = vendorElement != null;
        String vendorLootTable = LoadUtils.attribute(vendorElement, "lootTable", null);
        Set<String> vendorBuyTags = LoadUtils.setOfTags(vendorElement, "buyTag");
        boolean vendorBuyAll = LoadUtils.attributeBool(vendorElement, "buyAll", false);
        boolean vendorStartDisabled = LoadUtils.attributeBool(vendorElement, "startDisabled", false);
        return new ActorTemplate(id, parentID, name, nameIsProper, pronoun, faction, hp, limbs, attributes, skills, lootTable, topic, scripts, isVendor, vendorLootTable, vendorBuyTags, vendorBuyAll, vendorStartDisabled);
    }

    private static List<Limb> loadLimbs(Element element) {
        List<Limb> limbs = new ArrayList<>();
        if(element == null) return limbs;
        for(Element limbElement : LoadUtils.directChildrenWithName(element, "limb")) {
            limbs.add(loadLimb(limbElement));
        }
        return limbs;
    }

    private static Limb loadLimb(Element element) {
        String name = LoadUtils.singleTag(element, "name", null);
        float hitChance = LoadUtils.attributeFloat(element, "hitChance", 1.0f);
        float damageMult = LoadUtils.attributeFloat(element, "damageMult", 1.0f);
        ApparelComponent.ApparelSlot apparelSlot = LoadUtils.attributeEnum(element, "apparelSlot", ApparelComponent.ApparelSlot.class, ApparelComponent.ApparelSlot.TORSO);
        List<Effect> crippledEffects = loadEffects(element, false);
        return new Limb(name, hitChance, damageMult, apparelSlot, crippledEffects);
    }

    private static Map<Actor.Attribute, Integer> loadAttributes(Element element) {
        Map<Actor.Attribute, Integer> attributes = new EnumMap<>(Actor.Attribute.class);
        if(element == null) return attributes;
        for(Element attributeElement : LoadUtils.directChildrenWithName(element, "attribute")) {
            Actor.Attribute attribute = LoadUtils.attributeEnum(attributeElement, "key", Actor.Attribute.class, null);
            int value = LoadUtils.attributeInt(attributeElement, "value", 0);
            attributes.put(attribute, value);
        }
        return attributes;
    }

    private static Map<Actor.Skill, Integer> loadSkills(Element element) {
        Map<Actor.Skill, Integer> skills = new EnumMap<>(Actor.Skill.class);
        if(element == null) return skills;
        for(Element skillElement : LoadUtils.directChildrenWithName(element, "skill")) {
            Actor.Skill skill = LoadUtils.attributeEnum(skillElement, "key", Actor.Skill.class, null);
            int value = LoadUtils.attributeInt(skillElement, "value", 0);
            skills.put(skill, value);
        }
        return skills;
    }

    private static DialogueTopic loadTopic(Element topicElement) throws ParserConfigurationException, SAXException, IOException {
        String topicID = topicElement.getAttribute("id");
        DialogueTopic.TopicType type;
        switch(topicElement.getAttribute("type")) {
            case "random":
                type = DialogueTopic.TopicType.RANDOM;
                break;
            case "select":
                type = DialogueTopic.TopicType.SELECTOR;
                break;
            case "all":
            default:
                type = DialogueTopic.TopicType.SEQUENTIAL;
                break;
        }
        Condition condition = loadCondition(LoadUtils.singleChildWithName(topicElement, "condition"));
        boolean once = LoadUtils.attributeBool(topicElement, "once", false);
        List<Element> lineElements = LoadUtils.directChildrenWithName(topicElement, "line");
        List<DialogueLine> lines = new ArrayList<>();
        for(Element lineElement : lineElements) {
            DialogueLine line = loadLine(lineElement);
            lines.add(line);
        }
        List<Element> choiceElements = LoadUtils.directChildrenWithName(topicElement, "choice");
        List<DialogueChoice> choices = new ArrayList<>();
        for(Element choiceElement : choiceElements) {
            DialogueChoice choice = loadChoice(choiceElement);
            choices.add(choice);
        }
        return new DialogueTopic(topicID, condition, once, lines, choices, type);
    }

    private static DialogueLine loadLine(Element lineElement) throws ParserConfigurationException, SAXException, IOException {
        boolean once = LoadUtils.attributeBool(lineElement, "once", false);
        boolean exit = LoadUtils.attributeBool(lineElement, "exit", false);
        String redirect = LoadUtils.attribute(lineElement, "redirect", null);
        String from = LoadUtils.attribute(lineElement, "from", null);
        List<String> texts = LoadUtils.listOfTags(lineElement, "text");
        Condition condition = loadCondition(LoadUtils.singleChildWithName(lineElement, "condition"));
        Script script = loadScript(LoadUtils.singleChildWithName(lineElement, "script"));
        return new DialogueLine(texts, condition, script, once, exit, redirect, from);
    }

    private static DialogueChoice loadChoice(Element choiceElement) {
        String link = choiceElement.getAttribute("link");
        String prompt = choiceElement.getTextContent();
        return new DialogueChoice(link, prompt);
    }

    private static Condition loadCondition(Element conditionElement) throws ParserConfigurationException, SAXException, IOException {
        if(conditionElement == null) return null;
        String type = LoadUtils.attribute(conditionElement, "type", "compound");
        boolean invert = LoadUtils.attributeBool(conditionElement, "invert", false);
        ActorReference actorRef = loadActorReference(conditionElement, "actor");
        switch(type) {
            case "money":
                int moneyAmount = LoadUtils.attributeInt(conditionElement, "value", 0);
                return new ConditionMoney(invert, actorRef, moneyAmount);
            case "var":
                String varID = LoadUtils.attribute(conditionElement, "variable", null);
                Condition.Equality varEquality = LoadUtils.attributeEnum(conditionElement, "equality", Condition.Equality.class, Condition.Equality.GREATER_EQUAL);
                int varValue = LoadUtils.attributeInt(conditionElement, "value", 0);
                return new ConditionVariable(invert, varID, varEquality, varValue);
            case "attribute":
                Actor.Attribute attribute = LoadUtils.attributeEnum(conditionElement, "attribute", Actor.Attribute.class, null);
                int attributeValue = LoadUtils.attributeInt(conditionElement, "value", 0);
                return new ConditionAttribute(invert, actorRef, attribute, attributeValue);
            case "skill":
                Actor.Skill skill = LoadUtils.attributeEnum(conditionElement, "skill", Actor.Skill.class, null);
                int skillValue = LoadUtils.attributeInt(conditionElement, "value", 0);
                return new ConditionSkill(invert, actorRef, skill, skillValue);
            case "actorLocation":
                String actorArea = LoadUtils.attribute(conditionElement, "area", null);
                String actorRoom = LoadUtils.attribute(conditionElement, "room", null);
                boolean useRoom = actorArea == null;
                return new ConditionActorLocation(invert, actorRef, (useRoom ? actorRoom : actorArea), useRoom);
            case "actorAvailableForScene":
                return new ConditionActorAvailableForScene(invert, actorRef);
            case "actorDead":
                return new ConditionActorDead(invert, actorRef);
            case "actorHP":
                Condition.Equality hpEquality = LoadUtils.attributeEnum(conditionElement, "equality", Condition.Equality.class, Condition.Equality.GREATER_EQUAL);
                float hpValue = LoadUtils.attributeFloat(conditionElement, "value", 0);
                return new ConditionActorHP(invert, actorRef, hpEquality, hpValue);
            case "combatant":
                ActorReference targetRef = loadActorReference(conditionElement, "target");
                return new ConditionCombatant(invert, actorRef, targetRef);
            case "equippedItem":
                String itemEquipTag = LoadUtils.attribute(conditionElement, "tag", null);
                String itemEquipExact = LoadUtils.attribute(conditionElement, "exact", null);
                return new ConditionEquippedItem(invert, actorRef, itemEquipTag, itemEquipExact);
            case "inventoryItem":
                String itemInvTag = LoadUtils.attribute(conditionElement, "tag", null);
                String itemInvExact = LoadUtils.attribute(conditionElement, "exact", null);
                return new ConditionInventoryItem(invert, actorRef, itemInvTag, itemInvExact);
            case "actorVisible":
                ActorReference visibleTargetRef = loadActorReference(conditionElement, "target");
                return new ConditionActorVisible(invert, actorRef, visibleTargetRef);
            case "inCombat":
                return new ConditionActorInCombat(invert, actorRef);
            case "time":
                Element timeStartElement = LoadUtils.singleChildWithName(conditionElement, "start");
                Element timeEndElement = LoadUtils.singleChildWithName(conditionElement, "end");
                int hours1 = LoadUtils.attributeInt(timeStartElement, "hours", 0);
                int minutes1 = LoadUtils.attributeInt(timeStartElement, "minutes", 0);
                int hours2 = LoadUtils.attributeInt(timeEndElement, "hours", 0);
                int minutes2 = LoadUtils.attributeInt(timeEndElement, "minutes", 0);
                return new ConditionTime(invert, hours1, minutes1, hours2, minutes2);
            case "any":
                List<Condition> subConditionsAny = loadSubConditions(conditionElement);
                return new ConditionCompound(invert, subConditionsAny, true);
            case "all":
            default:
                List<Condition> subConditionsAll = loadSubConditions(conditionElement);
                return new ConditionCompound(invert, subConditionsAll, false);
        }
    }

    private static List<Condition> loadSubConditions(Element conditionElement) throws ParserConfigurationException, SAXException, IOException {
        List<Element> subConditionElements = LoadUtils.directChildrenWithName(conditionElement, "condition");
        List<Condition> subConditions = new ArrayList<>();
        for (Element subConditionElement : subConditionElements) {
            Condition subCondition = loadCondition(subConditionElement);
            subConditions.add(subCondition);
        }
        return subConditions;
    }

    private static List<Script> loadSubScripts(Element parentElement) throws ParserConfigurationException, IOException, SAXException {
        List<Element> scriptElements = LoadUtils.directChildrenWithName(parentElement, "script");
        List<Script> scripts = new ArrayList<>();
        for(Element scriptElement : scriptElements) {
            Script script = loadScript(scriptElement);
            scripts.add(script);
        }
        return scripts;
    }

    private static Map<String, Script> loadScriptsWithTriggers(Element parentElement) throws ParserConfigurationException, IOException, SAXException {
        Map<String, Script> scripts = new HashMap<>();
        List<Element> scriptElements = LoadUtils.directChildrenWithName(parentElement, "script");
        for(Element scriptElement : scriptElements) {
            String trigger = scriptElement.getAttribute("trigger");
            Script script = loadScript(scriptElement);
            scripts.put(trigger, script);
        }
        return scripts;
    }

    private static Script loadScript(Element scriptElement) throws ParserConfigurationException, IOException, SAXException {
        if(scriptElement == null) return null;
        String type = scriptElement.getAttribute("type");
        Condition condition = loadCondition(LoadUtils.singleChildWithName(scriptElement, "condition"));
        ActorReference actorRef = loadActorReference(scriptElement, "actor");
        switch(type) {
            case "external":
                String scriptID = LoadUtils.attribute(scriptElement, "scriptID", null);
                return new ScriptExternal(condition, scriptID);
            case "money":
                int moneyValue = LoadUtils.attributeInt(scriptElement, "value", 0);
                return new ScriptMoney(condition, actorRef, moneyValue);
            case "addItem":
                String addItemID = LoadUtils.attribute(scriptElement, "item", null);
                return new ScriptAddItem(condition, actorRef, addItemID);
            case "transferItem":
                ActorReference transferTargetRef = loadActorReference(scriptElement, "target");
                String transferItemID = LoadUtils.attribute(scriptElement, "item", null);
                String transferItemSelect = LoadUtils.attribute(scriptElement, "select", null);
                int transferItemCount = LoadUtils.attributeInt(scriptElement, "count", 1);
                return new ScriptTransferItem(condition, actorRef, transferTargetRef, transferItemID, transferItemSelect, transferItemCount);
            case "scene":
                List<String> scenes = LoadUtils.listOfTags(scriptElement, "scene");
                return new ScriptScene(condition, scenes);
            case "varSet":
                String varSetID = LoadUtils.attribute(scriptElement, "variable", null);
                int varSetValue = LoadUtils.attributeInt(scriptElement, "value", 0);
                return new ScriptVariableSet(condition, varSetID, varSetValue);
            case "varMod":
                String varModID = LoadUtils.attribute(scriptElement, "variable", null);
                int varModValue = LoadUtils.attributeInt(scriptElement, "value", 0);
                return new ScriptVariableMod(condition, varModID, varModValue);
            case "dialogue":
                String topic = LoadUtils.attribute(scriptElement, "topic", null);
                return new ScriptDialogue(condition, actorRef, topic);
            case "combat":
                ActorReference combatantRef = loadActorReference(scriptElement, "combatant");
                return new ScriptCombat(condition, actorRef, combatantRef);
            case "factionRelation":
                String targetFaction = LoadUtils.attribute(scriptElement, "targetFaction", null);
                String relationFaction = LoadUtils.attribute(scriptElement, "relationFaction", null);
                Faction.FactionRelation relation = LoadUtils.attributeEnum(scriptElement, "relation", Faction.FactionRelation.class, Faction.FactionRelation.NEUTRAL);
                return new ScriptFactionRelation(condition, targetFaction, relationFaction, relation);
            case "moveActor":
                String moveActorArea = LoadUtils.attribute(scriptElement, "area", null);
                return new ScriptMoveActor(condition, actorRef, moveActorArea);
            case "actorState":
                boolean actorEnabled = LoadUtils.attributeBool(scriptElement, "enabled", true);
                return new ScriptActorState(condition, actorRef, actorEnabled);
            case "bark":
                List<String> barkLines = LoadUtils.listOfTags(scriptElement, "line");
                float barkChance = LoadUtils.attributeFloat(scriptElement, "chance", 1.0f);
                return new ScriptBark(condition, actorRef, barkLines, barkChance);
            case "nearestActorScript":
                String nearestTrigger = LoadUtils.attribute(scriptElement, "trigger", null);
                return new ScriptNearestActorWithScript(condition, nearestTrigger);
            case "unlock":
                String unlockObject = LoadUtils.attribute(scriptElement, "object", null);
                return new ScriptUnlock(condition, unlockObject);
            case "select":
                List<Script> subScriptsSelect = loadSubScripts(scriptElement);
                return new ScriptCompound(condition, subScriptsSelect, true);
            case "all":
            default:
                List<Script> subScriptsSequence = loadSubScripts(scriptElement);
                return new ScriptCompound(condition, subScriptsSequence, false);
        }
    }

    private static ActorReference loadActorReference(Element element, String name) {
        if(element == null || !element.hasAttribute(name)) return new ActorReference(ActorReference.ReferenceType.SUBJECT, null);
        String targetRef = element.getAttribute(name);
        switch (targetRef) {
            case "PLAYER":
                return new ActorReference(ActorReference.ReferenceType.PLAYER, null);
            case "SUBJECT":
                return new ActorReference(ActorReference.ReferenceType.SUBJECT, null);
            default:
                return new ActorReference(ActorReference.ReferenceType.REFERENCE, targetRef);
        }
    }

    private static Faction loadFaction(Element factionElement) {
        if(factionElement == null) return null;
        String id = factionElement.getAttribute("id");
        Faction.FactionRelation defaultRelation = LoadUtils.attributeEnum(factionElement, "default", Faction.FactionRelation.class, Faction.FactionRelation.NEUTRAL);
        Map<String, Faction.FactionRelation> relations = loadFactionRelations(factionElement);
        return new Faction(id, defaultRelation, relations);
    }

    private static Map<String, Faction.FactionRelation> loadFactionRelations(Element factionElement) {
        if(factionElement == null) return new HashMap<>();
        Map<String, Faction.FactionRelation> relations = new HashMap<>();
        List<Element> relationElements = LoadUtils.directChildrenWithName(factionElement, "relation");
        for(Element relationElement : relationElements) {
            String factionID = LoadUtils.attribute(relationElement, "faction", null);
            Faction.FactionRelation type = LoadUtils.attributeEnum(relationElement, "type", Faction.FactionRelation.class, Faction.FactionRelation.NEUTRAL);
            relations.put(factionID, type);
        }
        return relations;
    }

    private static ItemTemplate loadItem(Element itemElement) throws ParserConfigurationException, IOException, SAXException {
        if(itemElement == null) return null;
        String type = itemElement.getAttribute("type");
        String id = itemElement.getAttribute("id");
        String name = LoadUtils.singleTag(itemElement, "name", null);
        String description = LoadUtils.singleTag(itemElement, "description", null);
        Map<String, Script> scripts = loadScriptsWithTriggers(itemElement);
        int price = LoadUtils.attributeInt(itemElement, "price", 0);
        switch(type) {
            case "apparel":
                ApparelComponent.ApparelSlot apparelSlot = LoadUtils.singleTagEnum(itemElement, "slot", ApparelComponent.ApparelSlot.class, ApparelComponent.ApparelSlot.TORSO);
                int damageResistance = LoadUtils.singleTagInt(itemElement, "damageResistance", 0);
                List<Effect> apparelEffects = loadEffects(itemElement, true);
                return new ApparelTemplate(id, name, description, scripts, price, apparelSlot, damageResistance, apparelEffects);
            case "consumable":
                ConsumableTemplate.ConsumableType consumableType = LoadUtils.attributeEnum(itemElement, "type", ConsumableTemplate.ConsumableType.class, ConsumableTemplate.ConsumableType.OTHER);
                List<Effect> consumableEffects = loadEffects(itemElement, false);
                return new ConsumableTemplate(id, name, description, scripts, price, consumableType, consumableEffects);
            case "weapon":
                WeaponTemplate.WeaponType weaponType = LoadUtils.attributeEnum(itemElement, "weaponType", WeaponTemplate.WeaponType.class, null);
                int weaponRate = LoadUtils.singleTagInt(itemElement, "rate", 1);
                Element damageElement = LoadUtils.singleChildWithName(itemElement, "damage");
                int weaponDamage = LoadUtils.attributeInt(damageElement, "base", 0);
                int critDamage = LoadUtils.attributeInt(damageElement, "crit", 0);
                Element rangeElement = LoadUtils.singleChildWithName(itemElement, "range");
                int weaponRangeMin = LoadUtils.attributeInt(rangeElement, "min", 0);
                int weaponRangeMax = LoadUtils.attributeInt(rangeElement, "max", 0);
                float weaponAccuracyBonus = LoadUtils.singleTagFloat(itemElement, "accuracyBonus", 0.0f);
                boolean weaponSilenced = LoadUtils.singleTagBoolean(itemElement, "silenced", false);
                int weaponClipSize = LoadUtils.singleTagInt(itemElement, "clipSize", 0);
                String weaponAmmoType = LoadUtils.singleTag(itemElement, "ammo", null);
                return new WeaponTemplate(id, name, description, scripts, price, weaponType, weaponDamage, weaponRate, critDamage, weaponRangeMin, weaponRangeMax, weaponClipSize, weaponAccuracyBonus, weaponSilenced, weaponAmmoType);
            case "note":
                List<String> noteText = LoadUtils.listOfTags(LoadUtils.singleChildWithName(itemElement, "text"), "line");
                return new NoteTemplate(id, name, description, scripts, price, noteText);
            case "misc":
            default:
                return new MiscTemplate(id, name, description, scripts, price);
        }
    }

    private static List<Effect> loadEffects(Element effectsElement, boolean manualRemoval) {
        if(effectsElement == null) return new ArrayList<>();
        List<Element> effectElements = LoadUtils.directChildrenWithName(effectsElement, "effect");
        List<Effect> effects = new ArrayList<>();
        for(Element effectElement : effectElements) {
            effects.add(loadEffect(effectElement, manualRemoval));
        }
        return effects;
    }

    private static Effect loadEffect(Element effectElement, boolean manualRemoval) {
        if(effectElement == null) return null;
        String effectType = effectElement.getAttribute("type");
        int duration = LoadUtils.attributeInt(effectElement, "duration", 0);
        switch(effectType) {
            case "state":
                String state = LoadUtils.attribute(effectElement, "state", null);
                int stateAmount = LoadUtils.attributeInt(effectElement, "amount", 0);
                return new EffectStateInt(duration, manualRemoval, state, stateAmount);
            case "trigger":
                String trigger = LoadUtils.attribute(effectElement, "trigger", null);
                return new EffectTrigger(duration, manualRemoval, trigger);
            case "mod":
                String statMod = LoadUtils.attribute(effectElement, "stat", null);
                String statModValue = LoadUtils.attribute(effectElement, "amount", "0");
                boolean statModIsFloat = statModValue.contains(".");
                if (statModIsFloat) {
                    float statModValueFloat = Float.parseFloat(statModValue);
                    return new EffectStatModFloat(duration, manualRemoval, statMod, statModValueFloat);
                } else {
                    int statModValueInt = Integer.parseInt(statModValue);
                    return new EffectStatModInt(duration, manualRemoval, statMod, statModValueInt);
                }
            case "mult":
                String statMult = LoadUtils.attribute(effectElement, "stat", null);
                float statMultAmount = LoadUtils.attributeFloat(effectElement, "amount", 0.0f);
                return new EffectStatMult(duration, manualRemoval, statMult, statMultAmount);
            default:
                return null;
        }
    }

    private static LootTable loadLootTable(Element tableElement, boolean useAllDefault) {
        if(tableElement == null) return null;
        String tableID = tableElement.getAttribute("id");
        boolean useAll = LoadUtils.attributeBool(tableElement, "useAll", useAllDefault);
        List<Element> entryItems = LoadUtils.directChildrenWithName(tableElement, "item");
        List<Element> entryTables = LoadUtils.directChildrenWithName(tableElement, "table");
        List<LootTableEntry> entries = new ArrayList<>();
        for (Element entryItem : entryItems) {
            LootTableEntry entry = loadLootTableEntry(entryItem, false);
            entries.add(entry);
        }
        for (Element entryTable : entryTables) {
            LootTableEntry entry = loadLootTableEntry(entryTable, true);
            entries.add(entry);
        }
        return new LootTable(tableID, useAll, entries);
    }

    private static LootTableEntry loadLootTableEntry(Element entryElement, boolean isTable) {
        if(entryElement == null) return null;
        String referenceID = entryElement.getTextContent();
        float chance = LoadUtils.attributeFloat(entryElement, "chance", 1.0f);
        int count = LoadUtils.attributeInt(entryElement, "count", 1);
        int countMin = LoadUtils.attributeInt(entryElement, "countMin", -1);
        int countMax = LoadUtils.attributeInt(entryElement, "countMax", -1);
        if (countMin == -1 || countMax == -1) {
            return new LootTableEntry(referenceID, isTable, chance, count, count);
        } else {
            return new LootTableEntry(referenceID, isTable, chance, countMin, countMax);
        }
    }

    private static Scene loadScene(Element sceneElement) throws ParserConfigurationException, SAXException, IOException {
        if(sceneElement == null) return null;
        boolean isRepeatable = LoadUtils.attributeBool(sceneElement, "isRepeatable", true);
        int priority = LoadUtils.attributeInt(sceneElement, "priority", 1);
        String sceneID = sceneElement.getAttribute("id");
        Condition condition = loadCondition(LoadUtils.singleChildWithName(sceneElement, "condition"));
        List<SceneLine> lines = new ArrayList<>();
        for(Element lineElement : LoadUtils.directChildrenWithName(sceneElement, "line")) {
            SceneLine line = loadSceneLine(lineElement);
            lines.add(line);
        }
        int cooldown = LoadUtils.attributeInt(sceneElement, "cooldown", 0);
        return new Scene(sceneID, condition, lines, isRepeatable, priority, cooldown);
    }

    private static SceneLine loadSceneLine(Element lineElement) throws ParserConfigurationException, SAXException, IOException {
        if(lineElement == null) return null;
        Condition condition = loadCondition(LoadUtils.singleChildWithName(lineElement, "condition"));
        List<String> text = LoadUtils.listOfTags(lineElement, "text");
        Script script = loadScript(LoadUtils.singleChildWithName(lineElement, "script"));
        return new SceneLine(condition, text, script);
    }

    private static Room loadRoom(Game game, Element roomElement) throws ParserConfigurationException, IOException, SAXException {
        if(roomElement == null) return null;
        String roomID = roomElement.getAttribute("id");
        Element roomNameElement = LoadUtils.singleChildWithName(roomElement, "name");
        String roomName = roomNameElement.getTextContent();
        boolean roomNameIsProper = LoadUtils.attributeBool(roomNameElement, "proper", false);
        Scene roomDescription = loadScene(LoadUtils.singleChildWithName(roomElement, "description"));
        String roomOwnerFaction = LoadUtils.attribute(roomElement, "faction", null);
        Map<String, Script> roomScripts = loadScriptsWithTriggers(roomElement);

        List<Element> areaElements = LoadUtils.directChildrenWithName(roomElement, "area");
        Set<Area> areas = new HashSet<>();
        for(Element areaElement : areaElements) {
            Area area = loadArea(game, areaElement, roomID);
            areas.add(area);
            game.data().addArea(area.getID(), area);
        }
        return new Room(game, roomID, roomName, roomNameIsProper, roomDescription, roomOwnerFaction, areas, roomScripts);
    }

    private static Area loadArea(Game game, Element areaElement, String roomID) throws ParserConfigurationException, IOException, SAXException {
        if(areaElement == null) return null;
        String areaID = areaElement.getAttribute("id");
        String landmarkID = LoadUtils.attribute(areaElement, "landmark", null);
        Scene description = loadScene(LoadUtils.singleChildWithName(areaElement, "description"));
        Element areaOwnerElement = LoadUtils.singleChildWithName(areaElement, "owner");
        String areaOwnerFaction = (areaOwnerElement != null ? areaOwnerElement.getTextContent() : null);
        boolean areaIsPrivate = LoadUtils.attributeBool(areaOwnerElement, "private", false);

        Element linksElement = LoadUtils.singleChildWithName(areaElement, "links");
        List<Element> linkElements = LoadUtils.directChildrenWithName(linksElement, "link");
        Map<String, AreaLink> linkSet = new HashMap<>();
        for(Element linkElement : linkElements) {
            String linkAreaID = linkElement.getTextContent();
            AreaLink.RelativeHeight linkHeight = LoadUtils.attributeEnum(linkElement, "height", AreaLink.RelativeHeight.class, AreaLink.RelativeHeight.EQUAL);
            AreaLink.AreaLinkType linkType = LoadUtils.attributeEnum(linkElement, "type", AreaLink.AreaLinkType.class, AreaLink.AreaLinkType.DIRECT);
            int linkDistance = LoadUtils.attributeInt(linkElement, "dist", 1);
            linkSet.put(linkAreaID, new AreaLink(linkAreaID, linkHeight, linkType, linkDistance));
        }

        Map<String, Script> areaScripts = loadScriptsWithTriggers(areaElement);

        Area area = new Area(game, areaID, landmarkID, description, roomID, areaOwnerFaction, areaIsPrivate, linkSet, areaScripts);

        List<Element> objectElements = LoadUtils.directChildrenWithName(areaElement, "object");
        for(Element objectElement : objectElements) {
            WorldObject object = loadObject(game, objectElement, area);
            game.data().addObject(object.getID(), object);
        }

        List<Element> actorElements = LoadUtils.directChildrenWithName(areaElement, "actor");
        for(Element actorElement : actorElements) {
            Actor actor = loadActorInstance(game, actorElement, area);
            game.data().addActor(actor.getID(), actor);
        }

        return area;
    }

    private static WorldObject loadObject(Game game, Element objectElement, Area area) throws ParserConfigurationException, IOException, SAXException {
        if(objectElement == null) return null;
        String objectType = objectElement.getAttribute("type");
        String objectName = LoadUtils.singleTag(objectElement, "name", null);
        String objectID = objectElement.getAttribute("id");
        String objectDescription = LoadUtils.singleTag(objectElement, "description", null);
        Map<String, Script> objectScripts = loadScriptsWithTriggers(objectElement);
        switch(objectType) {
            case "door":
                String doorLink = LoadUtils.attribute(objectElement, "link", null);
                Lock doorLock = loadLock(objectElement, objectID);
                return new ObjectDoor(game, objectID, area, objectName, objectDescription, objectScripts, doorLink, doorLock);
            case "elevator":
                Element floorElement = LoadUtils.singleChildWithName(objectElement, "floor");
                int floorNumber = LoadUtils.singleTagInt(floorElement, "number", 1);
                String floorName = floorElement.getTextContent();
                boolean elevatorStartLocked = LoadUtils.singleTagBoolean(objectElement, "startLocked", false);
                Set<String> linkedElevatorIDs = LoadUtils.setOfTags(objectElement, "link");
                return new ObjectElevator(game, objectID, area, objectName, objectDescription, objectScripts, floorNumber, floorName, linkedElevatorIDs, elevatorStartLocked);
            case "sign":
                List<String> signText = LoadUtils.listOfTags(LoadUtils.singleChildWithName(objectElement, "text"), "line");
                return new ObjectSign(game, objectID, area, objectName, objectDescription, objectScripts, signText);
            case "chair":
                return new ObjectChair(game, objectID, area, objectName, objectDescription, objectScripts);
            case "bed":
                return new ObjectBed(game, objectID, area, objectName, objectDescription, objectScripts);
            case "cover":
                return new ObjectCover(game, objectID, area, objectName, objectDescription, objectScripts);
            case "vendingMachine":
                List<String> vendingItems = LoadUtils.listOfTags(objectElement, "item");
                return new ObjectVendingMachine(game, objectID, area, objectName, objectDescription, objectScripts, vendingItems);
            case "item":
                String itemID = LoadUtils.singleTag(objectElement, "item", null);
                int itemCount = LoadUtils.singleTagInt(objectElement, "count", 1);
                return new ObjectItem(game, objectID, area, ItemFactory.create(game, itemID), itemCount);
            case "container":
                LootTable containerLootTable = loadLootTable(LoadUtils.singleChildWithName(objectElement, "inventory"), true);
                return new ObjectContainer(game, objectID, area, objectName, objectDescription, objectScripts, containerLootTable);
            case "custom":
                List<ActionCustom> objectActions = loadCustomActions(objectElement, objectID);
                return new ObjectCustom(game, objectID, area, objectName, objectDescription, objectScripts, objectActions);
        }
        return null;
    }

    private static Lock loadLock(Element objectElement, String objectID) {
        Element lockElement = LoadUtils.singleChildWithName(objectElement, "lock");
        boolean startLocked = LoadUtils.attributeBool(lockElement, "startLocked", true);
        int lockpickLevel = LoadUtils.attributeInt(lockElement, "lockpick", 0);
        int hotwireLevel = LoadUtils.attributeInt(lockElement, "hotwire", 0);
        Set<String> keys = LoadUtils.setOfTags(lockElement, "key");
        return new Lock(objectID, startLocked, keys, lockpickLevel, hotwireLevel);
    }

    private static List<ActionCustom> loadCustomActions(Element objectElement, String objectID) throws ParserConfigurationException, IOException, SAXException {
        if(objectElement == null) return new ArrayList<>();
        List<ActionCustom> actions = new ArrayList<>();
        for (Element actionElement : LoadUtils.directChildrenWithName(objectElement, "action")) {
            String prompt = LoadUtils.singleTag(actionElement, "prompt", null);
            String description = LoadUtils.singleTag(actionElement, "description", null);
            Condition condition = loadCondition(LoadUtils.singleChildWithName(actionElement, "condition"));
            Script script = loadScript(LoadUtils.singleChildWithName(actionElement, "script"));
            actions.add(new ActionCustom(prompt, description, objectID, condition, script));
        }
        return actions;
    }

    private static Actor loadActorInstance(Game game, Element actorElement, Area area) throws ParserConfigurationException, IOException, SAXException {
        if(actorElement == null) return null;
        String ID = actorElement.getAttribute("id");
        String template = LoadUtils.attribute(actorElement, "template", null);
        String descriptor = LoadUtils.singleTag(actorElement, "descriptor", null);
        List<Behavior> behaviors = loadBehaviors(LoadUtils.singleChildWithName(actorElement, "behaviors"));
        boolean startDead = LoadUtils.singleTagBoolean(actorElement, "startDead", false);
        boolean startDisabled = LoadUtils.singleTagBoolean(actorElement, "startDisabled", false);
        return ActorFactory.create(game, ID, area, game.data().getActorTemplate(template), descriptor, behaviors, startDead, startDisabled);
    }

    private static List<Behavior> loadBehaviors(Element behaviorsElement) throws ParserConfigurationException, IOException, SAXException {
        if(behaviorsElement == null) return new ArrayList<>();
        List<Behavior> behaviors = new ArrayList<>();
        for(Element behaviorElement : LoadUtils.directChildrenWithName(behaviorsElement, "behavior")) {
            Behavior behavior;
            String type = LoadUtils.attribute(behaviorElement, "type", null);
            Condition condition = loadCondition(LoadUtils.singleChildWithName(behaviorElement, "condition"));
            int duration = LoadUtils.attributeInt(behaviorElement, "duration", 0);
            List<Scene> idleScenes = new ArrayList<>();
            List<Element> sceneElements = LoadUtils.directChildrenWithName(behaviorElement, "idleScene");
            for (Element sceneElement : sceneElements) {
                Scene scene = loadScene(sceneElement);
                idleScenes.add(scene);
            }
            switch(type) {
                case "area":
                    String areaTarget = LoadUtils.attribute(behaviorElement, "area", null);
                    behavior = new BehaviorArea(condition, duration, idleScenes, areaTarget);
                    break;
                case "object":
                    String objectTarget = LoadUtils.attribute(behaviorElement, "object", null);
                    behavior = new BehaviorObject(condition, duration, idleScenes, objectTarget);
                    break;
                case "guard":
                    String guardTarget = LoadUtils.attribute(behaviorElement, "object", null);
                    behavior = new BehaviorGuard(condition, duration, idleScenes, guardTarget);
                    break;
                case "sandbox":
                    String startArea = LoadUtils.attribute(behaviorElement, "area", null);
                    behavior = new BehaviorSandbox(condition, duration, idleScenes, startArea);
                    break;
                case "sleep":
                    String bedTarget = LoadUtils.attribute(behaviorElement, "bed", null);
                    behavior = new BehaviorSleep(condition, idleScenes, bedTarget);
                    break;
                case "vendor":
                    String vendorArea = LoadUtils.attribute(behaviorElement, "area", null);
                    behavior = new BehaviorVendor(condition, duration, idleScenes, vendorArea);
                    break;
                case "cycle":
                    List<Behavior> cycleBehaviors = loadBehaviors(behaviorElement);
                    behavior = new BehaviorCycle(condition, cycleBehaviors);
                    break;
                default:
                    behavior = null;
                    break;
            }
            behaviors.add(behavior);
        }
        return behaviors;
    }

}
