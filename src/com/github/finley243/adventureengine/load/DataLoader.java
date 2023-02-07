package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.action.attack.ActionAttack;
import com.github.finley243.adventureengine.actor.*;
import com.github.finley243.adventureengine.actor.ai.Idle;
import com.github.finley243.adventureengine.actor.ai.behavior.*;
import com.github.finley243.adventureengine.combat.Damage;
import com.github.finley243.adventureengine.combat.WeaponAttackType;
import com.github.finley243.adventureengine.combat.WeaponClass;
import com.github.finley243.adventureengine.condition.*;
import com.github.finley243.adventureengine.effect.*;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemFactory;
import com.github.finley243.adventureengine.item.LootTable;
import com.github.finley243.adventureengine.item.LootTableEntry;
import com.github.finley243.adventureengine.item.template.*;
import com.github.finley243.adventureengine.network.*;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.scene.SceneChoice;
import com.github.finley243.adventureengine.scene.SceneLine;
import com.github.finley243.adventureengine.script.*;
import com.github.finley243.adventureengine.stat.StatHolderReference;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.variable.Variable;
import com.github.finley243.adventureengine.variable.VariableGlobal;
import com.github.finley243.adventureengine.variable.VariableLiteral;
import com.github.finley243.adventureengine.variable.VariableStat;
import com.github.finley243.adventureengine.world.Lock;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;
import com.github.finley243.adventureengine.world.environment.Room;
import com.github.finley243.adventureengine.world.environment.RoomLink;
import com.github.finley243.adventureengine.world.object.*;
import com.github.finley243.adventureengine.world.object.component.ComponentLink;
import com.github.finley243.adventureengine.world.object.template.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
                    Node currentChild = rootElement.getFirstChild();
                    while (currentChild != null) {
                        if (currentChild.getNodeType() == Node.ELEMENT_NODE) {
                            switch (currentChild.getNodeName()) {
                                case "faction":
                                    Faction faction = loadFaction((Element) currentChild);
                                    game.data().addFaction(faction.getID(), faction);
                                    break;
                                case "scene":
                                    Scene scene = loadScene(game, (Element) currentChild);
                                    game.data().addScene(scene.getID(), scene);
                                    break;
                                case "actor":
                                    ActorTemplate actor = loadActor(game, (Element) currentChild);
                                    game.data().addActorTemplate(actor.getID(), actor);
                                    break;
                                case "object":
                                    ObjectTemplate object = loadObjectTemplate(game, (Element) currentChild);
                                    game.data().addObjectTemplate(object.getID(), object);
                                    break;
                                case "objectComponent":
                                    ObjectComponentTemplate objectComponent = loadObjectComponentTemplate(game, (Element) currentChild);
                                    game.data().addObjectComponentTemplate(objectComponent.getID(), objectComponent);
                                    break;
                                case "item":
                                    ItemTemplate item = loadItemTemplate(game, (Element) currentChild);
                                    game.data().addItem(item.getID(), item);
                                    break;
                                case "lootTable":
                                    LootTable table = loadLootTable((Element) currentChild, false);
                                    game.data().addLootTable(table.getID(), table);
                                    break;
                                case "weaponClass":
                                    WeaponClass weaponClass = loadWeaponClass((Element) currentChild);
                                    game.data().addWeaponClass(weaponClass.getID(), weaponClass);
                                    break;
                                case "attackType":
                                    WeaponAttackType attackType = loadWeaponAttackType((Element) currentChild);
                                    game.data().addAttackType(attackType.getID(), attackType);
                                    break;
                                case "room":
                                    Room room = loadRoom(game, (Element) currentChild);
                                    game.data().addRoom(room.getID(), room);
                                    break;
                                case "script":
                                    String scriptID = LoadUtils.attribute((Element) currentChild, "id", null);
                                    Script script = loadScript((Element) currentChild);
                                    game.data().addScript(scriptID, script);
                                    break;
                                case "effect":
                                    String effectID = LoadUtils.attribute((Element) currentChild, "id", null);
                                    Effect effect = loadEffect((Element) currentChild);
                                    game.data().addEffect(effectID, effect);
                                    break;
                                case "network":
                                    Network network = loadNetwork((Element) currentChild);
                                    game.data().addNetwork(network.getID(), network);
                                    break;
                            }
                        }
                        currentChild = currentChild.getNextSibling();
                    }
                }
            }
        }
    }

    private static ActorTemplate loadActor(Game game, Element actorElement) throws ParserConfigurationException, IOException, SAXException {
        String id = LoadUtils.attribute(actorElement, "id", null);
        String parentID = LoadUtils.attribute(actorElement, "parent", null);
        Element nameElement = LoadUtils.singleChildWithName(actorElement, "name");
        String name = nameElement != null ? nameElement.getTextContent() : null;
        Boolean nameIsProper = nameElement != null && LoadUtils.attributeBool(nameElement, "proper", false);
        Context.Pronoun pronoun = LoadUtils.attributeEnum(nameElement, "pronoun", Context.Pronoun.class, Context.Pronoun.THEY);
        String faction = LoadUtils.attribute(actorElement, "faction", null);
        Boolean isEnforcer = LoadUtils.attributeBool(actorElement, "isEnforcer", null);

        Integer hp = LoadUtils.attributeInt(actorElement, "hp", null);
        List<Limb> limbs = loadLimbs(actorElement);
        String defaultApparelSlot = LoadUtils.attribute(actorElement, "defaultApparelSlot", null);
        LootTable lootTable = loadLootTable(LoadUtils.singleChildWithName(actorElement, "inventory"), true);
        String dialogueStart = LoadUtils.attribute(actorElement, "dialogueStart", null);
        Map<Actor.Attribute, Integer> attributes = loadAttributes(actorElement);
        Map<Actor.Skill, Integer> skills = loadSkills(actorElement);
        Map<String, Script> scripts = loadScriptsWithTriggers(actorElement);

        Map<String, Bark> barks = new HashMap<>();
        for (Element barkElement : LoadUtils.directChildrenWithName(actorElement, "bark")) {
            String barkTrigger = LoadUtils.attribute(barkElement, "trigger", null);
            Bark.BarkResponseType responseType = LoadUtils.attributeEnum(barkElement, "response", Bark.BarkResponseType.class, Bark.BarkResponseType.NONE);
            List<String> visiblePhrases = LoadUtils.listOfTags(barkElement, "visible");
            List<String> nonVisiblePhrases = LoadUtils.listOfTags(barkElement, "nonVisible");
            barks.put(barkTrigger, new Bark(responseType, visiblePhrases, nonVisiblePhrases));
        }

        Element vendorElement = LoadUtils.singleChildWithName(actorElement, "vendor");
        Boolean isVendor = vendorElement != null;
        String vendorLootTable = LoadUtils.attribute(vendorElement, "lootTable", null);
        Set<String> vendorBuyTags = LoadUtils.setOfTags(vendorElement, "buyTag");
        Boolean vendorBuyAll = LoadUtils.attributeBool(vendorElement, "buyAll", false);
        Boolean vendorStartDisabled = LoadUtils.attributeBool(vendorElement, "startDisabled", false);
        return new ActorTemplate(game, id, parentID, name, nameIsProper, pronoun, faction, isEnforcer, hp, limbs, defaultApparelSlot, attributes, skills, lootTable, dialogueStart, scripts, barks, isVendor, vendorLootTable, vendorBuyTags, vendorBuyAll, vendorStartDisabled);
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
        String apparelSlot = LoadUtils.attribute(element, "apparelSlot", null);
        List<String> hitEffects = LoadUtils.listOfTags(element, "hitEffect");
        return new Limb(name, hitChance, damageMult, apparelSlot, hitEffects);
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

    private static Scene loadScene(Game game, Element sceneElement) throws ParserConfigurationException, SAXException, IOException {
        if (sceneElement == null) return null;
        String sceneID = sceneElement.getAttribute("id");
        Scene.SceneType type;
        switch (sceneElement.getAttribute("type")) {
            case "random":
                type = Scene.SceneType.RANDOM;
                break;
            case "select":
                type = Scene.SceneType.SELECTOR;
                break;
            case "all":
            default:
                type = Scene.SceneType.SEQUENTIAL;
                break;
        }
        Condition condition = loadCondition(LoadUtils.singleChildWithName(sceneElement, "condition"));
        boolean once = LoadUtils.attributeBool(sceneElement, "once", false);
        int priority = LoadUtils.attributeInt(sceneElement, "priority", 1);
        List<Element> lineElements = LoadUtils.directChildrenWithName(sceneElement, "line");
        List<SceneLine> lines = new ArrayList<>();
        for(Element lineElement : lineElements) {
            SceneLine line = loadSceneLine(lineElement);
            lines.add(line);
        }
        List<Element> choiceElements = LoadUtils.directChildrenWithName(sceneElement, "choice");
        List<SceneChoice> choices = new ArrayList<>();
        for(Element choiceElement : choiceElements) {
            SceneChoice choice = loadSceneChoice(choiceElement);
            choices.add(choice);
        }
        return new Scene(game, sceneID, condition, once, priority, lines, choices, type);
    }

    private static SceneLine loadSceneLine(Element lineElement) throws ParserConfigurationException, SAXException, IOException {
        boolean once = LoadUtils.attributeBool(lineElement, "once", false);
        boolean exit = LoadUtils.attributeBool(lineElement, "exit", false);
        String redirect = LoadUtils.attribute(lineElement, "redirect", null);
        String from = LoadUtils.attribute(lineElement, "from", null);
        List<String> texts = LoadUtils.listOfTags(lineElement, "text");
        Condition condition = loadCondition(LoadUtils.singleChildWithName(lineElement, "condition"));
        Script script = loadScript(LoadUtils.singleChildWithName(lineElement, "script"));
        return new SceneLine(texts, condition, script, once, exit, redirect, from);
    }

    private static SceneChoice loadSceneChoice(Element choiceElement) {
        String link = choiceElement.getAttribute("link");
        String prompt = choiceElement.getTextContent();
        return new SceneChoice(link, prompt);
    }

    private static Condition loadCondition(Element conditionElement) throws ParserConfigurationException, SAXException, IOException {
        if(conditionElement == null) return null;
        String type = LoadUtils.attribute(conditionElement, "type", "compound");
        boolean invert = LoadUtils.attributeBool(conditionElement, "invert", false);
        ActorReference actorRef = loadActorReference(conditionElement, "actor");
        switch (type) {
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
            case "time":
                Element timeStartElement = LoadUtils.singleChildWithName(conditionElement, "start");
                Element timeEndElement = LoadUtils.singleChildWithName(conditionElement, "end");
                int hours1 = LoadUtils.attributeInt(timeStartElement, "hours", 0);
                int minutes1 = LoadUtils.attributeInt(timeStartElement, "minutes", 0);
                int hours2 = LoadUtils.attributeInt(timeEndElement, "hours", 0);
                int minutes2 = LoadUtils.attributeInt(timeEndElement, "minutes", 0);
                return new ConditionTime(invert, hours1, minutes1, hours2, minutes2);
            case "sceneVisited":
                String sceneVisitedID = LoadUtils.attribute(conditionElement, "scene", null);
                return new ConditionSceneVisited(invert, sceneVisitedID);
            case "random":
                float randomChance = LoadUtils.attributeFloat(conditionElement, "chance", 0.0f);
                return new ConditionRandom(invert, randomChance);
            case "timerActive":
                String timerID = LoadUtils.attribute(conditionElement, "timerID", null);
                return new ConditionTimerActive(invert, timerID);
            case "boolean":
                Variable booleanVariable = loadVariable(LoadUtils.singleChildWithName(conditionElement, "var"));
                return new ConditionBoolean(invert, booleanVariable);
            case "contains":
                Variable containsSetVariable = loadVariable(LoadUtils.singleChildWithName(conditionElement, "varSet"));
                Variable containsValueVariable = loadVariable(LoadUtils.singleChildWithName(conditionElement, "varValue"));
                return new ConditionSetContains(invert, containsSetVariable, containsValueVariable);
            case "compare":
                Variable compareVariable1 = loadVariable(LoadUtils.singleChildWithName(conditionElement, "var1"));
                Variable compareVariable2 = loadVariable(LoadUtils.singleChildWithName(conditionElement, "var2"));
                Condition.Equality compareEquality = LoadUtils.attributeEnum(conditionElement, "equality", Condition.Equality.class, Condition.Equality.GREATER_EQUAL);
                return new ConditionCompare(invert, compareVariable1, compareVariable2, compareEquality);
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

    private static Variable loadVariable(Element variableElement) {
        String type = LoadUtils.attribute(variableElement, "type", "literal");
        String dataType = LoadUtils.attribute(variableElement, "dataType", null);
        switch (type) {
            case "stat":
                StatHolderReference statHolderReference = loadStatHolderReference(variableElement);
                String statName = LoadUtils.attribute(variableElement, "stat", null);
                return new VariableStat(statHolderReference, dataType, statName);
            case "global":
                String globalVariableID = LoadUtils.attribute(variableElement, "globalID", null);
                return new VariableGlobal(dataType, globalVariableID);
            case "literal":
                if ("boolean".equals(dataType)) {
                    boolean literalBoolean = LoadUtils.attributeBool(variableElement, "value", true);
                    return new VariableLiteral(dataType, literalBoolean);
                } else if ("int".equals(dataType)) {
                    int literalInteger = LoadUtils.attributeInt(variableElement, "value", 0);
                    return new VariableLiteral(dataType, literalInteger);
                } else if ("float".equals(dataType)) {
                    float literalFloat = LoadUtils.attributeFloat(variableElement, "value", 0.0f);
                    return new VariableLiteral(dataType, literalFloat);
                } else if ("string".equals(dataType)) {
                    String literalString = LoadUtils.attribute(variableElement, "value", null);
                    return new VariableLiteral(dataType, literalString);
                } else if ("stringSet".equals(dataType)) {
                    Set<String> literalStringSet = LoadUtils.setOfTags(variableElement, "value");
                    return new VariableLiteral(dataType, literalStringSet);
                }
        }
        return null;
    }

    private static StatHolderReference loadStatHolderReference(Element statHolderElement) {
        String holderType = LoadUtils.attribute(statHolderElement, "holder", "subject");
        String holderID = LoadUtils.attribute(statHolderElement, "holderID", null);
        String holderLocalID = LoadUtils.attribute(statHolderElement, "holderLocalID", null);
        return new StatHolderReference(holderType, holderID, holderLocalID);
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
        for(Element scriptElement : LoadUtils.directChildrenWithName(parentElement, "script")) {
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
        switch (type) {
            case "external":
                String scriptID = LoadUtils.attribute(scriptElement, "scriptID", null);
                return new ScriptExternal(condition, scriptID);
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
                return new ScriptScene(condition, actorRef, scenes);
            case "combat":
                ActorReference combatantRef = loadActorReference(scriptElement, "combatant");
                return new ScriptCombat(condition, actorRef, combatantRef);
            case "factionRelation":
                String targetFaction = LoadUtils.attribute(scriptElement, "targetFaction", null);
                String relationFaction = LoadUtils.attribute(scriptElement, "relationFaction", null);
                Faction.FactionRelation relation = LoadUtils.attributeEnum(scriptElement, "relation", Faction.FactionRelation.class, Faction.FactionRelation.NEUTRAL);
                return new ScriptFactionRelation(condition, targetFaction, relationFaction, relation);
            case "bark":
                String barkTrigger = LoadUtils.attribute(scriptElement, "trigger", null);
                return new ScriptBark(condition, actorRef, barkTrigger);
            case "nearestActorScript":
                String nearestTrigger = LoadUtils.attribute(scriptElement, "trigger", null);
                return new ScriptNearestActorWithScript(condition, nearestTrigger);
            case "timerStart":
                String timerID = LoadUtils.attribute(scriptElement, "timerID", null);
                int timerDuration = LoadUtils.attributeInt(scriptElement, "duration", 1);
                Script timerExpireScript = loadScript(LoadUtils.singleChildWithName(scriptElement, "expireScript"));
                return new ScriptTimerStart(condition, timerID, timerDuration, timerExpireScript);
            case "setState":
                StatHolderReference setStateHolder = loadStatHolderReference(scriptElement);
                String setStateName = LoadUtils.attribute(scriptElement, "state", null);
                Variable setStateVariable = loadVariable(LoadUtils.singleChildWithName(scriptElement, "var"));
                return new ScriptSetState(condition, setStateHolder, setStateName, setStateVariable);
            case "modifyState":
                StatHolderReference modifyStateHolder = loadStatHolderReference(scriptElement);
                String modifyStateName = LoadUtils.attribute(scriptElement, "state", null);
                Variable modifyStateVariable = loadVariable(LoadUtils.singleChildWithName(scriptElement, "var"));
                return new ScriptModifyState(condition, modifyStateHolder, modifyStateName, modifyStateVariable);
            case "setGlobal":
                String setGlobalID = LoadUtils.attribute(scriptElement, "globalID", null);
                Variable setGlobalVariable = loadVariable(LoadUtils.singleChildWithName(scriptElement, "var"));
                return new ScriptSetGlobal(condition, setGlobalID, setGlobalVariable);
            case "modifyGlobal":
                String modifyGlobalID = LoadUtils.attribute(scriptElement, "globalID", null);
                Variable modifyGlobalVariable = loadVariable(LoadUtils.singleChildWithName(scriptElement, "var"));
                return new ScriptModifyGlobal(condition, modifyGlobalID, modifyGlobalVariable);
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
            case "TARGET":
                return new ActorReference(ActorReference.ReferenceType.TARGET, null);
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

    private static ItemTemplate loadItemTemplate(Game game, Element itemElement) throws ParserConfigurationException, IOException, SAXException {
        if(itemElement == null) return null;
        String type = itemElement.getAttribute("type");
        String id = itemElement.getAttribute("id");
        String name = LoadUtils.singleTag(itemElement, "name", null);
        Scene description = loadScene(game, LoadUtils.singleChildWithName(itemElement, "description"));
        Map<String, Script> scripts = loadScriptsWithTriggers(itemElement);
        int price = LoadUtils.attributeInt(itemElement, "price", 0);
        String attackType = LoadUtils.attribute(itemElement, "attackType", null);
        switch (type) {
            case "apparel":
                Set<String> apparelSlots = LoadUtils.setOfTags(itemElement, "slot");
                Map<Damage.DamageType, Integer> damageResistance = new HashMap<>();
                for (Element damageResistElement : LoadUtils.directChildrenWithName(itemElement, "damageResist")) {
                    Damage.DamageType damageResistType = LoadUtils.attributeEnum(damageResistElement, "type", Damage.DamageType.class, Damage.DamageType.PHYSICAL);
                    int amount = LoadUtils.attributeInt(damageResistElement, "amount", 0);
                    damageResistance.putIfAbsent(damageResistType, amount);
                }
                List<String> apparelEffects = LoadUtils.listOfTags(itemElement, "effect");
                return new ApparelTemplate(game, id, name, description, scripts, price, attackType, apparelSlots, damageResistance, apparelEffects);
            case "consumable":
                ConsumableTemplate.ConsumableType consumableType = LoadUtils.attributeEnum(itemElement, "type", ConsumableTemplate.ConsumableType.class, ConsumableTemplate.ConsumableType.OTHER);
                List<String> consumableEffects = LoadUtils.listOfTags(itemElement, "effect");
                return new ConsumableTemplate(game, id, name, description, scripts, price, attackType, consumableType, consumableEffects);
            case "weapon":
                String weaponClass = LoadUtils.attribute(itemElement, "class", null);
                int weaponRate = LoadUtils.singleTagInt(itemElement, "rate", 1);
                Element damageElement = LoadUtils.singleChildWithName(itemElement, "damage");
                int weaponDamage = LoadUtils.attributeInt(damageElement, "base", 0);
                int critDamage = LoadUtils.attributeInt(damageElement, "crit", 0);
                Damage.DamageType weaponDamageType = LoadUtils.attributeEnum(damageElement, "type", Damage.DamageType.class, Damage.DamageType.PHYSICAL);
                float weaponAccuracyBonus = LoadUtils.singleTagFloat(itemElement, "accuracyBonus", 0.0f);
                float weaponArmorMult = LoadUtils.singleTagFloat(itemElement, "armorMult", 1.0f);
                boolean weaponSilenced = LoadUtils.singleTagBoolean(itemElement, "silenced", false);
                int weaponClipSize = LoadUtils.singleTagInt(itemElement, "clipSize", 0);
                return new WeaponTemplate(game, id, name, description, scripts, price, attackType, weaponClass, weaponDamage, weaponRate, critDamage, weaponClipSize, weaponAccuracyBonus, weaponArmorMult, weaponSilenced, weaponDamageType);
            case "ammo":
                List<String> ammoWeaponEffects = LoadUtils.listOfTags(itemElement, "weaponEffect");
                boolean ammoIsReusable = LoadUtils.attributeBool(itemElement, "isReusable", false);
                return new AmmoTemplate(game, id, name, description, scripts, price, attackType, ammoWeaponEffects, ammoIsReusable);
            case "misc":
            default:
                return new MiscTemplate(game, id, name, description, scripts, price, attackType);
        }
    }

    private static List<Effect> loadEffects(Element effectsElement) {
        if(effectsElement == null) return new ArrayList<>();
        List<Element> effectElements = LoadUtils.directChildrenWithName(effectsElement, "effect");
        List<Effect> effects = new ArrayList<>();
        for(Element effectElement : effectElements) {
            effects.add(loadEffect(effectElement));
        }
        return effects;
    }

    private static Effect loadEffect(Element effectElement) {
        if(effectElement == null) return null;
        boolean manualRemoval = LoadUtils.attributeBool(effectElement, "isPermanent", false);
        String effectType = LoadUtils.attribute(effectElement, "type", null);
        int duration = LoadUtils.attributeInt(effectElement, "duration", 0);
        boolean stackable = LoadUtils.attributeBool(effectElement, "stack", true);
        switch (effectType) {
            case "state":
                String state = LoadUtils.attribute(effectElement, "state", null);
                int stateAmount = LoadUtils.attributeInt(effectElement, "amount", 0);
                return new EffectStateInt(duration, manualRemoval, stackable, state, stateAmount);
            case "trigger":
                String trigger = LoadUtils.attribute(effectElement, "trigger", null);
                return new EffectTrigger(duration, manualRemoval, stackable, trigger);
            case "mod":
                String statMod = LoadUtils.attribute(effectElement, "stat", null);
                String statModValue = LoadUtils.attribute(effectElement, "amount", "0");
                boolean statModIsFloat = statModValue.contains(".");
                if (statModIsFloat) {
                    float statModValueFloat = Float.parseFloat(statModValue);
                    return new EffectStatModFloat(duration, manualRemoval, stackable, statMod, statModValueFloat);
                } else {
                    int statModValueInt = Integer.parseInt(statModValue);
                    return new EffectStatModInt(duration, manualRemoval, stackable, statMod, statModValueInt);
                }
            case "mult":
                String statMult = LoadUtils.attribute(effectElement, "stat", null);
                float statMultAmount = LoadUtils.attributeFloat(effectElement, "amount", 0.0f);
                return new EffectStatMult(duration, manualRemoval, stackable, statMult, statMultAmount);
            case "boolean":
                String statBoolean = LoadUtils.attribute(effectElement, "stat", null);
                boolean statBooleanValue = LoadUtils.attributeBool(effectElement, "value", true);
                return new EffectStatBoolean(duration, manualRemoval, stackable, statBoolean, statBooleanValue);
            case "string":
                String statString = LoadUtils.attribute(effectElement, "stat", null);
                String statStringValue = LoadUtils.attribute(effectElement, "value", null);
                return new EffectStatString(duration, manualRemoval, stackable, statString, statStringValue);
            case "stringSet":
                String statStringSet = LoadUtils.attribute(effectElement, "stat", null);
                Set<String> stringSetValuesAdd = LoadUtils.setOfTags(effectElement, "add");
                Set<String> stringSetValuesRemove = LoadUtils.setOfTags(effectElement, "remove");
                return new EffectStringSet(duration, manualRemoval, stackable, statStringSet, stringSetValuesAdd, stringSetValuesRemove);
            case "compound":
                List<Effect> compoundEffects = loadEffects(effectElement);
                return new EffectCompound(duration, manualRemoval, stackable, compoundEffects);
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

    private static Room loadRoom(Game game, Element roomElement) throws ParserConfigurationException, IOException, SAXException {
        if(roomElement == null) return null;
        String roomID = roomElement.getAttribute("id");
        Element roomNameElement = LoadUtils.singleChildWithName(roomElement, "name");
        String roomName = roomNameElement.getTextContent();
        boolean roomNameIsProper = LoadUtils.attributeBool(roomNameElement, "proper", false);
        Scene roomDescription = loadScene(game, LoadUtils.singleChildWithName(roomElement, "description"));
        String roomOwnerFaction = LoadUtils.attribute(roomElement, "faction", null);
        Map<String, Script> roomScripts = loadScriptsWithTriggers(roomElement);

        List<Element> areaElements = LoadUtils.directChildrenWithName(roomElement, "area");
        Set<Area> areas = new HashSet<>();
        for(Element areaElement : areaElements) {
            Area area = loadArea(game, areaElement, roomID);
            areas.add(area);
            game.data().addArea(area.getID(), area);
        }

        Map<String, RoomLink> linkedRooms = new HashMap<>();
        List<Element> linkElements = LoadUtils.directChildrenWithName(roomElement, "roomLink");
        for (Element linkElement : linkElements) {
            String linkRoomID = LoadUtils.attribute(linkElement, "room", null);
            AreaLink.CompassDirection linkDirection = LoadUtils.attributeEnum(linkElement, "dir", AreaLink.CompassDirection.class, AreaLink.CompassDirection.N);
            AreaLink.DistanceCategory linkDistance = LoadUtils.attributeEnum(linkElement, "dist", AreaLink.DistanceCategory.class, AreaLink.DistanceCategory.FAR);
            RoomLink link = new RoomLink(linkRoomID, linkDirection, linkDistance);
            linkedRooms.put(linkRoomID, link);
        }

        return new Room(game, roomID, roomName, roomNameIsProper, roomDescription, roomOwnerFaction, areas, linkedRooms, roomScripts);
    }

    private static Area loadArea(Game game, Element areaElement, String roomID) throws ParserConfigurationException, IOException, SAXException {
        if(areaElement == null) return null;
        String areaID = areaElement.getAttribute("id");
        String landmarkID = LoadUtils.attribute(areaElement, "landmark", null);
        Element nameElement = LoadUtils.singleChildWithName(areaElement, "name");
        String name = (nameElement == null ? null : nameElement.getTextContent());
        Area.AreaNameType nameType = LoadUtils.attributeEnum(nameElement, "type", Area.AreaNameType.class, Area.AreaNameType.IN);
        Scene description = loadScene(game, LoadUtils.singleChildWithName(areaElement, "description"));
        Element areaOwnerElement = LoadUtils.singleChildWithName(areaElement, "owner");
        String areaOwnerFaction = (areaOwnerElement != null ? areaOwnerElement.getTextContent() : null);
        boolean areaIsPrivate = LoadUtils.attributeBool(areaOwnerElement, "private", false);

        List<Element> linkElements = LoadUtils.directChildrenWithName(areaElement, "link");
        Map<String, AreaLink> linkSet = new HashMap<>();
        for (Element linkElement : linkElements) {
            String linkAreaID = LoadUtils.attribute(linkElement, "area", null);
            AreaLink.AreaLinkType linkType = LoadUtils.attributeEnum(linkElement, "type", AreaLink.AreaLinkType.class, AreaLink.AreaLinkType.BASIC);
            AreaLink.CompassDirection linkDirection = LoadUtils.attributeEnum(linkElement, "dir", AreaLink.CompassDirection.class, AreaLink.CompassDirection.N);
            AreaLink.DistanceCategory linkDistance = LoadUtils.attributeEnum(linkElement, "dist", AreaLink.DistanceCategory.class, AreaLink.DistanceCategory.CLOSE);
            String moveNameOverride = LoadUtils.singleTag(linkElement, "moveName", null);
            String movePhraseOverride = LoadUtils.singleTag(linkElement, "movePhrase", null);
            AreaLink link = new AreaLink(linkAreaID, linkType, linkDirection, linkDistance, moveNameOverride, movePhraseOverride);
            linkSet.put(linkAreaID, link);
        }

        Map<String, Script> areaScripts = loadScriptsWithTriggers(areaElement);

        Area area = new Area(game, areaID, landmarkID, name, nameType, description, roomID, areaOwnerFaction, areaIsPrivate, linkSet, areaScripts);

        List<Element> objectElements = LoadUtils.directChildrenWithName(areaElement, "object");
        for (Element objectElement : objectElements) {
            WorldObject object = loadObject(game, objectElement, area);
            //loadObjectComponents(objectElement, object);
            game.data().addObject(object.getID(), object);
        }

        List<Element> itemElements = LoadUtils.directChildrenWithName(areaElement, "item");
        for (Element itemElement : itemElements) {
            Item item = loadItem(game, itemElement);
            area.getInventory().addItem(item);
        }

        List<Element> actorElements = LoadUtils.directChildrenWithName(areaElement, "actor");
        for (Element actorElement : actorElements) {
            Actor actor = loadActorInstance(game, actorElement, area);
            game.data().addActor(actor.getID(), actor);
        }

        return area;
    }

    private static Item loadItem(Game game, Element itemElement) {
        String itemTemplate = LoadUtils.attribute(itemElement, "template", null);
        return ItemFactory.create(game, itemTemplate);
    }

    private static ObjectTemplate loadObjectTemplate(Game game, Element objectElement) throws ParserConfigurationException, IOException, SAXException {
        String ID = LoadUtils.attribute(objectElement, "id", null);
        String name = LoadUtils.singleTag(objectElement, "name", null);
        Scene description = loadScene(game, LoadUtils.singleChildWithName(objectElement, "description"));
        Map<String, Script> scripts = loadScriptsWithTriggers(objectElement);
        // TODO - Find way to load object ID for custom actions (needs to be the instance ID, not the template ID)
        List<ActionCustom> customActions = loadCustomActions(objectElement, null, "action");
        Map<String, String> components = new HashMap<>();
        for (Element componentElement : LoadUtils.directChildrenWithName(objectElement, "component")) {
            String componentID = LoadUtils.attribute(componentElement, "id", null);
            String componentTemplate = LoadUtils.attribute(componentElement, "template", null);
            components.put(componentID, componentTemplate);
        }
        return new ObjectTemplate(game, ID, name, description, scripts, customActions, components);
    }

    private static WorldObject loadObject(Game game, Element objectElement, Area area) throws ParserConfigurationException, IOException, SAXException {
        if(objectElement == null) return null;
        String template = LoadUtils.attribute(objectElement, "template", null);
        String type = LoadUtils.attribute(objectElement, "type", null);
        String id = LoadUtils.attribute(objectElement, "id", null);
        boolean startDisabled = LoadUtils.attributeBool(objectElement, "startDisabled", false);
        boolean startHidden = LoadUtils.attributeBool(objectElement, "startHidden", false);
        List<ActionCustom> customUsingActions = loadCustomActions(objectElement, id, "actionUsing");
        Map<String, ComponentLink> linkedObjects = new HashMap<>();
        for (Element linkedObjectElement : LoadUtils.directChildrenWithName(objectElement, "link")) {
            String componentID = LoadUtils.attribute(linkedObjectElement, "component", null);
            String linkedObjectID = LoadUtils.attribute(linkedObjectElement, "object", null);
            AreaLink.CompassDirection linkedObjectDirection = LoadUtils.attributeEnum(linkedObjectElement, "dir", AreaLink.CompassDirection.class, AreaLink.CompassDirection.N);
            ComponentLink componentLink = new ComponentLink(linkedObjectID, linkedObjectDirection);
            linkedObjects.put(componentID, componentLink);
        }
        switch (type) {
            case "door":
                String doorLink = LoadUtils.attribute(objectElement, "link", null);
                AreaLink.CompassDirection doorDirection = LoadUtils.attributeEnum(objectElement, "dir", AreaLink.CompassDirection.class, AreaLink.CompassDirection.N);
                Lock doorLock = loadLock(objectElement, id);
                return new ObjectDoor(game, id, template, area, startDisabled, startHidden, linkedObjects, doorLink, doorDirection, doorLock);
            case "elevator":
                Element floorElement = LoadUtils.singleChildWithName(objectElement, "floor");
                int floorNumber = LoadUtils.attributeInt(floorElement, "number", 1);
                String floorName = floorElement.getTextContent();
                boolean elevatorStartLocked = LoadUtils.attributeBool(objectElement, "startLocked", false);
                Set<String> linkedElevatorIDs = LoadUtils.setOfTags(objectElement, "link");
                return new ObjectElevator(game, id, template, area, startDisabled, startHidden, linkedObjects, floorNumber, floorName, linkedElevatorIDs, elevatorStartLocked);
            case "chair":
                return new ObjectChair(game, id, template, area, startDisabled, startHidden, linkedObjects, customUsingActions);
            case "bed":
                return new ObjectBed(game, id, template, area, startDisabled, startHidden, linkedObjects, customUsingActions);
            case "cover":
                return new ObjectCover(game, id, template, area, startDisabled, startHidden, linkedObjects, customUsingActions);
            case "vendingMachine":
                List<String> vendingItems = LoadUtils.listOfTags(objectElement, "item");
                return new ObjectVendingMachine(game, id, template, area, startDisabled, startHidden, linkedObjects, vendingItems);
            case "basic":
            default:
                return new WorldObject(game, id, template, area, startDisabled, startHidden, linkedObjects);
        }
    }

    private static ObjectComponentTemplate loadObjectComponentTemplate(Game game, Element componentElement) {
        String ID = LoadUtils.attribute(componentElement, "id", null);
        String type = LoadUtils.attribute(componentElement, "type", null);
        boolean startEnabled = LoadUtils.attributeBool(componentElement, "startEnabled", true);
        switch (type) {
            case "inventory":
                String inventoryName = LoadUtils.singleTag(componentElement, "name", null);
                LootTable lootTable = loadLootTable(LoadUtils.singleChildWithName(componentElement, "inventory"), true);
                boolean inventoryIsExposed = LoadUtils.attributeBool(componentElement, "exposed", false);
                return new ObjectComponentTemplateInventory(game, ID, startEnabled, inventoryName, lootTable, inventoryIsExposed);
            case "network":
                String networkID = LoadUtils.attribute(componentElement, "network", null);
                return new ObjectComponentTemplateNetwork(game, ID, startEnabled, networkID);
            case "link":
                boolean linkIsMovable = LoadUtils.attributeBool(componentElement, "movable", true);
                boolean linkIsVisible = LoadUtils.attributeBool(componentElement, "visible", false);
                // TODO - Find a way to load the direction from the object instance, just like the linked object ID
                AreaLink.CompassDirection linkDirection = LoadUtils.attributeEnum(componentElement, "direction", AreaLink.CompassDirection.class, AreaLink.CompassDirection.N);
                return new ObjectComponentTemplateLink(game, ID, startEnabled, linkIsMovable, linkIsVisible, linkDirection);
            case "usable":
                return new ObjectComponentTemplateUsable(game, ID, startEnabled);
            default:
                throw new IllegalArgumentException("ObjectComponentTemplate has invalid or missing type");
        }
    }

    private static Lock loadLock(Element objectElement, String objectID) {
        Element lockElement = LoadUtils.singleChildWithName(objectElement, "lock");
        if (lockElement == null) return null;
        boolean startLocked = LoadUtils.attributeBool(lockElement, "startLocked", true);
        int lockpickLevel = LoadUtils.attributeInt(lockElement, "lockpick", 0);
        int hotwireLevel = LoadUtils.attributeInt(lockElement, "hotwire", 0);
        Set<String> keys = LoadUtils.setOfTags(lockElement, "key");
        return new Lock(objectID, startLocked, keys, lockpickLevel, hotwireLevel);
    }

    private static List<ActionCustom> loadCustomActions(Element objectElement, String objectID, String elementName) throws ParserConfigurationException, IOException, SAXException {
        if(objectElement == null) return new ArrayList<>();
        List<ActionCustom> actions = new ArrayList<>();
        for (Element actionElement : LoadUtils.directChildrenWithName(objectElement, elementName)) {
            String prompt = LoadUtils.singleTag(actionElement, "prompt", null);
            String description = LoadUtils.singleTag(actionElement, "description", null);
            Condition condition = loadCondition(LoadUtils.singleChildWithName(actionElement, "condition"));
            Condition conditionShow = loadCondition(LoadUtils.singleChildWithName(actionElement, "conditionShow"));
            Script script = loadScript(LoadUtils.singleChildWithName(actionElement, "script"));
            actions.add(new ActionCustom(prompt, description, objectID, condition, conditionShow, script));
        }
        return actions;
    }

    // TODO - Delay creation of instances until all data has been loaded (could attempt to load instance before template is loaded)
    private static Actor loadActorInstance(Game game, Element actorElement, Area area) throws ParserConfigurationException, IOException, SAXException {
        if(actorElement == null) return null;
        String ID = actorElement.getAttribute("id");
        String template = LoadUtils.attribute(actorElement, "template", null);
        List<Behavior> behaviors = loadBehaviors(LoadUtils.singleChildWithName(actorElement, "behaviors"));
        boolean startDead = LoadUtils.attributeBool(actorElement, "startDead", false);
        boolean startDisabled = LoadUtils.attributeBool(actorElement, "startDisabled", false);
        return ActorFactory.create(game, ID, area, template, behaviors, startDead, startDisabled);
    }

    private static List<Behavior> loadBehaviors(Element behaviorsElement) throws ParserConfigurationException, IOException, SAXException {
        if(behaviorsElement == null) return new ArrayList<>();
        List<Behavior> behaviors = new ArrayList<>();
        for(Element behaviorElement : LoadUtils.directChildrenWithName(behaviorsElement, "behavior")) {
            Behavior behavior;
            String type = LoadUtils.attribute(behaviorElement, "type", null);
            Condition condition = loadCondition(LoadUtils.singleChildWithName(behaviorElement, "condition"));
            int duration = LoadUtils.attributeInt(behaviorElement, "duration", 0);
            List<Idle> idles = new ArrayList<>();
            List<Element> idleElements = LoadUtils.directChildrenWithName(behaviorElement, "idle");
            for (Element idleElement : idleElements) {
                Idle idle = loadIdle(idleElement);
                idles.add(idle);
            }
            switch (type) {
                case "area":
                    String areaTarget = LoadUtils.attribute(behaviorElement, "area", null);
                    behavior = new BehaviorArea(condition, duration, idles, areaTarget);
                    break;
                case "object":
                    String objectTarget = LoadUtils.attribute(behaviorElement, "object", null);
                    behavior = new BehaviorObject(condition, duration, idles, objectTarget);
                    break;
                case "guard":
                    String guardTarget = LoadUtils.attribute(behaviorElement, "object", null);
                    behavior = new BehaviorGuard(condition, duration, idles, guardTarget);
                    break;
                case "sandbox":
                    String startArea = LoadUtils.attribute(behaviorElement, "area", null);
                    behavior = new BehaviorSandbox(condition, duration, idles, startArea);
                    break;
                case "sleep":
                    String bedTarget = LoadUtils.attribute(behaviorElement, "bed", null);
                    behavior = new BehaviorSleep(condition, idles, bedTarget);
                    break;
                case "vendor":
                    String vendorArea = LoadUtils.attribute(behaviorElement, "area", null);
                    behavior = new BehaviorVendor(condition, duration, idles, vendorArea);
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

    private static Idle loadIdle(Element idleElement) throws ParserConfigurationException, IOException, SAXException {
        Condition condition = loadCondition(LoadUtils.singleChildWithName(idleElement, "condition"));
        String phrase = LoadUtils.singleTag(idleElement, "phrase", null);
        return new Idle(condition, phrase);
    }

    private static WeaponClass loadWeaponClass(Element weaponClassElement) {
        String ID = LoadUtils.attribute(weaponClassElement, "id", null);
        String name = LoadUtils.singleTag(weaponClassElement, "name", null);
        boolean isRanged = LoadUtils.attributeBool(weaponClassElement, "isRanged", false);
        boolean isTwoHanded = LoadUtils.attributeBool(weaponClassElement, "isTwoHanded", false);
        Actor.Skill skill = LoadUtils.attributeEnum(weaponClassElement, "skill", Actor.Skill.class, Actor.Skill.MELEE);
        Set<AreaLink.DistanceCategory> primaryRanges = LoadUtils.setOfEnumTags(weaponClassElement, "range", AreaLink.DistanceCategory.class);
        Set<String> ammoTypes = LoadUtils.setOfTags(weaponClassElement, "ammo");
        Set<String> attackTypes = LoadUtils.setOfTags(weaponClassElement, "attackType");
        return new WeaponClass(ID, name, isRanged, isTwoHanded, skill, primaryRanges, ammoTypes, attackTypes);
    }

    private static WeaponAttackType loadWeaponAttackType(Element attackTypeElement) {
        String ID = LoadUtils.attribute(attackTypeElement, "id", null);
        WeaponAttackType.AttackCategory category = LoadUtils.attributeEnum(attackTypeElement, "category", WeaponAttackType.AttackCategory.class, WeaponAttackType.AttackCategory.SINGLE);
        String prompt = LoadUtils.singleTag(attackTypeElement, "prompt", null);
        String hitPhrase = LoadUtils.singleTag(attackTypeElement, "hitPhrase", null);
        String hitPhraseRepeat = LoadUtils.singleTag(attackTypeElement, "hitPhraseRepeat", null);
        String hitOverallPhrase = LoadUtils.singleTag(attackTypeElement, "hitOverallPhrase", null);
        String hitOverallPhraseRepeat = LoadUtils.singleTag(attackTypeElement, "hitOverallPhraseRepeat", null);
        String missPhrase = LoadUtils.singleTag(attackTypeElement, "missPhrase", null);
        String missPhraseRepeat = LoadUtils.singleTag(attackTypeElement, "missPhraseRepeat", null);
        String missOverallPhrase = LoadUtils.singleTag(attackTypeElement, "missOverallPhrase", null);
        String missOverallPhraseRepeat = LoadUtils.singleTag(attackTypeElement, "missOverallPhraseRepeat", null);
        int ammoConsumed = LoadUtils.attributeInt(attackTypeElement, "ammoConsumed", 1);
        Actor.Skill skillOverride = LoadUtils.attributeEnum(attackTypeElement, "skill", Actor.Skill.class, null);
        Float baseHitChanceMin = LoadUtils.attributeFloat(attackTypeElement, "hitChanceMin", null);
        Float baseHitChanceMax = LoadUtils.attributeFloat(attackTypeElement, "hitChanceMax", null);
        boolean useNonIdealRange = LoadUtils.attributeBool(attackTypeElement, "nonIdealRange", false);
        Set<AreaLink.DistanceCategory> rangeOverride = LoadUtils.setOfEnumTags(attackTypeElement, "range", AreaLink.DistanceCategory.class);
        Integer rateOverride = LoadUtils.attributeInt(attackTypeElement, "rate", null);
        Integer damageOverride = LoadUtils.attributeInt(attackTypeElement, "damage", null);
        float damageMult = LoadUtils.attributeFloat(attackTypeElement, "damageMult", 0.0f);
        Damage.DamageType damageTypeOverride = LoadUtils.attributeEnum(attackTypeElement, "damageType", Damage.DamageType.class, null);
        Float armorMultOverride = LoadUtils.attributeFloat(attackTypeElement, "armorMult", null);
        List<String> targetEffects = LoadUtils.listOfTags(attackTypeElement, "targetEffect");
        float hitChanceMult = LoadUtils.attributeFloat(attackTypeElement, "hitChanceMult", 0.0f);
        boolean canDodge = LoadUtils.attributeBool(attackTypeElement, "canDodge", false);
        ActionAttack.AttackHitChanceType hitChanceType = LoadUtils.attributeEnum(attackTypeElement, "hitChanceType", ActionAttack.AttackHitChanceType.class, ActionAttack.AttackHitChanceType.INDEPENDENT);
        return new WeaponAttackType(ID, category, prompt, hitPhrase, hitPhraseRepeat, hitOverallPhrase, hitOverallPhraseRepeat, missPhrase, missPhraseRepeat, missOverallPhrase, missOverallPhraseRepeat, ammoConsumed, skillOverride, baseHitChanceMin, baseHitChanceMax, useNonIdealRange, rangeOverride, rateOverride, damageOverride, damageMult, damageTypeOverride, armorMultOverride, targetEffects, hitChanceMult, canDodge, hitChanceType);
    }

    private static Network loadNetwork(Element networkElement) {
        String ID = LoadUtils.attribute(networkElement, "id", null);
        String name = LoadUtils.attribute(networkElement, "name", null);
        NetworkNode topNode = loadNetworkNode(LoadUtils.singleChildWithName(networkElement, "node"));
        return new Network(ID, name, topNode);
    }

    private static NetworkNode loadNetworkNode(Element nodeElement) {
        String type = LoadUtils.attribute(nodeElement, "type", null);
        String ID = LoadUtils.attribute(nodeElement, "id", null);
        String name = LoadUtils.singleTag(nodeElement, "name", null);
        int securityLevel = LoadUtils.attributeInt(nodeElement, "securityLevel", 0);
        switch (type) {
            case "data":
                String dataSceneID = LoadUtils.attribute(nodeElement, "scene", null);
                return new NetworkNodeData(ID, name, securityLevel, dataSceneID);
            case "control":
                String controlObjectID = LoadUtils.attribute(nodeElement, "object", null);
                return new NetworkNodeControl(ID, name, securityLevel, controlObjectID);
            case "group":
            default:
                Set<NetworkNode> groupNodes = new HashSet<>();
                for (Element childNodeElement : LoadUtils.directChildrenWithName(nodeElement, "node")) {
                    NetworkNode childNode = loadNetworkNode(childNodeElement);
                    groupNodes.add(childNode);
                }
                return new NetworkNodeGroup(ID, name, securityLevel, groupNodes);
        }
    }

}
