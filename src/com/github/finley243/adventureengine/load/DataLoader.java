package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameDataException;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.action.ActionTemplate;
import com.github.finley243.adventureengine.action.attack.ActionAttack;
import com.github.finley243.adventureengine.actor.*;
import com.github.finley243.adventureengine.actor.ai.Idle;
import com.github.finley243.adventureengine.actor.ai.behavior.*;
import com.github.finley243.adventureengine.combat.WeaponAttackType;
import com.github.finley243.adventureengine.combat.WeaponClass;
import com.github.finley243.adventureengine.condition.*;
import com.github.finley243.adventureengine.effect.*;
import com.github.finley243.adventureengine.expression.*;
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
import com.github.finley243.adventureengine.textgen.TextContext;
import com.github.finley243.adventureengine.world.environment.*;
import com.github.finley243.adventureengine.world.object.WorldObject;
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

    public static void loadFromDir(Game game, File dir) throws ParserConfigurationException, IOException, SAXException, GameDataException {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            assert files != null;
            for (File file : files) {
                if (file.getName().substring(file.getName().lastIndexOf(".") + 1).equalsIgnoreCase("xml")) {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.parse(file);
                    Element rootElement = document.getDocumentElement();
                    Node currentChild = rootElement.getFirstChild();
                    while (currentChild != null) {
                        if (currentChild.getNodeType() == Node.ELEMENT_NODE) {
                            Element currentElement = (Element) currentChild;
                            switch (currentChild.getNodeName()) {
                                case "faction" -> {
                                    Faction faction = loadFaction(currentElement);
                                    game.data().addFaction(faction.getID(), faction);
                                }
                                case "scene" -> {
                                    Scene scene = loadScene(game, currentElement);
                                    game.data().addScene(scene.getID(), scene);
                                }
                                case "actor" -> {
                                    ActorTemplate actor = loadActor(game, currentElement);
                                    game.data().addActorTemplate(actor.getID(), actor);
                                }
                                case "object" -> {
                                    ObjectTemplate object = loadObjectTemplate(game, currentElement);
                                    game.data().addObjectTemplate(object.getID(), object);
                                }
                                case "item" -> {
                                    ItemTemplate item = loadItemTemplate(game, currentElement);
                                    game.data().addItemTemplate(item.getID(), item);
                                }
                                case "lootTable" -> {
                                    LootTable table = loadLootTable(currentElement, false);
                                    game.data().addLootTable(table.getID(), table);
                                }
                                case "weaponClass" -> {
                                    WeaponClass weaponClass = loadWeaponClass(currentElement);
                                    game.data().addWeaponClass(weaponClass.getID(), weaponClass);
                                }
                                case "attackType" -> {
                                    WeaponAttackType attackType = loadWeaponAttackType(currentElement);
                                    game.data().addAttackType(attackType.getID(), attackType);
                                }
                                case "room" -> {
                                    Room room = loadRoom(game, currentElement);
                                    game.data().addRoom(room.getID(), room);
                                }
                                case "script" -> {
                                    String scriptID = LoadUtils.attribute(currentElement, "id", null);
                                    Script script = loadScript(currentElement);
                                    game.data().addScript(scriptID, script);
                                }
                                case "expression" -> {
                                    String expressionID = LoadUtils.attribute(currentElement, "id", null);
                                    Expression expression = loadExpression(currentElement, null);
                                    game.data().addExpression(expressionID, expression);
                                }
                                case "condition" -> {
                                    String conditionID = LoadUtils.attribute(currentElement, "id", null);
                                    Condition condition = loadCondition(currentElement);
                                    game.data().addCondition(conditionID, condition);
                                }
                                case "effect" -> {
                                    Effect effect = loadEffect(game, currentElement);
                                    game.data().addEffect(effect.getID(), effect);
                                }
                                case "action" -> {
                                    ActionTemplate action = loadActionTemplate(game, currentElement);
                                    game.data().addActionTemplate(action.getID(), action);
                                }
                                case "linkType" -> {
                                    LinkType linkType = loadLinkType(game, currentElement);
                                    game.data().addLinkType(linkType.getID(), linkType);
                                }
                                case "network" -> {
                                    Network network = loadNetwork(currentElement);
                                    game.data().addNetwork(network.getID(), network);
                                }
                                case "damageType" -> {
                                    String ID = LoadUtils.attribute(currentElement, "id", "");
                                    game.data().addDamageType(ID);
                                }
                            }
                        }
                        currentChild = currentChild.getNextSibling();
                    }
                }
            }
        }
    }

    private static ActorTemplate loadActor(Game game, Element actorElement) {
        String id = LoadUtils.attribute(actorElement, "id", null);
        String parentID = LoadUtils.attribute(actorElement, "parent", null);
        Element nameElement = LoadUtils.singleChildWithName(actorElement, "name");
        String name = nameElement != null ? nameElement.getTextContent() : null;
        Boolean nameIsProper = nameElement != null && LoadUtils.attributeBool(nameElement, "proper", false);
        TextContext.Pronoun pronoun = LoadUtils.attributeEnum(nameElement, "pronoun", TextContext.Pronoun.class, TextContext.Pronoun.THEY);
        String faction = LoadUtils.attribute(actorElement, "faction", null);
        Boolean isEnforcer = LoadUtils.attributeBool(actorElement, "isEnforcer", null);

        Integer hp = LoadUtils.attributeInt(actorElement, "hp", null);
        Map<String, Integer> damageResistance = loadDamageResistance(actorElement);
        List<Limb> limbs = loadLimbs(actorElement);
        Map<String, EquipSlot> equipSlots = new HashMap<>();
        for (Element slotElement : LoadUtils.directChildrenWithName(actorElement, "equipSlot")) {
            String slotID = LoadUtils.attribute(slotElement, "id", null);
            String slotName = slotElement.getTextContent();
            equipSlots.put(slotID, new EquipSlot(slotID, slotName));
        }
        String defaultEquipSlot = LoadUtils.attribute(actorElement, "defaultEquipSlot", null);
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

        return new ActorTemplate(game, id, parentID, name, nameIsProper, pronoun, faction, isEnforcer, hp, damageResistance, limbs, equipSlots, defaultEquipSlot, attributes, skills, lootTable, dialogueStart, scripts, barks);
    }

    private static List<Limb> loadLimbs(Element element) {
        List<Limb> limbs = new ArrayList<>();
        if (element == null) return limbs;
        for (Element limbElement : LoadUtils.directChildrenWithName(element, "limb")) {
            limbs.add(loadLimb(limbElement));
        }
        return limbs;
    }

    private static Limb loadLimb(Element element) {
        String ID = LoadUtils.attribute(element, "id", null);
        String name = LoadUtils.singleTag(element, "name", null);
        float hitChance = LoadUtils.attributeFloat(element, "hitChance", 1.0f);
        float damageMult = LoadUtils.attributeFloat(element, "damageMult", 1.0f);
        String apparelSlot = LoadUtils.attribute(element, "apparelSlot", null);
        List<String> hitEffects = LoadUtils.listOfTags(element, "hitEffect");
        return new Limb(ID, name, hitChance, damageMult, apparelSlot, hitEffects);
    }

    private static Map<Actor.Attribute, Integer> loadAttributes(Element element) {
        Map<Actor.Attribute, Integer> attributes = new EnumMap<>(Actor.Attribute.class);
        if (element == null) return attributes;
        for (Element attributeElement : LoadUtils.directChildrenWithName(element, "attribute")) {
            Actor.Attribute attribute = LoadUtils.attributeEnum(attributeElement, "key", Actor.Attribute.class, null);
            int value = LoadUtils.attributeInt(attributeElement, "value", 0);
            attributes.put(attribute, value);
        }
        return attributes;
    }

    private static Map<Actor.Skill, Integer> loadSkills(Element element) {
        Map<Actor.Skill, Integer> skills = new EnumMap<>(Actor.Skill.class);
        if (element == null) return skills;
        for (Element skillElement : LoadUtils.directChildrenWithName(element, "skill")) {
            Actor.Skill skill = LoadUtils.attributeEnum(skillElement, "key", Actor.Skill.class, null);
            int value = LoadUtils.attributeInt(skillElement, "value", 0);
            skills.put(skill, value);
        }
        return skills;
    }

    private static Map<String, Integer> loadDamageResistance(Element element) {
        Map<String, Integer> damageResistance = new HashMap<>();
        if (element == null) return damageResistance;
        for (Element damageResistanceElement : LoadUtils.directChildrenWithName(element, "damageResistance")) {
            String damageType = LoadUtils.attribute(damageResistanceElement, "type", null);
            int value = LoadUtils.attributeInt(damageResistanceElement, "value", 0);
            damageResistance.put(damageType, value);
        }
        return damageResistance;
    }

    private static Scene loadScene(Game game, Element sceneElement) {
        if (sceneElement == null) return null;
        String sceneID = LoadUtils.attribute(sceneElement, "id", null);
        Scene.SceneType type = switch (LoadUtils.attribute(sceneElement, "type", "all")) {
            case "random" -> Scene.SceneType.RANDOM;
            case "select" -> Scene.SceneType.SELECTOR;
            case "all", default -> Scene.SceneType.SEQUENTIAL;
        };
        Condition condition = loadCondition(LoadUtils.singleChildWithName(sceneElement, "condition"));
        boolean once = LoadUtils.attributeBool(sceneElement, "once", false);
        int priority = LoadUtils.attributeInt(sceneElement, "priority", 1);
        List<Element> lineElements = LoadUtils.directChildrenWithName(sceneElement, "line");
        List<SceneLine> lines = new ArrayList<>();
        for (Element lineElement : lineElements) {
            SceneLine line = loadSceneLine(lineElement);
            lines.add(line);
        }
        List<Element> choiceElements = LoadUtils.directChildrenWithName(sceneElement, "choice");
        List<SceneChoice> choices = new ArrayList<>();
        for (Element choiceElement : choiceElements) {
            SceneChoice choice = loadSceneChoice(choiceElement);
            choices.add(choice);
        }
        return new Scene(game, sceneID, condition, once, priority, lines, choices, type);
    }

    private static SceneLine loadSceneLine(Element lineElement) {
        boolean once = LoadUtils.attributeBool(lineElement, "once", false);
        boolean exit = LoadUtils.attributeBool(lineElement, "exit", false);
        String redirect = LoadUtils.attribute(lineElement, "redirect", null);
        String from = LoadUtils.attribute(lineElement, "from", null);
        // TODO - Check this condition's functionality more thoroughly (may have unexpected results in rare situations)
        if (lineElement.getChildNodes().getLength() == 1) {
            String text = lineElement.getTextContent().trim();
            return new SceneLine(text, once, exit, redirect, from);
        } else {
            Scene.SceneType type = switch (LoadUtils.attribute(lineElement, "type", "all")) {
                case "random" -> Scene.SceneType.RANDOM;
                case "select" -> Scene.SceneType.SELECTOR;
                case "all", default -> Scene.SceneType.SEQUENTIAL;
            };
            Condition condition = loadCondition(LoadUtils.singleChildWithName(lineElement, "condition"));
            Script script = loadScript(LoadUtils.singleChildWithName(lineElement, "script"));
            List<SceneLine> subLines = new ArrayList<>();
            for (Element subLineElement : LoadUtils.directChildrenWithName(lineElement, "line")) {
                SceneLine subLine = loadSceneLine(subLineElement);
                subLines.add(subLine);
            }
            return new SceneLine(type, subLines, condition, script, once, exit, redirect, from);
        }
    }

    private static SceneChoice loadSceneChoice(Element choiceElement) {
        String link = choiceElement.getAttribute("link");
        String prompt = choiceElement.getTextContent();
        return new SceneChoice(link, prompt);
    }

    private static Condition loadCondition(Element conditionElement) {
        if (conditionElement == null) return null;
        String type = LoadUtils.attribute(conditionElement, "type", "compound");
        boolean invert = LoadUtils.attributeBool(conditionElement, "invert", false);
        if (conditionElement.hasAttribute("external")) {
            String externalID = LoadUtils.attribute(conditionElement, "external", null);
            return new ConditionExternal(invert, externalID);
        }
        switch (type) {
            case "external" -> {
                String externalID = LoadUtils.attribute(conditionElement, "conditionID", null);
                return new ConditionExternal(invert, externalID);
            }
            case "combatant" -> {
                StatHolderReference actorRef = loadStatHolderReference(LoadUtils.singleChildWithName(conditionElement, "actor"));
                StatHolderReference targetRef = loadStatHolderReference(LoadUtils.singleChildWithName(conditionElement, "target"));
                return new ConditionCombatant(invert, actorRef, targetRef);
            }
            case "inventoryItem" -> {
                Expression invItemVar = loadExpression(LoadUtils.singleChildWithName(conditionElement, "inv"), "inventory");
                Expression invItemID = loadExpressionOrAttribute(conditionElement, "item", "string");
                boolean invRequireAll = LoadUtils.attributeBool(conditionElement, "requireAll", false);
                return new ConditionInventoryItem(invert, invItemVar, invItemID, invRequireAll);
            }
            case "visible" -> {
                StatHolderReference actorRef = loadStatHolderReference(LoadUtils.singleChildWithName(conditionElement, "actor"));
                StatHolderReference targetRef = loadStatHolderReference(LoadUtils.singleChildWithName(conditionElement, "target"));
                return new ConditionVisible(invert, actorRef, targetRef);
            }
            case "time" -> {
                Element timeStartElement = LoadUtils.singleChildWithName(conditionElement, "start");
                Element timeEndElement = LoadUtils.singleChildWithName(conditionElement, "end");
                int hours1 = LoadUtils.attributeInt(timeStartElement, "hours", 0);
                int minutes1 = LoadUtils.attributeInt(timeStartElement, "minutes", 0);
                int hours2 = LoadUtils.attributeInt(timeEndElement, "hours", 0);
                int minutes2 = LoadUtils.attributeInt(timeEndElement, "minutes", 0);
                return new ConditionTime(invert, hours1, minutes1, hours2, minutes2);
            }
            case "random" -> {
                Expression randomChance = loadExpressionOrAttribute(conditionElement, "chance", "float");
                return new ConditionRandom(invert, randomChance);
            }
            case "timerActive" -> {
                Expression timerID = loadExpressionOrAttribute(conditionElement, "timerID", "string");
                return new ConditionTimerActive(invert, timerID);
            }
            case "boolean" -> {
                Expression booleanExpression = loadExpression(LoadUtils.singleChildWithName(conditionElement, "value"), "boolean");
                return new ConditionBoolean(invert, booleanExpression);
            }
            case "contains" -> {
                Expression containsSetExpression = loadExpression(LoadUtils.singleChildWithName(conditionElement, "set"), "stringSet");
                Expression containsValueExpression = loadExpressionOrAttribute(conditionElement, "value", "string");
                return new ConditionSetContains(invert, containsSetExpression, containsValueExpression);
            }
            case "compare" -> {
                Expression compareExpression1 = loadExpressionOrAttribute(conditionElement, "value1", null);
                Expression compareExpression2 = loadExpressionOrAttribute(conditionElement, "value2", null);
                Condition.Comparator comparator = LoadUtils.attributeEnum(conditionElement, "equality", Condition.Comparator.class, Condition.Comparator.GREATER_EQUAL);
                return new ConditionCompare(invert, compareExpression1, compareExpression2, comparator);
            }
            case "any" -> {
                List<Condition> subConditionsAny = loadSubConditions(conditionElement);
                return new ConditionCompound(invert, subConditionsAny, true);
            }
            default -> {
                List<Condition> subConditionsAll = loadSubConditions(conditionElement);
                return new ConditionCompound(invert, subConditionsAll, false);
            }
        }
    }

    private static List<Condition> loadSubConditions(Element conditionElement) {
        List<Element> subConditionElements = LoadUtils.directChildrenWithName(conditionElement, "condition");
        List<Condition> subConditions = new ArrayList<>();
        for (Element subConditionElement : subConditionElements) {
            Condition subCondition = loadCondition(subConditionElement);
            subConditions.add(subCondition);
        }
        return subConditions;
    }

    private static Expression loadExpression(Element expressionElement, String dataTypeDefault) {
        if (expressionElement == null) return null;
        String defaultType = ("inventory".equals(dataTypeDefault) || "noun".equals(dataTypeDefault)) ? "stat" : null;
        String type = LoadUtils.attribute(expressionElement, "type", defaultType);
        String dataType = LoadUtils.attribute(expressionElement, "dataType", dataTypeDefault);
        if (expressionElement.hasAttribute("external")) {
            String externalID = LoadUtils.attribute(expressionElement, "external", null);
            return new ExpressionExternal(dataType, externalID);
        }
        switch (type) {
            case "stat" -> {
                StatHolderReference statHolderReference = loadStatHolderReference(expressionElement);
                Expression statName = loadExpressionOrAttribute(expressionElement, "stat", "string");
                return new ExpressionStat(statHolderReference, dataType, statName);
            }
            case "global" -> {
                Expression globalExpressionID = loadExpressionOrAttribute(expressionElement, "globalID", "string");
                return new ExpressionGlobal(dataType, globalExpressionID);
            }
            case "external" -> {
                String externalID = LoadUtils.attribute(expressionElement, "id", null);
                return new ExpressionExternal(dataType, externalID);
            }
            case "parameter" -> {
                String parameterName = LoadUtils.attribute(expressionElement, "name", null);
                return new ExpressionParameter(dataType, parameterName);
            }
            case "and" -> {
                List<Expression> expressions = new ArrayList<>();
                for (Element productVariableElement : LoadUtils.directChildrenWithName(expressionElement, "value")) {
                    expressions.add(loadExpression(productVariableElement, null));
                }
                return new ExpressionLogicCompound(expressions, true);
            }
            case "or" -> {
                List<Expression> expressions = new ArrayList<>();
                for (Element productVariableElement : LoadUtils.directChildrenWithName(expressionElement, "value")) {
                    expressions.add(loadExpression(productVariableElement, null));
                }
                return new ExpressionLogicCompound(expressions, false);
            }
            case "add" -> {
                List<Expression> sumExpressions = new ArrayList<>();
                for (Element sumVariableElement : LoadUtils.directChildrenWithName(expressionElement, "value")) {
                    sumExpressions.add(loadExpression(sumVariableElement, null));
                }
                return new ExpressionAdd(sumExpressions);
            }
            case "multiply" -> {
                List<Expression> productExpressions = new ArrayList<>();
                for (Element productVariableElement : LoadUtils.directChildrenWithName(expressionElement, "value")) {
                    productExpressions.add(loadExpression(productVariableElement, null));
                }
                return new ExpressionMultiply(productExpressions);
            }
            case "subtract" -> {
                Expression expression1 = loadExpressionOrAttribute(expressionElement, "value1", null);
                Expression expression2 = loadExpressionOrAttribute(expressionElement, "value2", null);
                return new ExpressionSubtract(expression1, expression2);
            }
            case "divide" -> {
                Expression expression1 = loadExpressionOrAttribute(expressionElement, "value1", null);
                Expression expression2 = loadExpressionOrAttribute(expressionElement, "value2", null);
                return new ExpressionDivide(expression1, expression2);
            }
            case "power" -> {
                Expression expressionBase = loadExpressionOrAttribute(expressionElement, "base", null);
                Expression expressionExponent = loadExpressionOrAttribute(expressionElement, "exponent", null);
                return new ExpressionPower(expressionBase, expressionExponent);
            }
            case "hasVariable" -> {
                String variableName = LoadUtils.attribute(expressionElement, "name", null);
                return new ExpressionHasVariable(variableName);
            }
            case "concat" -> {
                List<Expression> stringExpressions = new ArrayList<>();
                for (Element concatVariableElement : LoadUtils.directChildrenWithName(expressionElement, "value")) {
                    stringExpressions.add(loadExpression(concatVariableElement, "string"));
                }
                return new ExpressionConcatStrings(stringExpressions);
            }
            case "toString" -> {
                Expression toStringExpression = loadExpression(LoadUtils.singleChildWithName(expressionElement, "value"), null);
                return new ExpressionToString(toStringExpression);
            }
            case "randomStringFromSet" -> {
                Expression setExpression = loadExpression(LoadUtils.singleChildWithName(expressionElement, "set"), "stringSet");
                return new ExpressionRandomStringFromSet(setExpression);
            }
            case "size" -> {
                Expression setExpression = loadExpression(LoadUtils.singleChildWithName(expressionElement, "set"), "stringSet");
                return new ExpressionSetSize(setExpression);
            }
            case "buildStringSet" -> {
                List<Expression> stringVars = new ArrayList<>();
                for (Element stringVarElement : LoadUtils.directChildrenWithName(expressionElement, "value")) {
                    stringVars.add(loadExpression(stringVarElement, "string"));
                }
                return new ExpressionSetFromStrings(stringVars);
            }
            case "round" -> {
                Expression roundExpression = loadExpression(LoadUtils.singleChildWithName(expressionElement, "value"), "float");
                return new ExpressionRound(roundExpression);
            }
            case "conditional" -> {
                List<ExpressionConditional.ConditionVariablePair> conditionVariablePairs = new ArrayList<>();
                for (Element pairElement : LoadUtils.directChildrenWithName(expressionElement, "if")) {
                    Condition condition = loadCondition(LoadUtils.singleChildWithName(pairElement, "condition"));
                    Expression expression = loadExpressionOrAttribute(pairElement, "value", dataType);
                    conditionVariablePairs.add(new ExpressionConditional.ConditionVariablePair(condition, expression));
                }
                Expression expressionElse = loadExpression(LoadUtils.singleChildWithName(expressionElement, "else"), dataType);
                return new ExpressionConditional(dataType, conditionVariablePairs, expressionElse);
            }
            case null, default -> {
                String valueString = LoadUtils.attribute(expressionElement, "value", null);
                if (LoadUtils.isValidFloat(valueString)) {
                    return new ExpressionConstantFloat(Float.parseFloat(valueString));
                } else if (LoadUtils.isValidInteger(valueString)) {
                    return new ExpressionConstantInteger(Integer.parseInt(valueString));
                } else if (LoadUtils.isValidBoolean(valueString)) {
                    boolean valueBoolean = valueString.equalsIgnoreCase("t") || valueString.equalsIgnoreCase("true");
                    return new ExpressionConstantBoolean(valueBoolean);
                } else if (valueString != null) {
                    return new ExpressionConstantString(valueString);
                } else if (!expressionElement.getTextContent().isEmpty()) {
                    return new ExpressionConstantString(expressionElement.getTextContent());
                } else if (LoadUtils.hasChildWithName(expressionElement, "value")) {
                    Set<String> stringSet = LoadUtils.setOfTags(expressionElement, "value");
                    return new ExpressionConstantStringSet(stringSet);
                }
            }
        }
        return null;
    }

    private static Expression loadExpressionOrAttribute(Element parentElement, String name, String dataTypeDefault) {
        Expression expressionFromTag = loadExpression(LoadUtils.singleChildWithName(parentElement, name), dataTypeDefault);
        if (expressionFromTag != null) return expressionFromTag;
        String attributeValue = LoadUtils.attribute(parentElement, name, null);
        if (attributeValue == null) {
            return null;
        } else if (LoadUtils.isValidFloat(attributeValue)) {
            return new ExpressionConstantFloat(Float.parseFloat(attributeValue));
        } else if (LoadUtils.isValidInteger(attributeValue)) {
            return new ExpressionConstantInteger(Integer.parseInt(attributeValue));
        } else if (LoadUtils.isValidBoolean(attributeValue)) {
            boolean valueBoolean = attributeValue.equalsIgnoreCase("t") || attributeValue.equalsIgnoreCase("true");
            return new ExpressionConstantBoolean(valueBoolean);
        } else {
            return new ExpressionConstantString(attributeValue);
        }
    }

    private static Expression loadExpressionOrAttribute(Element parentElement, String name, String dataTypeDefault, Expression defaultExpression) {
        Expression resultExpression = loadExpressionOrAttribute(parentElement, name, dataTypeDefault);
        if (resultExpression != null) {
            return resultExpression;
        } else {
            return defaultExpression;
        }
    }

    private static StatHolderReference loadStatHolderReference(Element statHolderElement) {
        String holderType = LoadUtils.attribute(statHolderElement, "holder", "subject");
        Expression holderID = loadExpressionOrAttribute(statHolderElement, "holderID", "string");
        String subType = LoadUtils.attribute(statHolderElement, "subType", null);
        Expression subID = loadExpressionOrAttribute(statHolderElement, "subID", "string");
        return new StatHolderReference(holderType, holderID, subType, subID);
    }

    private static List<Script> loadSubScripts(Element parentElement) {
        List<Element> scriptElements = LoadUtils.directChildrenWithName(parentElement, "script");
        List<Script> scripts = new ArrayList<>();
        for (Element scriptElement : scriptElements) {
            Script script = loadScript(scriptElement);
            scripts.add(script);
        }
        return scripts;
    }

    private static Map<String, Script> loadScriptsWithTriggers(Element parentElement) {
        Map<String, Script> scripts = new HashMap<>();
        for (Element scriptElement : LoadUtils.directChildrenWithName(parentElement, "script")) {
            String trigger = scriptElement.getAttribute("trigger");
            Script script = loadScript(scriptElement);
            scripts.put(trigger, script);
        }
        return scripts;
    }

    private static Script loadScript(Element scriptElement) {
        if (scriptElement == null) return null;
        String type = scriptElement.getAttribute("type");
        Condition condition = loadCondition(LoadUtils.singleChildWithName(scriptElement, "condition"));
        Map<String, Expression> localParameters = new HashMap<>();
        for (Element parameterElement : LoadUtils.directChildrenWithName(scriptElement, "parameter")) {
            String parameterName = LoadUtils.attribute(parameterElement, "name", null);
            Expression parameterValue = loadExpression(parameterElement, null);
            localParameters.put(parameterName, parameterValue);
        }
        if (scriptElement.hasAttribute("external")) {
            String externalID = LoadUtils.attribute(scriptElement, "external", null);
            return new ScriptExternal(condition, localParameters, externalID);
        }
        switch (type) {
            case "external" -> {
                String scriptID = LoadUtils.attribute(scriptElement, "scriptID", null);
                return new ScriptExternal(condition, localParameters, scriptID);
            }
            case "transferItem" -> {
                Expression transferItemInvOrigin = loadExpression(LoadUtils.singleChildWithName(scriptElement, "fromInv"), "inventory");
                Expression transferItemInvTarget = loadExpression(LoadUtils.singleChildWithName(scriptElement, "toInv"), "inventory");
                Expression transferItemID = loadExpressionOrAttribute(scriptElement, "item", "string");
                ScriptTransferItem.TransferItemsType transferType = LoadUtils.attributeEnum(scriptElement, "transferType", ScriptTransferItem.TransferItemsType.class, ScriptTransferItem.TransferItemsType.COUNT);
                Expression transferItemCount = loadExpressionOrAttribute(scriptElement, "count", "int", new ExpressionConstantInteger(1));
                return new ScriptTransferItem(condition, localParameters, transferItemInvOrigin, transferItemInvTarget, transferItemID, transferType, transferItemCount);
            }
            case "scene" -> {
                StatHolderReference actorRef = loadStatHolderReference(LoadUtils.singleChildWithName(scriptElement, "actor"));
                Expression scenes = loadExpressionOrAttribute(scriptElement, "scene", "string");
                return new ScriptScene(condition, localParameters, actorRef, scenes);
            }
            case "combat" -> {
                StatHolderReference actorRef = loadStatHolderReference(LoadUtils.singleChildWithName(scriptElement, "actor"));
                StatHolderReference targetRef = loadStatHolderReference(LoadUtils.singleChildWithName(scriptElement, "target"));
                return new ScriptCombat(condition, localParameters, actorRef, targetRef);
            }
            case "factionRelation" -> {
                String targetFaction = LoadUtils.attribute(scriptElement, "targetFaction", null);
                String relationFaction = LoadUtils.attribute(scriptElement, "relationFaction", null);
                Faction.FactionRelation relation = LoadUtils.attributeEnum(scriptElement, "relation", Faction.FactionRelation.class, Faction.FactionRelation.NEUTRAL);
                return new ScriptFactionRelation(condition, localParameters, targetFaction, relationFaction, relation);
            }
            case "sensoryEvent" -> {
                Expression phrase = loadExpression(LoadUtils.singleChildWithName(scriptElement, "phrase"), "string");
                Expression phraseAudible = loadExpression(LoadUtils.singleChildWithName(scriptElement, "phraseAudible"), "string");
                Expression area = loadExpression(LoadUtils.singleChildWithName(scriptElement, "area"), "string");
                return new ScriptSensoryEvent(condition, localParameters, phrase, phraseAudible, area);
            }
            case "bark" -> {
                StatHolderReference actorRef = loadStatHolderReference(LoadUtils.singleChildWithName(scriptElement, "actor"));
                String barkTrigger = LoadUtils.attribute(scriptElement, "trigger", null);
                return new ScriptBark(condition, localParameters, actorRef, barkTrigger);
            }
            case "nearestActorScript" -> {
                Expression nearestTrigger = loadExpressionOrAttribute(scriptElement, "trigger", "string");
                return new ScriptNearestActorWithScript(condition, localParameters, nearestTrigger);
            }
            case "timerStart" -> {
                Expression timerID = loadExpressionOrAttribute(scriptElement, "timerID", "string");
                Expression timerDuration = loadExpressionOrAttribute(scriptElement, "duration", "int");
                Script timerScriptExpire = loadScript(LoadUtils.singleChildWithName(scriptElement, "scriptExpire"));
                Script timerScriptUpdate = loadScript(LoadUtils.singleChildWithName(scriptElement, "scriptUpdate"));
                return new ScriptTimerStart(condition, localParameters, timerID, timerDuration, timerScriptExpire, timerScriptUpdate);
            }
            case "setState" -> {
                StatHolderReference setStateHolder = loadStatHolderReference(scriptElement);
                Expression setStateName = loadExpressionOrAttribute(scriptElement, "stat", "string");
                Expression setStateExpression = loadExpressionOrAttribute(scriptElement, "value", null);
                return new ScriptSetState(condition, localParameters, setStateHolder, setStateName, setStateExpression);
            }
            case "modifyState" -> {
                StatHolderReference modifyStateHolder = loadStatHolderReference(scriptElement);
                Expression modifyStateName = loadExpressionOrAttribute(scriptElement, "stat", "string");
                Expression modifyStateExpression = loadExpressionOrAttribute(scriptElement, "value", null);
                return new ScriptModifyState(condition, localParameters, modifyStateHolder, modifyStateName, modifyStateExpression);
            }
            case "setGlobal" -> {
                Expression setGlobalID = loadExpressionOrAttribute(scriptElement, "globalID", "string");
                Expression setGlobalExpression = loadExpressionOrAttribute(scriptElement, "value", null);
                return new ScriptSetGlobal(condition, localParameters, setGlobalID, setGlobalExpression);
            }
            case "modifyGlobal" -> {
                Expression modifyGlobalID = loadExpressionOrAttribute(scriptElement, "globalID", "string");
                Expression modifyGlobalExpression = loadExpressionOrAttribute(scriptElement, "value", null);
                return new ScriptModifyGlobal(condition, localParameters, modifyGlobalID, modifyGlobalExpression);
            }
            case "iterator" -> {
                Expression setExpression = loadExpression(LoadUtils.singleChildWithName(scriptElement, "set"), "stringSet");
                String iteratorParameterName = LoadUtils.attribute(scriptElement, "itrName", null);
                Script iteratedScript = loadScript(LoadUtils.singleChildWithName(scriptElement, "script"));
                return new ScriptIterator(condition, localParameters, setExpression, iteratorParameterName, iteratedScript);
            }
            case "select" -> {
                List<Script> subScriptsSelect = loadSubScripts(scriptElement);
                return new ScriptCompound(condition, localParameters, subScriptsSelect, true);
            }
            case "all", default -> {
                List<Script> subScriptsSequence = loadSubScripts(scriptElement);
                return new ScriptCompound(condition, localParameters, subScriptsSequence, false);
            }
        }
    }

    private static Faction loadFaction(Element factionElement) {
        if (factionElement == null) return null;
        String id = factionElement.getAttribute("id");
        Faction.FactionRelation defaultRelation = LoadUtils.attributeEnum(factionElement, "default", Faction.FactionRelation.class, Faction.FactionRelation.NEUTRAL);
        Map<String, Faction.FactionRelation> relations = loadFactionRelations(factionElement);
        return new Faction(id, defaultRelation, relations);
    }

    private static Map<String, Faction.FactionRelation> loadFactionRelations(Element factionElement) {
        if (factionElement == null) return new HashMap<>();
        Map<String, Faction.FactionRelation> relations = new HashMap<>();
        List<Element> relationElements = LoadUtils.directChildrenWithName(factionElement, "relation");
        for (Element relationElement : relationElements) {
            String factionID = LoadUtils.attribute(relationElement, "faction", null);
            Faction.FactionRelation type = LoadUtils.attributeEnum(relationElement, "type", Faction.FactionRelation.class, Faction.FactionRelation.NEUTRAL);
            relations.put(factionID, type);
        }
        return relations;
    }

    private static ItemTemplate loadItemTemplate(Game game, Element itemElement) {
        if (itemElement == null) return null;
        String type = itemElement.getAttribute("type");
        String id = itemElement.getAttribute("id");
        String name = LoadUtils.singleTag(itemElement, "name", null);
        Scene description = loadScene(game, LoadUtils.singleChildWithName(itemElement, "description"));
        Map<String, Script> scripts = loadScriptsWithTriggers(itemElement);
        List<ActionCustom.CustomActionHolder> customActions = loadCustomActions(itemElement, "action");
        int price = LoadUtils.attributeInt(itemElement, "price", 0);
        switch (type) {
            case "apparel" -> {
                Set<Set<String>> apparelSlots = new HashSet<>();
                for (Element slotGroupElement : LoadUtils.directChildrenWithName(itemElement, "slotGroup")) {
                    Set<String> slotGroup = LoadUtils.setOfTags(slotGroupElement, "slot");
                    apparelSlots.add(slotGroup);
                }
                List<String> apparelEffects = LoadUtils.listOfTags(itemElement, "effect");
                List<ActionCustom.CustomActionHolder> equippedActions = loadCustomActions(itemElement, "equippedAction");
                return new EquippableTemplate(game, id, name, description, scripts, customActions, price, apparelSlots, apparelEffects, equippedActions);
            }
            case "consumable" -> {
                ConsumableTemplate.ConsumableType consumableType = LoadUtils.attributeEnum(itemElement, "consumableType", ConsumableTemplate.ConsumableType.class, ConsumableTemplate.ConsumableType.OTHER);
                List<String> consumableEffects = LoadUtils.listOfTags(itemElement, "effect");
                return new ConsumableTemplate(game, id, name, description, scripts, customActions, price, consumableType, consumableEffects);
            }
            case "weapon" -> {
                List<ActionCustom.CustomActionHolder> equippedActions = loadCustomActions(itemElement, "equippedAction");
                String weaponClass = LoadUtils.attribute(itemElement, "class", null);
                int weaponRate = LoadUtils.singleTagInt(itemElement, "rate", 1);
                Element damageElement = LoadUtils.singleChildWithName(itemElement, "damage");
                int weaponDamage = LoadUtils.attributeInt(damageElement, "base", 0);
                int critDamage = LoadUtils.attributeInt(damageElement, "crit", 0);
                float critChance = LoadUtils.attributeFloat(itemElement, "critChance", 0.0f);
                String weaponDamageType = LoadUtils.attribute(damageElement, "type", game.data().getConfig("defaultDamageType"));
                float weaponAccuracyBonus = LoadUtils.singleTagFloat(itemElement, "accuracyBonus", 0.0f);
                float weaponArmorMult = LoadUtils.singleTagFloat(itemElement, "armorMult", 1.0f);
                boolean weaponSilenced = LoadUtils.singleTagBoolean(itemElement, "silenced", false);
                int weaponClipSize = LoadUtils.singleTagInt(itemElement, "clipSize", 0);
                int weaponReloadActionPoints = LoadUtils.singleTagInt(itemElement, "reloadActionPoints", 1);
                Set<String> weaponTargetEffects = LoadUtils.setOfTags(itemElement, "targetEffect");
                Map<String, Integer> modSlots = new HashMap<>();
                for (Element modSlotElement : LoadUtils.directChildrenWithName(itemElement, "modSlot")) {
                    String slotName = LoadUtils.attribute(modSlotElement, "name", null);
                    int slotCount = LoadUtils.attributeInt(modSlotElement, "count", 1);
                    modSlots.put(slotName, slotCount);
                }
                return new WeaponTemplate(game, id, name, description, scripts, customActions, price, equippedActions, weaponClass, weaponDamage, weaponRate, critDamage, critChance, weaponClipSize, weaponReloadActionPoints, weaponAccuracyBonus, weaponArmorMult, weaponSilenced, weaponDamageType, weaponTargetEffects, modSlots);
            }
            case "mod" -> {
                String modSlot = LoadUtils.attribute(itemElement, "modSlot", null);
                List<String> effects = LoadUtils.listOfTags(itemElement, "effect");
                return new ModTemplate(game, id, name, description, scripts, customActions, price, modSlot, effects);
            }
            case "ammo" -> {
                List<String> ammoWeaponEffects = LoadUtils.listOfTags(itemElement, "weaponEffect");
                boolean ammoIsReusable = LoadUtils.attributeBool(itemElement, "isReusable", false);
                return new AmmoTemplate(game, id, name, description, scripts, customActions, price, ammoWeaponEffects, ammoIsReusable);
            }
            default -> {
                return new MiscTemplate(game, id, name, description, scripts, customActions, price);
            }
        }
    }

    private static List<Effect> loadEffects(Game game, Element effectsElement) {
        if (effectsElement == null) return new ArrayList<>();
        List<Element> effectElements = LoadUtils.directChildrenWithName(effectsElement, "effect");
        List<Effect> effects = new ArrayList<>();
        for (Element effectElement : effectElements) {
            effects.add(loadEffect(game, effectElement));
        }
        return effects;
    }

    private static Effect loadEffect(Game game, Element effectElement) {
        if (effectElement == null) return null;
        String ID = LoadUtils.attribute(effectElement, "id", null);
        boolean manualRemoval = LoadUtils.attributeBool(effectElement, "permanent", false);
        String effectType = LoadUtils.attribute(effectElement, "type", null);
        int duration = LoadUtils.attributeInt(effectElement, "duration", 0);
        boolean stackable = LoadUtils.attributeBool(effectElement, "stack", true);
        Condition conditionAdd = loadCondition(LoadUtils.singleChildWithName(effectElement, "conditionAdd"));
        Condition conditionRemove = loadCondition(LoadUtils.singleChildWithName(effectElement, "conditionRemove"));
        Condition conditionActive = loadCondition(LoadUtils.singleChildWithName(effectElement, "conditionActive"));
        Script scriptAdd = loadScript(LoadUtils.singleChildWithName(effectElement, "scriptAdd"));
        Script scriptRemove = loadScript(LoadUtils.singleChildWithName(effectElement, "scriptRemove"));
        Script scriptRound = loadScript(LoadUtils.singleChildWithName(effectElement, "scriptRound"));
        switch (effectType) {
            case "add" -> {
                String statMod = LoadUtils.attribute(effectElement, "stat", null);
                String statModValue = LoadUtils.attribute(effectElement, "amount", "0");
                Condition statCondition = loadCondition(LoadUtils.singleChildWithName(effectElement, "statCondition"));
                boolean statModIsFloat = statModValue.contains(".");
                if (statModIsFloat) {
                    float statModValueFloat = Float.parseFloat(statModValue);
                    return new EffectStatAddFloat(game, ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound, statMod, statModValueFloat, statCondition);
                } else {
                    int statModValueInt = Integer.parseInt(statModValue);
                    return new EffectStatAddInt(game, ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound, statMod, statModValueInt, statCondition);
                }
            }
            case "mult" -> {
                String statMult = LoadUtils.attribute(effectElement, "stat", null);
                float statMultAmount = LoadUtils.attributeFloat(effectElement, "amount", 0.0f);
                Condition statCondition = loadCondition(LoadUtils.singleChildWithName(effectElement, "statCondition"));
                return new EffectStatMult(game, ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound, statMult, statMultAmount, statCondition);
            }
            case "boolean" -> {
                String statBoolean = LoadUtils.attribute(effectElement, "stat", null);
                boolean statBooleanValue = LoadUtils.attributeBool(effectElement, "value", true);
                Condition statCondition = loadCondition(LoadUtils.singleChildWithName(effectElement, "statCondition"));
                return new EffectStatBoolean(game, ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound, statBoolean, statBooleanValue, statCondition);
            }
            case "string" -> {
                String statString = LoadUtils.attribute(effectElement, "stat", null);
                String statStringValue = LoadUtils.attribute(effectElement, "value", null);
                Condition statCondition = loadCondition(LoadUtils.singleChildWithName(effectElement, "statCondition"));
                return new EffectStatString(game, ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound, statString, statStringValue, statCondition);
            }
            case "stringSet" -> {
                String statStringSet = LoadUtils.attribute(effectElement, "stat", null);
                Set<String> stringSetValuesAdd = LoadUtils.setOfTags(effectElement, "add");
                Set<String> stringSetValuesRemove = LoadUtils.setOfTags(effectElement, "remove");
                Condition statCondition = loadCondition(LoadUtils.singleChildWithName(effectElement, "statCondition"));
                return new EffectStatStringSet(game, ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound, statStringSet, stringSetValuesAdd, stringSetValuesRemove, statCondition);
            }
            case "compound" -> {
                List<Effect> compoundEffects = loadEffects(game, effectElement);
                return new EffectCompound(game, ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound, compoundEffects);
            }
            case "basic", null, default -> {
                return new Effect(game, ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound);
            }
        }
    }

    private static LootTable loadLootTable(Element tableElement, boolean useAllDefault) {
        if (tableElement == null) return null;
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
        if (entryElement == null) return null;
        String referenceID = entryElement.getTextContent();
        float chance = LoadUtils.attributeFloat(entryElement, "chance", 1.0f);
        int count = LoadUtils.attributeInt(entryElement, "count", 1);
        int countMin = LoadUtils.attributeInt(entryElement, "countMin", count);
        int countMax = LoadUtils.attributeInt(entryElement, "countMax", count);
        String modTable = LoadUtils.attribute(entryElement, "modTable", null);
        String modItem = LoadUtils.attribute(entryElement, "modItem", null);
        float modChance = LoadUtils.attributeFloat(entryElement, "modChance", 1.0f);
        boolean modIsTable = modTable != null;
        String modReference = modIsTable ? modTable : modItem;
        return new LootTableEntry(referenceID, isTable, chance, countMin, countMax, modReference, modIsTable, modChance);
    }

    private static Room loadRoom(Game game, Element roomElement) {
        if (roomElement == null) return null;
        String roomID = roomElement.getAttribute("id");
        Element roomNameElement = LoadUtils.singleChildWithName(roomElement, "name");
        String roomName = roomNameElement.getTextContent();
        boolean roomNameIsProper = LoadUtils.attributeBool(roomNameElement, "proper", false);
        Scene roomDescription = loadScene(game, LoadUtils.singleChildWithName(roomElement, "description"));
        String roomOwnerFaction = LoadUtils.attribute(roomElement, "faction", null);
        Map<String, Script> roomScripts = loadScriptsWithTriggers(roomElement);

        List<Element> areaElements = LoadUtils.directChildrenWithName(roomElement, "area");
        Set<Area> areas = new HashSet<>();
        for (Element areaElement : areaElements) {
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

    private static Area loadArea(Game game, Element areaElement, String roomID) {
        if (areaElement == null) return null;
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
        String linkTypeDefault = game.data().getConfig("defaultLinkType");
        for (Element linkElement : linkElements) {
            String linkAreaID = LoadUtils.attribute(linkElement, "area", null);
            String linkType = LoadUtils.attribute(linkElement, "type", linkTypeDefault);
            AreaLink.CompassDirection linkDirection = LoadUtils.attributeEnum(linkElement, "dir", AreaLink.CompassDirection.class, AreaLink.CompassDirection.N);
            AreaLink.DistanceCategory linkDistance = LoadUtils.attributeEnum(linkElement, "dist", AreaLink.DistanceCategory.class, AreaLink.DistanceCategory.CLOSE);
            AreaLink link = new AreaLink(linkAreaID, linkType, linkDirection, linkDistance);
            linkSet.put(linkAreaID, link);
        }

        Map<String, Script> areaScripts = loadScriptsWithTriggers(areaElement);

        Area area = new Area(game, areaID, landmarkID, name, nameType, description, roomID, areaOwnerFaction, areaIsPrivate, linkSet, areaScripts);

        List<Element> objectElements = LoadUtils.directChildrenWithName(areaElement, "object");
        for (Element objectElement : objectElements) {
            WorldObject object = loadObject(game, objectElement, area);
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

    private static ObjectTemplate loadObjectTemplate(Game game, Element objectElement) throws GameDataException {
        String ID = LoadUtils.attribute(objectElement, "id", null);
        String name = LoadUtils.singleTag(objectElement, "name", null);
        Scene description = loadScene(game, LoadUtils.singleChildWithName(objectElement, "description"));
        Map<String, Script> scripts = loadScriptsWithTriggers(objectElement);
        List<ActionCustom.CustomActionHolder> customActions = loadCustomActions(objectElement, "action");
        List<ActionCustom.CustomActionHolder> networkActions = loadCustomActions(objectElement, "networkAction");
        Map<String, ObjectComponentTemplate> components = new HashMap<>();
        for (Element componentElement : LoadUtils.directChildrenWithName(objectElement, "component")) {
            String componentID = LoadUtils.attribute(componentElement, "id", null);
            ObjectComponentTemplate componentTemplate = loadObjectComponentTemplate(game, componentElement);
            components.put(componentID, componentTemplate);
        }
        Map<String, Boolean> localVarsBooleanDefault = new HashMap<>();
        Map<String, Integer> localVarsIntegerDefault = new HashMap<>();
        Map<String, Float> localVarsFloatDefault = new HashMap<>();
        Map<String, String> localVarsStringDefault = new HashMap<>();
        Map<String, Set<String>> localVarsStringSetDefault = new HashMap<>();
        for (Element varDefaultElement : LoadUtils.directChildrenWithName(objectElement, "localVar")) {
            String varName = LoadUtils.attribute(varDefaultElement, "name", null);
            String varDataType = LoadUtils.attribute(varDefaultElement, "dataType", null);
            switch (varDataType) {
                case "boolean" -> {
                    boolean booleanValue = LoadUtils.attributeBool(varDefaultElement, "value", false);
                    localVarsBooleanDefault.put(varName, booleanValue);
                }
                case "int" -> {
                    int integerValue = LoadUtils.attributeInt(varDefaultElement, "value", 0);
                    localVarsIntegerDefault.put(varName, integerValue);
                }
                case "float" -> {
                    float floatValue = LoadUtils.attributeFloat(varDefaultElement, "value", 0.0f);
                    localVarsFloatDefault.put(varName, floatValue);
                }
                case "string" -> {
                    String stringValue = LoadUtils.attribute(varDefaultElement, "value", "EMPTY");
                    localVarsStringDefault.put(varName, stringValue);
                }
                case "stringSet" -> {
                    Set<String> stringSetValue = LoadUtils.setOfTags(varDefaultElement, "value");
                    localVarsStringSetDefault.put(varName, stringSetValue);
                }
            }
        }
        return new ObjectTemplate(game, ID, name, description, scripts, customActions, networkActions, components, localVarsBooleanDefault, localVarsIntegerDefault, localVarsFloatDefault, localVarsStringDefault, localVarsStringSetDefault);
    }

    private static WorldObject loadObject(Game game, Element objectElement, Area area) {
        if (objectElement == null) return null;
        String template = LoadUtils.attribute(objectElement, "template", null);
        String id = LoadUtils.attribute(objectElement, "id", null);
        boolean startDisabled = LoadUtils.attributeBool(objectElement, "startDisabled", false);
        boolean startHidden = LoadUtils.attributeBool(objectElement, "startHidden", false);
        Map<String, Boolean> localVarsBooleanDefault = new HashMap<>();
        Map<String, Integer> localVarsIntegerDefault = new HashMap<>();
        Map<String, Float> localVarsFloatDefault = new HashMap<>();
        Map<String, String> localVarsStringDefault = new HashMap<>();
        Map<String, Set<String>> localVarsStringSetDefault = new HashMap<>();
        for (Element varDefaultElement : LoadUtils.directChildrenWithName(objectElement, "localVar")) {
            String varName = LoadUtils.attribute(varDefaultElement, "name", null);
            String varDataType = LoadUtils.attribute(varDefaultElement, "dataType", null);
            switch (varDataType) {
                case "boolean" -> {
                    boolean booleanValue = LoadUtils.attributeBool(varDefaultElement, "value", false);
                    localVarsBooleanDefault.put(varName, booleanValue);
                }
                case "int" -> {
                    int integerValue = LoadUtils.attributeInt(varDefaultElement, "value", 0);
                    localVarsIntegerDefault.put(varName, integerValue);
                }
                case "float" -> {
                    float floatValue = LoadUtils.attributeFloat(varDefaultElement, "value", 0.0f);
                    localVarsFloatDefault.put(varName, floatValue);
                }
                case "string" -> {
                    String stringValue = LoadUtils.attribute(varDefaultElement, "value", "EMPTY");
                    localVarsStringDefault.put(varName, stringValue);
                }
                case "stringSet" -> {
                    Set<String> stringSetValue = LoadUtils.setOfTags(varDefaultElement, "value");
                    localVarsStringSetDefault.put(varName, stringSetValue);
                }
            }
        }
        return new WorldObject(game, id, template, area, startDisabled, startHidden, localVarsBooleanDefault, localVarsIntegerDefault, localVarsFloatDefault, localVarsStringDefault, localVarsStringSetDefault);
    }

    private static ObjectComponentTemplate loadObjectComponentTemplate(Game game, Element componentElement) throws GameDataException {
        String type = LoadUtils.attribute(componentElement, "type", null);
        boolean startEnabled = LoadUtils.attributeBool(componentElement, "startEnabled", true);
        boolean actionsRestricted = LoadUtils.attributeBool(componentElement, "restricted", false);
        String name = LoadUtils.singleTag(componentElement, "name", null);
        switch (type) {
            case "inventory" -> {
                LootTable lootTable = loadLootTable(LoadUtils.singleChildWithName(componentElement, "inventory"), true);
                boolean inventoryIsExposed = LoadUtils.attributeBool(componentElement, "exposed", false);
                boolean enableTake = LoadUtils.attributeBool(componentElement, "enableTake", true);
                boolean enableStore = LoadUtils.attributeBool(componentElement, "enableStore", true);
                List<ActionCustom.CustomActionHolder> perItemActions = loadCustomActions(componentElement, "itemAction");
                return new ObjectComponentTemplateInventory(game, startEnabled, actionsRestricted, name, lootTable, inventoryIsExposed, enableTake, enableStore, perItemActions);
            }
            case "network" -> {
                Expression networkID = loadExpressionOrAttribute(componentElement, "networkID", "string");
                return new ObjectComponentTemplateNetwork(game, startEnabled, actionsRestricted, name, networkID);
            }
            case "link" -> {
                Condition linkCondition = loadCondition(LoadUtils.singleChildWithName(componentElement, "condition"));
                boolean linkIsMovable = LoadUtils.attributeBool(componentElement, "movable", true);
                boolean linkIsVisible = LoadUtils.attributeBool(componentElement, "visible", false);
                return new ObjectComponentTemplateLink(game, startEnabled, actionsRestricted, name, linkCondition, linkIsMovable, linkIsVisible);
            }
            case "usable" -> {
                String usableStartPhrase = LoadUtils.singleTag(componentElement, "startPhrase", null);
                String usableEndPhrase = LoadUtils.singleTag(componentElement, "endPhrase", null);
                String usableStartPrompt = LoadUtils.singleTag(componentElement, "startPrompt", null);
                String usableEndPrompt = LoadUtils.singleTag(componentElement, "endPrompt", null);
                boolean userIsInCover = LoadUtils.attributeBool(componentElement, "cover", false);
                boolean userIsHidden = LoadUtils.attributeBool(componentElement, "hidden", false);
                boolean userCanSeeOtherAreas = LoadUtils.attributeBool(componentElement, "seeOtherAreas", true);
                boolean userCanPerformLocalActions = LoadUtils.attributeBool(componentElement, "localActions", true);
                boolean userCanPerformParentActions = LoadUtils.attributeBool(componentElement, "parentActions", true);
                Set<String> componentsExposed = LoadUtils.setOfTags(componentElement, "exposedComponent");
                List<ActionCustom.CustomActionHolder> usingActions = loadCustomActions(componentElement, "usingAction");
                return new ObjectComponentTemplateUsable(game, startEnabled, actionsRestricted, name, usableStartPhrase, usableEndPhrase, usableStartPrompt, usableEndPrompt, userIsInCover, userIsHidden, userCanSeeOtherAreas, userCanPerformLocalActions, userCanPerformParentActions, componentsExposed, usingActions);
            }
            case "vehicle" -> {
                String vehicleType = LoadUtils.attribute(componentElement, "vehicleType", null);
                String moveMenuName = LoadUtils.singleTag(componentElement, "moveMenuName", null);
                return new ObjectComponentTemplateVehicle(game, startEnabled, actionsRestricted, name, vehicleType, moveMenuName);
            }
            default -> throw new GameDataException("ObjectComponentTemplate has invalid or missing type");
        }
    }

    private static ActionTemplate loadActionTemplate(Game game, Element actionElement) {
        String ID = LoadUtils.attribute(actionElement, "id", null);
        String prompt = LoadUtils.singleTag(actionElement, "prompt", null);
        Map<String, Expression> parameters = new HashMap<>();
        for (Element parameterElement : LoadUtils.directChildrenWithName(actionElement, "parameter")) {
            String parameterName = LoadUtils.attribute(parameterElement, "name", null);
            Expression parameterValue = loadExpression(parameterElement, null);
            parameters.put(parameterName, parameterValue);
        }
        int actionPoints = LoadUtils.attributeInt(actionElement, "actionPoints", 0);
        Condition conditionSelect = loadCondition(LoadUtils.singleChildWithName(actionElement, "condition"));
        Condition conditionShow = loadCondition(LoadUtils.singleChildWithName(actionElement, "conditionShow"));
        Script script = loadScript(LoadUtils.singleChildWithName(actionElement, "script"));
        return new ActionTemplate(game, ID, prompt, parameters, actionPoints, conditionSelect, conditionShow, script);
    }

    private static List<ActionCustom.CustomActionHolder> loadCustomActions(Element parentElement, String name) {
        List<ActionCustom.CustomActionHolder> customActions = new ArrayList<>();
        if (parentElement != null) {
            for (Element actionElement : LoadUtils.directChildrenWithName(parentElement, name)) {
                String action = LoadUtils.attribute(actionElement, "template", null);
                Map<String, Expression> parameters = new HashMap<>();
                for (Element variableElement : LoadUtils.directChildrenWithName(actionElement, "parameter")) {
                    String parameterName = LoadUtils.attribute(variableElement, "name", null);
                    Expression parameterExpression = loadExpression(variableElement, null);
                    parameters.put(parameterName, parameterExpression);
                }
                customActions.add(new ActionCustom.CustomActionHolder(action, parameters));
            }
        }
        return customActions;
    }

    private static LinkType loadLinkType(Game game, Element linkTypeElement) {
        String ID = LoadUtils.attribute(linkTypeElement, "id", null);
        boolean isVisible = LoadUtils.attributeBool(linkTypeElement, "visible", true);
        String actorMoveAction = LoadUtils.attribute(linkTypeElement, "moveAction", null);
        Set<AreaLink.DistanceCategory> actorMoveDistances = LoadUtils.setOfEnumTags(linkTypeElement, "moveDistance", AreaLink.DistanceCategory.class);
        Map<String, String> vehicleMoveActions = new HashMap<>();
        Map<String, Set<AreaLink.DistanceCategory>> vehicleMoveDistances = new HashMap<>();
        for (Element vehicleTypeElement : LoadUtils.directChildrenWithName(linkTypeElement, "vehicleMoveAction")) {
            String vehicleType = LoadUtils.attribute(vehicleTypeElement, "type", null);
            String vehicleAction = LoadUtils.attribute(vehicleTypeElement, "action", null);
            Set<AreaLink.DistanceCategory> vehicleTypeMoveDistances = LoadUtils.setOfEnumTags(linkTypeElement, "moveDistance", AreaLink.DistanceCategory.class);
            vehicleMoveActions.put(vehicleType, vehicleAction);
            vehicleMoveDistances.put(vehicleType, vehicleTypeMoveDistances);
        }
        return new LinkType(game, ID, isVisible, actorMoveAction, actorMoveDistances, vehicleMoveActions, vehicleMoveDistances);
    }

    private static Actor loadActorInstance(Game game, Element actorElement, Area area) {
        if (actorElement == null) return null;
        String ID = actorElement.getAttribute("id");
        String template = LoadUtils.attribute(actorElement, "template", null);
        List<Behavior> behaviors = loadBehaviors(LoadUtils.singleChildWithName(actorElement, "behaviors"));
        boolean startDead = LoadUtils.attributeBool(actorElement, "startDead", false);
        boolean startDisabled = LoadUtils.attributeBool(actorElement, "startDisabled", false);
        return ActorFactory.create(game, ID, area, template, behaviors, startDead, startDisabled);
    }

    private static List<Behavior> loadBehaviors(Element behaviorsElement) {
        if (behaviorsElement == null) return new ArrayList<>();
        List<Behavior> behaviors = new ArrayList<>();
        for (Element behaviorElement : LoadUtils.directChildrenWithName(behaviorsElement, "behavior")) {
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
                case "move" -> {
                    String areaTarget = LoadUtils.attribute(behaviorElement, "area", null);
                    behavior = new BehaviorMove(condition, duration, idles, areaTarget);
                }
                case "use" -> {
                    String objectTarget = LoadUtils.attribute(behaviorElement, "object", null);
                    String componentTarget = LoadUtils.attribute(behaviorElement, "component", null);
                    behavior = new BehaviorUse(condition, duration, idles, objectTarget, componentTarget);
                }
                case "guard" -> {
                    String guardTarget = LoadUtils.attribute(behaviorElement, "object", null);
                    behavior = new BehaviorGuard(condition, duration, idles, guardTarget);
                }
                case "sleep" -> {
                    String bedTarget = LoadUtils.attribute(behaviorElement, "bed", null);
                    behavior = new BehaviorSleep(condition, idles, bedTarget);
                }
                case "procedure" -> {
                    List<Behavior> procedureBehaviors = loadBehaviors(behaviorElement);
                    boolean isCycle = LoadUtils.attributeBool(behaviorElement, "isCycle", false);
                    behavior = new BehaviorProcedure(condition, isCycle, procedureBehaviors);
                }
                default -> behavior = null;
            }
            behaviors.add(behavior);
        }
        return behaviors;
    }

    private static Idle loadIdle(Element idleElement) {
        Condition condition = loadCondition(LoadUtils.singleChildWithName(idleElement, "condition"));
        String phrase = LoadUtils.singleTag(idleElement, "phrase", null);
        return new Idle(condition, phrase);
    }

    private static WeaponClass loadWeaponClass(Element weaponClassElement) {
        String ID = LoadUtils.attribute(weaponClassElement, "id", null);
        String name = LoadUtils.singleTag(weaponClassElement, "name", null);
        boolean isRanged = LoadUtils.attributeBool(weaponClassElement, "isRanged", false);
        boolean isTwoHanded = LoadUtils.attributeBool(weaponClassElement, "isTwoHanded", false);
        boolean isLoud = LoadUtils.attributeBool(weaponClassElement, "isLoud", false);
        Actor.Skill skill = LoadUtils.attributeEnum(weaponClassElement, "skill", Actor.Skill.class, Actor.Skill.MELEE);
        Set<AreaLink.DistanceCategory> primaryRanges = LoadUtils.setOfEnumTags(weaponClassElement, "range", AreaLink.DistanceCategory.class);
        Set<String> ammoTypes = LoadUtils.setOfTags(weaponClassElement, "ammo");
        Set<String> attackTypes = LoadUtils.setOfTags(weaponClassElement, "attackType");
        return new WeaponClass(ID, name, isRanged, isTwoHanded, isLoud, skill, primaryRanges, ammoTypes, attackTypes);
    }

    private static WeaponAttackType loadWeaponAttackType(Element attackTypeElement) {
        String ID = LoadUtils.attribute(attackTypeElement, "id", null);
        WeaponAttackType.AttackCategory category = LoadUtils.attributeEnum(attackTypeElement, "category", WeaponAttackType.AttackCategory.class, WeaponAttackType.AttackCategory.SINGLE);
        String prompt = LoadUtils.singleTag(attackTypeElement, "prompt", null);
        String hitPhrase = LoadUtils.singleTag(attackTypeElement, "hitPhrase", null);
        String hitPhraseRepeat = LoadUtils.singleTag(attackTypeElement, "hitPhraseRepeat", null);
        String hitOverallPhrase = LoadUtils.singleTag(attackTypeElement, "hitOverallPhrase", null);
        String hitOverallPhraseRepeat = LoadUtils.singleTag(attackTypeElement, "hitOverallPhraseRepeat", null);
        String hitPhraseAudible = LoadUtils.singleTag(attackTypeElement, "hitPhraseAudible", null);
        String hitPhraseRepeatAudible = LoadUtils.singleTag(attackTypeElement, "hitPhraseRepeatAudible", null);
        String hitOverallPhraseAudible = LoadUtils.singleTag(attackTypeElement, "hitOverallPhraseAudible", null);
        String hitOverallPhraseRepeatAudible = LoadUtils.singleTag(attackTypeElement, "hitOverallPhraseRepeatAudible", null);
        String missPhrase = LoadUtils.singleTag(attackTypeElement, "missPhrase", null);
        String missPhraseRepeat = LoadUtils.singleTag(attackTypeElement, "missPhraseRepeat", null);
        String missOverallPhrase = LoadUtils.singleTag(attackTypeElement, "missOverallPhrase", null);
        String missOverallPhraseRepeat = LoadUtils.singleTag(attackTypeElement, "missOverallPhraseRepeat", null);
        String missPhraseAudible = LoadUtils.singleTag(attackTypeElement, "missPhraseAudible", null);
        String missPhraseRepeatAudible = LoadUtils.singleTag(attackTypeElement, "missPhraseRepeatAudible", null);
        String missOverallPhraseAudible = LoadUtils.singleTag(attackTypeElement, "missOverallPhraseAudible", null);
        String missOverallPhraseRepeatAudible = LoadUtils.singleTag(attackTypeElement, "missOverallPhraseRepeatAudible", null);
        int ammoConsumed = LoadUtils.attributeInt(attackTypeElement, "ammoConsumed", 1);
        WeaponAttackType.WeaponConsumeType weaponConsumeType = LoadUtils.attributeEnum(attackTypeElement, "weaponConsumeType", WeaponAttackType.WeaponConsumeType.class, WeaponAttackType.WeaponConsumeType.NONE);
        Actor.Skill skillOverride = LoadUtils.attributeEnum(attackTypeElement, "skill", Actor.Skill.class, null);
        Float baseHitChanceMin = LoadUtils.attributeFloat(attackTypeElement, "hitChanceMin", null);
        Float baseHitChanceMax = LoadUtils.attributeFloat(attackTypeElement, "hitChanceMax", null);
        boolean useNonIdealRange = LoadUtils.attributeBool(attackTypeElement, "nonIdealRange", false);
        Set<AreaLink.DistanceCategory> rangeOverride = LoadUtils.setOfEnumTags(attackTypeElement, "range", AreaLink.DistanceCategory.class);
        Integer rateOverride = LoadUtils.attributeInt(attackTypeElement, "rate", null);
        Integer damageOverride = LoadUtils.attributeInt(attackTypeElement, "damage", null);
        float damageMult = LoadUtils.attributeFloat(attackTypeElement, "damageMult", 0.0f);
        String damageTypeOverride = LoadUtils.attribute(attackTypeElement, "damageType", null);
        Float armorMultOverride = LoadUtils.attributeFloat(attackTypeElement, "armorMult", null);
        List<String> targetEffects = LoadUtils.listOfTags(attackTypeElement, "targetEffect");
        boolean overrideTargetEffects = LoadUtils.attributeBool(attackTypeElement, "overrideEffects", false);
        float hitChanceMult = LoadUtils.attributeFloat(attackTypeElement, "hitChanceMult", 0.0f);
        boolean canDodge = LoadUtils.attributeBool(attackTypeElement, "canDodge", false);
        ActionAttack.AttackHitChanceType hitChanceType = LoadUtils.attributeEnum(attackTypeElement, "hitChanceType", ActionAttack.AttackHitChanceType.class, ActionAttack.AttackHitChanceType.INDEPENDENT);
        Boolean isLoudOverride = LoadUtils.attributeBool(attackTypeElement, "isLoudOverride", null);
        return new WeaponAttackType(ID, category, prompt, hitPhrase, hitPhraseRepeat, hitOverallPhrase, hitOverallPhraseRepeat, hitPhraseAudible, hitPhraseRepeatAudible, hitOverallPhraseAudible, hitOverallPhraseRepeatAudible, missPhrase, missPhraseRepeat, missOverallPhrase, missOverallPhraseRepeat, missPhraseAudible, missPhraseRepeatAudible, missOverallPhraseAudible, missOverallPhraseRepeatAudible, ammoConsumed, weaponConsumeType, skillOverride, baseHitChanceMin, baseHitChanceMax, useNonIdealRange, rangeOverride, rateOverride, damageOverride, damageMult, damageTypeOverride, armorMultOverride, targetEffects, overrideTargetEffects, hitChanceMult, canDodge, hitChanceType, isLoudOverride);
    }

    private static Network loadNetwork(Element networkElement) {
        String ID = LoadUtils.attribute(networkElement, "id", null);
        String name = LoadUtils.singleTag(networkElement, "name", null);
        NetworkNode topNode = loadNetworkNode(LoadUtils.singleChildWithName(networkElement, "node"));
        return new Network(ID, name, topNode);
    }

    private static NetworkNode loadNetworkNode(Element nodeElement) {
        String type = LoadUtils.attribute(nodeElement, "type", null);
        String ID = LoadUtils.attribute(nodeElement, "id", null);
        String name = LoadUtils.singleTag(nodeElement, "name", null);
        int securityLevel = LoadUtils.attributeInt(nodeElement, "securityLevel", 0);
        switch (type) {
            case "data" -> {
                String dataSceneID = LoadUtils.attribute(nodeElement, "scene", null);
                return new NetworkNodeData(ID, name, securityLevel, dataSceneID);
            }
            case "control" -> {
                String controlObjectID = LoadUtils.attribute(nodeElement, "object", null);
                return new NetworkNodeControl(ID, name, securityLevel, controlObjectID);
            }
            case null, default -> {
                Set<NetworkNode> groupNodes = new HashSet<>();
                for (Element childNodeElement : LoadUtils.directChildrenWithName(nodeElement, "node")) {
                    NetworkNode childNode = loadNetworkNode(childNodeElement);
                    groupNodes.add(childNode);
                }
                return new NetworkNodeGroup(ID, name, securityLevel, groupNodes);
            }
        }
    }

}
