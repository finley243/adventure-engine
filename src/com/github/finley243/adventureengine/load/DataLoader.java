package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.actor.*;
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
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;
import com.github.finley243.adventureengine.world.environment.Room;
import com.github.finley243.adventureengine.world.item.ItemFactory;
import com.github.finley243.adventureengine.world.item.LootTable;
import com.github.finley243.adventureengine.world.item.LootTableEntry;
import com.github.finley243.adventureengine.world.item.template.*;
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
                    game.data().addActorStats(actor.getID(), actor);
                }
                List<Element> items = LoadUtils.directChildrenWithName(rootElement, "item");
                for (Element itemElement : items) {
                    ItemTemplate item = loadItem(itemElement);
                    game.data().addItem(item.getID(), item);
                }
                List<Element> tables = LoadUtils.directChildrenWithName(rootElement, "lootTable");
                for (Element tableElement : tables) {
                    LootTable table = loadTable(tableElement);
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

    private static ActorTemplate loadActor(Element actorElement) throws ParserConfigurationException, IOException, SAXException {
        String id = actorElement.getAttribute("id");
        String parentID = actorElement.getAttribute("parent");
        Element nameElement = LoadUtils.singleChildWithName(actorElement, "name");
        String name = nameElement != null ? nameElement.getTextContent() : null;
        boolean nameIsProper = nameElement != null && LoadUtils.boolAttribute(nameElement, "proper", false);
        Context.Pronoun pronoun = LoadUtils.singleTagEnum(actorElement, "pronoun", Context.Pronoun.class, Context.Pronoun.THEY);
        String faction = LoadUtils.singleTag(actorElement, "faction", "default");
        int hp = LoadUtils.singleTagInt(actorElement, "hp", 0);
        List<Limb> limbs = loadLimbs(LoadUtils.singleChildWithName(actorElement, "limbs"));
        String lootTable = LoadUtils.singleTag(actorElement, "lootTable", null);
        String topic = LoadUtils.singleTag(actorElement, "topic", null);
        Map<Actor.Attribute, Integer> attributes = loadAttributes(LoadUtils.singleChildWithName(actorElement, "attributes"));
        Map<Actor.Skill, Integer> skills = loadSkills(LoadUtils.singleChildWithName(actorElement, "skills"));
        Map<String, Script> scripts = loadScriptsWithTriggers(actorElement);
        Element vendorElement = LoadUtils.singleChildWithName(actorElement, "vendor");
        boolean isVendor = vendorElement != null;
        String vendorLootTable = null;
        Set<String> vendorBuyTags = new HashSet<>();
        boolean vendorBuyAll = false;
        boolean vendorStartDisabled = false;
        if(vendorElement != null) {
            vendorLootTable = LoadUtils.singleTag(vendorElement, "lootTable", null);
            vendorBuyTags = LoadUtils.setOfTags(vendorElement, "buyTag");
            vendorBuyAll = LoadUtils.boolAttribute(vendorElement, "buyAll", false);
            vendorStartDisabled = LoadUtils.singleTagBoolean(vendorElement, "startDisabled", false);
        }
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
        float hitChance = LoadUtils.singleTagFloat(element, "hitChance", 1.0f);
        float damageMult = LoadUtils.singleTagFloat(element, "damageMult", 1.0f);
        ApparelComponent.ApparelSlot apparelSlot = LoadUtils.singleTagEnum(element, "apparelSlot", ApparelComponent.ApparelSlot.class, ApparelComponent.ApparelSlot.TORSO);
        List<Effect> crippledEffects = loadEffects(LoadUtils.singleChildWithName(element, "effects"), false);
        return new Limb(name, hitChance, damageMult, apparelSlot, crippledEffects);
    }

    private static Map<Actor.Attribute, Integer> loadAttributes(Element element) {
        Map<Actor.Attribute, Integer> attributes = new EnumMap<>(Actor.Attribute.class);
        if(element == null) return attributes;
        for(Element attributeElement : LoadUtils.directChildrenWithName(element, "attribute")) {
            Actor.Attribute attribute = LoadUtils.enumAttribute(attributeElement, "key", Actor.Attribute.class, null);
            int value = Integer.parseInt(attributeElement.getTextContent());
            attributes.put(attribute, value);
        }
        return attributes;
    }

    private static Map<Actor.Skill, Integer> loadSkills(Element element) {
        Map<Actor.Skill, Integer> skills = new EnumMap<>(Actor.Skill.class);
        if(element == null) return skills;
        for(Element skillElement : LoadUtils.directChildrenWithName(element, "skill")) {
            Actor.Skill skill = LoadUtils.enumAttribute(skillElement, "key", Actor.Skill.class, null);
            int value = Integer.parseInt(skillElement.getTextContent());
            skills.put(skill, value);
        }
        return skills;
    }

    private static DialogueTopic loadTopic(Element topicElement) throws ParserConfigurationException, SAXException, IOException {
        String topicID = topicElement.getAttribute("id");
        DialogueTopic.TopicType type;
        switch(topicElement.getAttribute("type")) {
            case "sel":
                type = DialogueTopic.TopicType.SELECTOR;
                break;
            case "seq":
            default:
                type = DialogueTopic.TopicType.SEQUENTIAL;
                break;
        }
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
        return new DialogueTopic(topicID, lines, choices, type);
    }

    private static DialogueLine loadLine(Element lineElement) throws ParserConfigurationException, SAXException, IOException {
        boolean once = LoadUtils.boolAttribute(lineElement, "once", false);
        boolean exit = LoadUtils.boolAttribute(lineElement, "exit", false);
        String redirect = lineElement.getAttribute("redirect");
        if(redirect.isEmpty()) {
            redirect = null;
        }
        List<String> texts = LoadUtils.listOfTags(lineElement, "text");
        Condition condition = loadCondition(LoadUtils.singleChildWithName(lineElement, "condition"));
        Script script = loadScript(LoadUtils.singleChildWithName(lineElement, "script"));
        return new DialogueLine(texts, condition, script, once, exit, redirect);
    }

    private static DialogueChoice loadChoice(Element choiceElement) throws ParserConfigurationException, SAXException, IOException {
        boolean once = LoadUtils.boolAttribute(choiceElement, "once", false);
        String link = choiceElement.getAttribute("link");
        String prompt = LoadUtils.singleTag(choiceElement, "prompt", null);
        Element conditionElement = LoadUtils.singleChildWithName(choiceElement, "condition");
        Condition condition = loadCondition(conditionElement);
        return new DialogueChoice(link, prompt, condition, once);
    }

    private static Condition loadCondition(Element conditionElement) throws ParserConfigurationException, SAXException, IOException {
        if(conditionElement == null) return null;
        String type = LoadUtils.attribute(conditionElement, "type", "compound");
        boolean invert = LoadUtils.boolAttribute(conditionElement, "invert", false);
        ActorReference actorRef = loadActorReference(conditionElement, "actor");
        switch(type) {
            case "money":
                int moneyAmount = LoadUtils.singleTagInt(conditionElement, "value", 0);
                return new ConditionMoney(invert, actorRef, moneyAmount);
            case "var":
                String varID = LoadUtils.singleTag(conditionElement, "variable", null);
                Condition.Equality varEquality = LoadUtils.singleTagEnum(conditionElement, "equality", Condition.Equality.class, Condition.Equality.GREATER_EQUAL);
                int varValue = LoadUtils.singleTagInt(conditionElement, "value", 0);
                return new ConditionVariable(invert, varID, varEquality, varValue);
            case "attribute":
                Actor.Attribute attribute = LoadUtils.singleTagEnum(conditionElement, "attribute", Actor.Attribute.class, null);
                int attributeValue = LoadUtils.singleTagInt(conditionElement, "value", 0);
                return new ConditionAttribute(invert, actorRef, attribute, attributeValue);
            case "skill":
                Actor.Skill skill = LoadUtils.singleTagEnum(conditionElement, "skill", Actor.Skill.class, null);
                int skillValue = LoadUtils.singleTagInt(conditionElement, "value", 0);
                return new ConditionSkill(invert, actorRef, skill, skillValue);
            case "actorLocation":
                String actorArea = LoadUtils.singleTag(conditionElement, "area", null);
                String actorRoom = LoadUtils.singleTag(conditionElement, "room", null);
                boolean useRoom = actorArea == null;
                return new ConditionActorLocation(invert, actorRef, (useRoom ? actorRoom : actorArea), useRoom);
            case "actorAvailableForScene":
                return new ConditionActorAvailableForScene(invert, actorRef);
            case "actorDead":
                return new ConditionActorDead(invert, actorRef);
            case "actorHP":
                Condition.Equality hpEquality = LoadUtils.singleTagEnum(conditionElement, "equality", Condition.Equality.class, Condition.Equality.GREATER_EQUAL);
                float hpValue = LoadUtils.singleTagFloat(conditionElement, "value", 0);
                return new ConditionActorHP(invert, actorRef, hpEquality, hpValue);
            case "combatTarget":
                ActorReference targetRef = loadActorReference(conditionElement, "target");
                return new ConditionCombatTarget(invert, actorRef, targetRef);
            case "equippedItem":
                String itemTag = LoadUtils.singleTag(conditionElement, "tag", null);
                return new ConditionEquippedItem(invert, actorRef, itemTag);
            case "actorVisible":
                ActorReference visibleTargetRef = loadActorReference(conditionElement, "target");
                return new ConditionActorVisible(invert, actorRef, visibleTargetRef);
            case "inCombat":
                return new ConditionActorInCombat(invert, actorRef);
            case "compound":
            default:
                List<Condition> subConditions = loadSubConditions(conditionElement);
                boolean useOr = LoadUtils.attribute(conditionElement, "logic", "and").equalsIgnoreCase("or");
                return new ConditionCompound(invert, subConditions, useOr);
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
        Element conditionElement = LoadUtils.singleChildWithName(scriptElement, "condition");
        Condition condition = loadCondition(conditionElement);
        ActorReference actorRef = loadActorReference(scriptElement, "actor");
        switch(type) {
            case "external":
                String scriptID = LoadUtils.singleTag(scriptElement, "scriptID", null);
                return new ScriptExternal(condition, scriptID);
            case "money":
                int moneyValue = LoadUtils.singleTagInt(scriptElement, "value", 0);
                return new ScriptMoney(condition, actorRef, moneyValue);
            case "addItem":
                String addItemID = LoadUtils.singleTag(scriptElement, "item", null);
                return new ScriptAddItem(condition, actorRef, addItemID);
            case "scene":
                List<String> scenes = LoadUtils.listOfTags(scriptElement, "scene");
                return new ScriptScene(condition, scenes);
            case "varSet":
                String varSetID = LoadUtils.singleTag(scriptElement, "variable", null);
                int varSetValue = LoadUtils.singleTagInt(scriptElement, "value", 0);
                return new ScriptVariableSet(condition, varSetID, varSetValue);
            case "varMod":
                String varModID = LoadUtils.singleTag(scriptElement, "variable", null);
                int varModValue = LoadUtils.singleTagInt(scriptElement, "value", 0);
                return new ScriptVariableMod(condition, varModID, varModValue);
            case "dialogue":
                String topic = LoadUtils.singleTag(scriptElement, "topic", null);
                return new ScriptDialogue(condition, actorRef, topic);
            case "combat":
                ActorReference combatantRef = loadActorReference(scriptElement, "combatant");
                return new ScriptCombat(condition, actorRef, combatantRef);
            case "factionRelation":
                String targetFaction = LoadUtils.singleTag(scriptElement, "targetFaction", null);
                String relationFaction = LoadUtils.singleTag(scriptElement, "relationFaction", null);
                Faction.FactionRelation relation = factionRelationTag(scriptElement, "relation");
                return new ScriptFactionRelation(condition, targetFaction, relationFaction, relation);
            case "moveActor":
                String moveActorArea = LoadUtils.singleTag(scriptElement, "area", null);
                return new ScriptMoveActor(condition, actorRef, moveActorArea);
            case "actorState":
                boolean actorEnabled = LoadUtils.singleTagBoolean(scriptElement, "enabled", true);
                return new ScriptActorState(condition, actorRef, actorEnabled);
            case "bark":
                List<String> barkLines = LoadUtils.listOfTags(scriptElement, "line");
                float barkChance = LoadUtils.singleTagFloat(scriptElement, "chance", 1.0f);
                return new ScriptBark(condition, actorRef, barkLines, barkChance);
            case "nearestActorScript":
                String nearestTrigger = LoadUtils.singleTag(scriptElement, "trigger", null);
                return new ScriptNearestActorWithScript(condition, nearestTrigger);
            case "compound":
            default:
                List<Script> subScripts = loadSubScripts(scriptElement);
                boolean compoundSelect = LoadUtils.boolAttribute(scriptElement, "select", false);
                return new ScriptCompound(condition, subScripts, compoundSelect);
        }
    }

    private static ActorReference loadActorReference(Element parentElement, String name) {
        Element refElement = LoadUtils.singleChildWithName(parentElement, name);
        if(refElement == null) return new ActorReference(ActorReference.ReferenceType.SUBJECT, null);
        String targetTypeString = refElement.getAttribute("target");
        ActorReference.ReferenceType targetType;
        switch(targetTypeString) {
            case "player":
                targetType = ActorReference.ReferenceType.PLAYER;
                break;
            case "reference":
                targetType = ActorReference.ReferenceType.REFERENCE;
                break;
            case "subject":
            default:
                targetType = ActorReference.ReferenceType.SUBJECT;
                break;
        }
        String targetRef = refElement.getTextContent();
        return new ActorReference(targetType, targetRef);
    }

    private static Faction loadFaction(Element factionElement) {
        String id = factionElement.getAttribute("id");
        Faction.FactionRelation defaultRelation = factionRelationTag(factionElement, "default");
        Map<String, Faction.FactionRelation> relations = loadFactionRelations(factionElement);
        return new Faction(id, defaultRelation, relations);
    }

    private static Map<String, Faction.FactionRelation> loadFactionRelations(Element factionElement) {
        Map<String, Faction.FactionRelation> relations = new HashMap<>();
        List<Element> relationElements = LoadUtils.directChildrenWithName(factionElement, "relation");
        for(Element relationElement : relationElements) {
            String id = LoadUtils.singleTag(relationElement, "id", null);
            Faction.FactionRelation type = factionRelationTag(relationElement, "type");
            relations.put(id, type);
        }
        return relations;
    }

    private static Faction.FactionRelation factionRelationTag(Element element, String name) {
        String factionRelationString = LoadUtils.singleTag(element, name, null);
        switch(factionRelationString) {
            case "ASSIST":
                return Faction.FactionRelation.ASSIST;
            case "HOSTILE":
                return Faction.FactionRelation.HOSTILE;
            case "NEUTRAL":
            default:
                return Faction.FactionRelation.NEUTRAL;
        }
    }

    private static ItemTemplate loadItem(Element itemElement) throws ParserConfigurationException, IOException, SAXException {
        String type = itemElement.getAttribute("type");
        String id = itemElement.getAttribute("id");
        String name = LoadUtils.singleTag(itemElement, "name", null);
        String description = LoadUtils.singleTag(itemElement, "description", null);
        Map<String, Script> scripts = loadScriptsWithTriggers(itemElement);
        int price = LoadUtils.singleTagInt(itemElement, "price", 0);
        switch(type) {
            case "apparel":
                ApparelComponent.ApparelSlot apparelSlot = LoadUtils.singleTagEnum(itemElement, "slot", ApparelComponent.ApparelSlot.class, ApparelComponent.ApparelSlot.TORSO);
                int damageResistance = LoadUtils.singleTagInt(itemElement, "damageResistance", 0);
                List<Effect> apparelEffects = loadEffects(itemElement, true);
                return new ApparelTemplate(id, name, description, scripts, price, apparelSlot, damageResistance, apparelEffects);
            case "consumable":
                ConsumableTemplate.ConsumableType consumableType = LoadUtils.singleTagEnum(itemElement, "type", ConsumableTemplate.ConsumableType.class, ConsumableTemplate.ConsumableType.OTHER);
                List<Effect> consumableEffects = loadEffects(LoadUtils.singleChildWithName(itemElement, "effects"), false);
                return new ConsumableTemplate(id, name, description, scripts, price, consumableType, consumableEffects);
            case "key":
                return new KeyTemplate(id, name, description, scripts);
            case "weapon":
                WeaponTemplate.WeaponType weaponType = LoadUtils.singleTagEnum(itemElement, "type", WeaponTemplate.WeaponType.class, null);
                int weaponDamage = LoadUtils.singleTagInt(itemElement, "damage", 0);
                int weaponRate = LoadUtils.singleTagInt(itemElement, "rate", 1);
                int critDamage = LoadUtils.singleTagInt(itemElement, "critDamage", 0);
                int weaponRangeMin = LoadUtils.singleTagInt(itemElement, "rangeMin", 0);
                int weaponRangeMax = LoadUtils.singleTagInt(itemElement, "rangeMax", 0);
                int weaponClipSize = LoadUtils.singleTagInt(itemElement, "clipSize", 0);
                float weaponAccuracyBonus = LoadUtils.singleTagFloat(itemElement, "accuracyBonus", 0.0f);
                boolean weaponSilenced = LoadUtils.singleTagBoolean(itemElement, "silenced", false);
                return new WeaponTemplate(id, name, description, scripts, price, weaponType, weaponDamage, weaponRate, critDamage, weaponRangeMin, weaponRangeMax, weaponClipSize, weaponAccuracyBonus, weaponSilenced);
            case "junk":
                return new JunkTemplate(id, name, description, scripts, price);
        }
        return null;
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
        String effectType = effectElement.getAttribute("type");
        int duration = LoadUtils.singleTagInt(effectElement, "duration", 0);
        int amount = LoadUtils.singleTagInt(effectElement, "amount", 0);
        switch(effectType) {
            case "heal":
                return new EffectHealInstant(amount);
            case "heal_over_time":
                return new EffectHealOverTime(duration, manualRemoval, amount);
            case "attribute":
                Actor.Attribute attribute = LoadUtils.singleTagEnum(effectElement, "attribute", Actor.Attribute.class, null);
                return new EffectAttribute(duration, manualRemoval, attribute, amount);
            case "skill":
                Actor.Skill skill = LoadUtils.singleTagEnum(effectElement, "skill", Actor.Skill.class, null);
                return new EffectSkill(duration, manualRemoval, skill, amount);
            case "drop_equipped":
                return new EffectDropEquipped();
            default:
                return null;
        }
    }

    private static LootTable loadTable(Element tableElement) {
        String tableID = tableElement.getAttribute("id");
        boolean useAll = LoadUtils.boolAttribute(tableElement, "useAll", false);
        List<Element> entryElements = LoadUtils.directChildrenWithName(tableElement, "entry");
        List<LootTableEntry> entries = new ArrayList<>();
        for(Element entryElement : entryElements) {
            LootTableEntry entry = loadEntry(entryElement);
            entries.add(entry);
        }
        return new LootTable(tableID, useAll, entries);
    }

    private static LootTableEntry loadEntry(Element entryElement) {
        String referenceID = LoadUtils.singleTag(entryElement, "reference", null);
        boolean isLootTable;
        Element referenceElement = LoadUtils.singleChildWithName(entryElement, "reference");
        isLootTable = LoadUtils.boolAttribute(referenceElement, "table", false);
        float chance = LoadUtils.singleTagFloat(entryElement, "chance", 1.0f);
        int count = LoadUtils.singleTagInt(entryElement, "count", 1);
        return new LootTableEntry(referenceID, isLootTable, chance, count);
    }

    private static Scene loadScene(Element sceneElement) throws ParserConfigurationException, SAXException, IOException {
        boolean isRepeatable = LoadUtils.boolAttribute(sceneElement, "isRepeatable", true);
        boolean playImmediately = LoadUtils.boolAttribute(sceneElement, "playImmediately", false);
        String sceneID = sceneElement.getAttribute("id");
        Element conditionElement = LoadUtils.singleChildWithName(sceneElement, "condition");
        Condition condition = loadCondition(conditionElement);
        List<Element> lineElements = LoadUtils.directChildrenWithName(sceneElement, "line");
        List<SceneLine> lines = new ArrayList<>();
        for(Element lineElement : lineElements) {
            SceneLine line = loadSceneLine(lineElement);
            lines.add(line);
        }
        float chance = LoadUtils.singleTagFloat(sceneElement, "chance", 1.0f);
        int cooldown = LoadUtils.singleTagInt(sceneElement, "cooldown", 0);
        return new Scene(sceneID, condition, lines, isRepeatable, playImmediately, chance, cooldown);
    }

    private static SceneLine loadSceneLine(Element lineElement) throws ParserConfigurationException, SAXException, IOException {
        Element conditionElement = LoadUtils.singleChildWithName(lineElement, "condition");
        Condition condition = loadCondition(conditionElement);
        List<String> text = LoadUtils.listOfTags(lineElement, "text");
        Script script = loadScript(LoadUtils.singleChildWithName(lineElement, "script"));
        return new SceneLine(condition, text, script);
    }

    private static Room loadRoom(Game game, Element roomElement) throws ParserConfigurationException, IOException, SAXException {
        String roomID = roomElement.getAttribute("id");
        Element roomNameElement = LoadUtils.singleChildWithName(roomElement, "name");
        String roomName = roomNameElement.getTextContent();
        boolean roomNameIsProper = LoadUtils.boolAttribute(roomNameElement, "proper", false);
        String roomDescription = LoadUtils.singleTag(roomElement, "roomDescription", null);
        String roomOwnerFaction = LoadUtils.singleTag(roomElement, "ownerFaction", null);
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
        String areaID = areaElement.getAttribute("id");
        Element nameElement = LoadUtils.singleChildWithName(areaElement, "name");
        String name = nameElement.getTextContent();
        boolean isProperName = LoadUtils.boolAttribute(nameElement, "proper", false);
        Area.AreaNameType nameType = LoadUtils.singleTagEnum(nameElement, "type", Area.AreaNameType.class, Area.AreaNameType.IN);
        String description = LoadUtils.singleTag(areaElement, "areaDescription", null);
        String areaOwnerFaction = LoadUtils.singleTag(areaElement, "ownerFaction", null);
        Element areaOwnerElement = LoadUtils.singleChildWithName(areaElement, "ownerFaction");
        boolean areaIsPrivate = false;
        if(areaOwnerElement != null) {
            areaIsPrivate = LoadUtils.boolAttribute(areaOwnerElement, "private", false);
        }

        Element linksElement = LoadUtils.singleChildWithName(areaElement, "links");
        List<Element> linkElements = LoadUtils.directChildrenWithName(linksElement, "link");
        Map<String, AreaLink> linkSet = new HashMap<>();
        for(Element linkElement : linkElements) {
            String linkAreaID = linkElement.getTextContent();
            AreaLink.RelativeDirection linkDirection = LoadUtils.enumAttribute(linkElement, "dir", AreaLink.RelativeDirection.class, AreaLink.RelativeDirection.NORTH);
            AreaLink.RelativeHeight linkHeight = LoadUtils.enumAttribute(linkElement, "height", AreaLink.RelativeHeight.class, AreaLink.RelativeHeight.EQUAL);
            AreaLink.AreaLinkType linkType = LoadUtils.enumAttribute(linkElement, "type", AreaLink.AreaLinkType.class, AreaLink.AreaLinkType.DIRECT);
            int linkDistance = LoadUtils.intAttribute(linkElement, "dist", 1);
            linkSet.put(linkAreaID, new AreaLink(linkAreaID, linkDirection, linkHeight, linkType, linkDistance));
        }

        Map<String, Script> areaScripts = loadScriptsWithTriggers(areaElement);

        Area area = new Area(game, areaID, name, description, isProperName, nameType, roomID, areaOwnerFaction, areaIsPrivate, linkSet, areaScripts);

        Element objectsElement = LoadUtils.singleChildWithName(areaElement, "objects");
        List<Element> objectElements = LoadUtils.directChildrenWithName(objectsElement, "object");
        for(Element objectElement : objectElements) {
            WorldObject object = loadObject(game, objectElement, area);
            game.data().addObject(object.getID(), object);
            // TODO - Replace with enable/disable system in WorldObject (similar to Actor)
            area.addObject(object);
        }

        Element actorsElement = LoadUtils.singleChildWithName(areaElement, "actors");
        List<Element> actorElements = LoadUtils.directChildrenWithName(actorsElement, "actor");
        for(Element actorElement : actorElements) {
            Actor actor = loadActorInstance(game, actorElement, area);
            game.data().addActor(actor.getID(), actor);
        }

        return area;
    }

    private static WorldObject loadObject(Game game, Element objectElement, Area area) throws ParserConfigurationException, IOException, SAXException {
        String objectType = objectElement.getAttribute("type");
        String objectName = LoadUtils.singleTag(objectElement, "name", null);
        String objectID = objectElement.getAttribute("id");
        String objectDescription = LoadUtils.singleTag(objectElement, "description", null);
        Map<String, Script> objectScripts = loadScriptsWithTriggers(objectElement);
        switch(objectType) {
            case "exit":
                String exitLink = LoadUtils.singleTag(objectElement, "link", null);
                Set<String> exitKeys = LoadUtils.setOfTags(objectElement, "key");
                return new ObjectExit(game, objectID, area, objectName, objectDescription, objectScripts, exitLink, exitKeys);
            case "elevator":
                int floorNumber = LoadUtils.singleTagInt(objectElement, "floorNumber", 1);
                String floorName = LoadUtils.singleTag(objectElement, "floorName", null);
                boolean elevatorStartLocked = LoadUtils.singleTagBoolean(objectElement, "startLocked", false);
                Set<String> linkedElevatorIDs = LoadUtils.setOfTags(LoadUtils.singleChildWithName(objectElement, "links"), "link");
                return new ObjectElevator(game, objectID, area, objectName, objectDescription, objectScripts, floorNumber, floorName, linkedElevatorIDs, elevatorStartLocked);
            case "sign":
                List<String> signText = LoadUtils.listOfTags(LoadUtils.singleChildWithName(objectElement, "lines"), "text");
                return new ObjectSign(game, objectID, area, objectName, objectDescription, objectScripts, signText);
            case "chair":
                return new ObjectChair(game, objectID, area, objectName, objectDescription, objectScripts);
            case "cover":
                ObjectCover.CoverDirection coverDirection = LoadUtils.singleTagEnum(objectElement, "direction", ObjectCover.CoverDirection.class, null);
                return new ObjectCover(game, objectID, area, objectName, objectDescription, objectScripts, coverDirection);
            case "vending_machine":
                List<String> vendingItems = LoadUtils.listOfTags(LoadUtils.singleChildWithName(objectElement, "items"), "item");
                return new ObjectVendingMachine(game, objectID, area, objectName, objectDescription, objectScripts, vendingItems);
            case "item":
                String itemID = LoadUtils.singleTag(objectElement, "item", null);
                return ItemFactory.create(game, itemID, objectID, area);
            case "container":
                String containerLootTable = LoadUtils.singleTag(objectElement, "lootTable", null);
                return new ObjectContainer(game, objectID, area, objectName, objectDescription, objectScripts, containerLootTable);
            case "custom":
                List<ActionCustom> objectActions = loadCustomActions(objectElement);
                return new ObjectCustom(game, objectID, area, objectName, objectDescription, objectScripts, objectActions);
        }
        return null;
    }

    private static List<ActionCustom> loadCustomActions(Element objectElement) throws ParserConfigurationException, IOException, SAXException {
        List<ActionCustom> actions = new ArrayList<>();
        for (Element actionElement : LoadUtils.directChildrenWithName(objectElement, "action")) {
            String prompt = LoadUtils.singleTag(actionElement, "prompt", null);
            String description = LoadUtils.singleTag(actionElement, "description", null);
            Condition condition = loadCondition(LoadUtils.singleChildWithName(actionElement, "condition"));
            Script script = loadScript(LoadUtils.singleChildWithName(actionElement, "script"));
            actions.add(new ActionCustom(prompt, description, condition, script));
        }
        return actions;
    }

    private static Actor loadActorInstance(Game game, Element actorElement, Area area) {
        String ID = actorElement.getAttribute("id");
        String stats = LoadUtils.singleTag(actorElement, "stats", null);
        String descriptor = LoadUtils.singleTag(actorElement, "descriptor", null);
        List<String> idle;
        Element idleElement = LoadUtils.singleChildWithName(actorElement, "idle");
        if(idleElement != null) {
            idle = LoadUtils.listOfTags(idleElement, "area");
        } else {
            idle = new ArrayList<>();
        }
        boolean preventMovement = LoadUtils.singleTagBoolean(actorElement, "preventMovement", false);
        boolean startDead = LoadUtils.singleTagBoolean(actorElement, "startDead", false);
        boolean startDisabled = LoadUtils.singleTagBoolean(actorElement, "startDisabled", false);
        return ActorFactory.create(game, ID, area, game.data().getActorStats(stats), descriptor, idle, preventMovement, startDead, startDisabled);
    }

}
