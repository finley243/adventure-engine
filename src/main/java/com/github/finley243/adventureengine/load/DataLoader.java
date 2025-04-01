package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameDataException;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.action.ActionTemplate;
import com.github.finley243.adventureengine.actor.*;
import com.github.finley243.adventureengine.actor.ai.Idle;
import com.github.finley243.adventureengine.actor.ai.behavior.*;
import com.github.finley243.adventureengine.combat.DamageType;
import com.github.finley243.adventureengine.combat.WeaponAttackType;
import com.github.finley243.adventureengine.combat.WeaponClass;
import com.github.finley243.adventureengine.condition.Condition;
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
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.textgen.TextContext;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;
import com.github.finley243.adventureengine.world.environment.LinkType;
import com.github.finley243.adventureengine.world.environment.Room;
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
import java.nio.file.Files;
import java.util.*;

public class DataLoader {

    private static final String SCRIPT_FILE_EXTENSION = "ascr";
    private static final String PHRASE_FILE_EXTENSION = "aphr";

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
                                    Faction faction = loadFaction(game, currentElement);
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
                                case "area" -> {
                                    Area area = loadArea(game, currentElement);
                                    game.data().addArea(area.getID(), area);
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
                                case "networkNode" -> {
                                    NetworkNode networkNode = loadNetworkNode(game, currentElement);
                                    game.data().addNetworkNode(networkNode.getID(), networkNode);
                                }
                                case "networkNodeTemplate" -> {
                                    NetworkNodeTemplate networkNodeTemplate = loadNetworkNodeTemplate(currentElement);
                                    game.data().addNetworkNodeTemplate(networkNodeTemplate.getID(), networkNodeTemplate);
                                }
                                case "damageType" -> {
                                    DamageType damageType = loadDamageType(currentElement);
                                    game.data().addDamageType(damageType.ID(), damageType);
                                }
                                case "attribute" -> {
                                    Attribute attribute = loadAttribute(currentElement);
                                    game.data().addAttribute(attribute.ID(), attribute);
                                }
                                case "skill" -> {
                                    Skill skill = loadSkill(currentElement);
                                    game.data().addSkill(skill.ID(), skill);
                                }
                                case "senseType" -> {
                                    SenseType senseType = loadSenseType(currentElement);
                                    game.data().addSenseType(senseType.ID(), senseType);
                                }
                            }
                        }
                        currentChild = currentChild.getNextSibling();
                    }
                } else if (file.getName().substring(file.getName().lastIndexOf(".") + 1).equalsIgnoreCase(SCRIPT_FILE_EXTENSION)) {
                    String fileContents = Files.readString(file.toPath());
                    List<ScriptParser.ScriptData> functions = ScriptParser.parseFunctions(fileContents, file.getName());
                    for (ScriptParser.ScriptData function : functions) {
                        game.data().addScript(function.name(), function);
                    }
                } else if (file.getName().substring(file.getName().lastIndexOf(".") + 1).equalsIgnoreCase(PHRASE_FILE_EXTENSION)) {
                    Phrases.load(file);
                }
            }
        }
    }

    private static DamageType loadDamageType(Element element) {
        String ID = LoadUtils.attribute(element, "id", null);
        String name = element.getTextContent();
        return new DamageType(ID, name);
    }

    private static Attribute loadAttribute(Element element) {
        String ID = LoadUtils.attribute(element, "id", null);
        String name = element.getTextContent();
        return new Attribute(ID, name);
    }

    private static Skill loadSkill(Element element) {
        String ID = LoadUtils.attribute(element, "id", null);
        String name = element.getTextContent();
        return new Skill(ID, name);
    }

    private static SenseType loadSenseType(Element element) {
        String ID = LoadUtils.attribute(element, "id", null);
        String name = element.getTextContent();
        return new SenseType(ID, name);
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

        Integer actionPoints = LoadUtils.attributeInt(actorElement, "actionPoints", null);
        Integer movePoints = LoadUtils.attributeInt(actorElement, "movePoints", null);
        Integer startingLevel = LoadUtils.attributeInt(actorElement, "startLevel", null);
        Script levelUpThresholdExpression = loadExpressionScript(LoadUtils.singleChildWithName(actorElement, "levelUpThreshold"), "Actor(" + id + ") - level up threshold");
        Integer hp = LoadUtils.attributeInt(actorElement, "hp", null);
        Map<String, Integer> damageResistances = new HashMap<>();
        Map<String, Float> damageMults = new HashMap<>();
        for (Element damageElement : LoadUtils.directChildrenWithName(actorElement, "damage")) {
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
        List<Limb> limbs = loadLimbs(actorElement);
        Map<String, EquipSlot> equipSlots = new HashMap<>();
        for (Element slotElement : LoadUtils.directChildrenWithName(actorElement, "equipSlot")) {
            String slotID = LoadUtils.attribute(slotElement, "id", null);
            String slotName = slotElement.getTextContent();
            equipSlots.put(slotID, new EquipSlot(slotID, slotName));
        }
        LootTable lootTable = loadLootTable(LoadUtils.singleChildWithName(actorElement, "inventory"), true);
        String dialogueStart = LoadUtils.attribute(actorElement, "dialogueStart", null);
        Map<String, Integer> attributes = new HashMap<>();
        for (Element attributeElement : LoadUtils.directChildrenWithName(actorElement, "attribute")) {
            String attribute = LoadUtils.attribute(attributeElement, "key", null);
            int value = LoadUtils.attributeInt(attributeElement, "value", 0);
            attributes.put(attribute, value);
        }
        Map<String, Integer> skills = new HashMap<>();
        for (Element skillElement : LoadUtils.directChildrenWithName(actorElement, "skill")) {
            String skill = LoadUtils.attribute(skillElement, "key", null);
            int value = LoadUtils.attributeInt(skillElement, "value", 0);
            skills.put(skill, value);
        }
        Set<String> senseTypes = LoadUtils.setOfTags(actorElement, "senseType");
        Set<String> tags = LoadUtils.setOfTags(actorElement, "tag");
        List<String> unarmedAttackTypes = LoadUtils.listOfTags(actorElement, "attackType");
        Map<String, List<Script>> scripts = loadScriptsWithTriggers(actorElement, "Actor(" + id + ")");
        List<String> startingEffects = LoadUtils.listOfTags(actorElement, "startEffect");

        Map<String, Bark> barks = new HashMap<>();
        for (Element barkElement : LoadUtils.directChildrenWithName(actorElement, "bark")) {
            String barkTrigger = LoadUtils.attribute(barkElement, "trigger", null);
            Bark.BarkResponseType responseType = LoadUtils.attributeEnum(barkElement, "response", Bark.BarkResponseType.class, Bark.BarkResponseType.NONE);
            List<String> visiblePhrases = LoadUtils.listOfTags(barkElement, "visible");
            List<String> nonVisiblePhrases = LoadUtils.listOfTags(barkElement, "nonVisible");
            barks.put(barkTrigger, new Bark(responseType, visiblePhrases, nonVisiblePhrases));
        }

        List<ActionCustom.CustomActionHolder> customActions = loadCustomActions(actorElement, "action", "ActorTemplate(" + id + ")");
        List<ActionCustom.CustomActionHolder> customInventoryActions = loadCustomActions(actorElement, "itemAction", "ActorTemplate(" + id + ")");

        return new ActorTemplate(game, id, parentID, name, nameIsProper, pronoun, faction, isEnforcer, actionPoints, movePoints, startingLevel, levelUpThresholdExpression, hp, damageResistances, damageMults, limbs, equipSlots, attributes, skills, senseTypes, tags, unarmedAttackTypes, startingEffects, lootTable, dialogueStart, scripts, barks, customActions, customInventoryActions);
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

    private static Scene loadScene(Game game, Element sceneElement) {
        if (sceneElement == null) return null;
        String sceneID = LoadUtils.attribute(sceneElement, "id", null);
        Scene.SceneType type = switch (LoadUtils.attribute(sceneElement, "type", "all")) {
            case "random" -> Scene.SceneType.RANDOM;
            case "select" -> Scene.SceneType.SELECTOR;
            default -> Scene.SceneType.SEQUENTIAL; // "all"
        };
        Condition condition = loadCondition(LoadUtils.singleChildWithName(sceneElement, "condition"), "Scene(" + sceneID + ") - condition");
        boolean once = LoadUtils.attributeBool(sceneElement, "once", false);
        int priority = LoadUtils.attributeInt(sceneElement, "priority", 1);
        List<Element> lineElements = LoadUtils.directChildrenWithName(sceneElement, "line");
        List<SceneLine> lines = new ArrayList<>();
        for (Element lineElement : lineElements) {
            SceneLine line = loadSceneLine(lineElement, sceneID);
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

    private static SceneLine loadSceneLine(Element lineElement, String sceneID) {
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
                default -> Scene.SceneType.SEQUENTIAL; // "all"
            };
            Condition condition = loadCondition(LoadUtils.singleChildWithName(lineElement, "condition"), "Scene(" + sceneID + ") - line condition");
            Script scriptPre = loadScript(LoadUtils.singleChildWithName(lineElement, "scriptPre"), "Scene(" + sceneID + ") - line pre-script");
            Script scriptPost = loadScript(LoadUtils.singleChildWithName(lineElement, "scriptPost"), "Scene(" + sceneID + ") - line post-script");
            List<SceneLine> subLines = new ArrayList<>();
            for (Element subLineElement : LoadUtils.directChildrenWithName(lineElement, "line")) {
                SceneLine subLine = loadSceneLine(subLineElement, sceneID);
                subLines.add(subLine);
            }
            return new SceneLine(type, subLines, condition, scriptPre, scriptPost, once, exit, redirect, from);
        }
    }

    private static SceneChoice loadSceneChoice(Element choiceElement) {
        String link = choiceElement.getAttribute("link");
        String prompt = choiceElement.getTextContent();
        return new SceneChoice(link, prompt);
    }

    private static Condition loadCondition(Element conditionElement, String traceString) {
        if (conditionElement == null) return null;
        Script conditionScript = loadExpressionScript(conditionElement, traceString);
        return new Condition(conditionScript);
    }

    private static Expression loadExpressionOrAttribute(Element parentElement, String traceString) {
        if (parentElement == null) return null;
        String expressionText = parentElement.getTextContent().trim();
        return ScriptParser.parseLiteral(expressionText, traceString);
    }

    private static Map<String, List<Script>> loadScriptsWithTriggers(Element parentElement, String traceString) {
        Map<String, List<Script>> scripts = new HashMap<>();
        for (Element scriptElement : LoadUtils.directChildrenWithName(parentElement, "script")) {
            String trigger = scriptElement.getAttribute("trigger");
            Script script = loadScript(scriptElement, traceString + " - script trigger: " + trigger);
            if (!scripts.containsKey(trigger)) {
                scripts.put(trigger, new ArrayList<>());
            }
            scripts.get(trigger).add(script);
        }
        return scripts;
    }

    private static Script loadScript(Element scriptElement, String traceString) {
        if (scriptElement == null) return null;
        String scriptText = scriptElement.getTextContent().trim();
        return ScriptParser.parseScript(scriptText, traceString);
    }

    private static Script loadExpressionScript(Element scriptElement, String traceString) {
        if (scriptElement == null) return null;
        String scriptText = scriptElement.getTextContent().trim();
        return ScriptParser.parseExpression(scriptText, traceString);
    }

    private static Faction loadFaction(Game game, Element factionElement) {
        if (factionElement == null) return null;
        String id = factionElement.getAttribute("id");
        Faction.FactionRelation defaultRelation = LoadUtils.attributeEnum(factionElement, "default", Faction.FactionRelation.class, Faction.FactionRelation.NEUTRAL);
        Map<String, Faction.FactionRelation> relations = loadFactionRelations(factionElement);
        return new Faction(game, id, defaultRelation, relations);
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
        String id = itemElement.getAttribute("id");
        String name = LoadUtils.singleTag(itemElement, "name", null);
        Scene description = loadScene(game, LoadUtils.singleChildWithName(itemElement, "description"));
        Map<String, List<Script>> scripts = loadScriptsWithTriggers(itemElement, "ItemTemplate(" + id + ")");
        List<ActionCustom.CustomActionHolder> customActions = loadCustomActions(itemElement, "action", "ItemTemplate(" + id + ")");
        int price = LoadUtils.attributeInt(itemElement, "price", 0);
        List<ItemComponentTemplate> components = new ArrayList<>();
        for (Element componentElement : LoadUtils.directChildrenWithName(itemElement, "component")) {
            ItemComponentTemplate componentTemplate = loadItemComponentTemplate(game, componentElement, id);
            components.add(componentTemplate);
        }
        return new ItemTemplate(game, id, name, description, scripts, components, customActions, price);
    }

    private static ItemComponentTemplate loadItemComponentTemplate(Game game, Element componentElement, String itemID) {
        if (componentElement == null) return null;
        String type = componentElement.getAttribute("type");
        boolean actionsRestricted = LoadUtils.attributeBool(componentElement, "restricted", false);
        switch (type) {
            case "ammo" -> {
                List<String> ammoWeaponEffects = LoadUtils.listOfTags(componentElement, "weaponEffect");
                boolean ammoIsReusable = LoadUtils.attributeBool(componentElement, "isReusable", false);
                return new ItemComponentTemplateAmmo(actionsRestricted, ammoWeaponEffects, ammoIsReusable);
            }
            case "armor" -> {
                Map<String, Integer> damageResistances = new HashMap<>();
                Map<String, Float> damageMults = new HashMap<>();
                for (Element damageElement : LoadUtils.directChildrenWithName(componentElement, "damage")) {
                    String damageType = LoadUtils.attribute(damageElement, "type", null);
                    Integer resistance = LoadUtils.attributeInt(damageElement, "resistance", null);
                    Float mult = LoadUtils.attributeFloat(damageElement, "mult", null);
                    if (resistance != null) {
                        damageResistances.put(damageType, resistance);
                    }
                    if (mult != null) {
                        damageMults.put(damageType, mult);
                    }
                }
                Set<String> coveredLimbs = LoadUtils.setOfTags(componentElement, "coveredLimb");
                boolean coversMainBody = LoadUtils.attributeBool(componentElement, "coversMainBody", false);
                return new ItemComponentTemplateArmor(actionsRestricted, damageResistances, damageMults, coveredLimbs, coversMainBody);
            }
            case "consumable" -> {
                String consumePrompt = LoadUtils.singleTag(componentElement, "consumePrompt", null);
                String consumePhrase = LoadUtils.singleTag(componentElement, "consumePhrase", null);
                List<String> consumableEffects = LoadUtils.listOfTags(componentElement, "effect");
                return new ItemComponentTemplateConsumable(actionsRestricted, consumePrompt, consumePhrase, consumableEffects);
            }
            case "effectible" -> {
                return new ItemComponentTemplateEffectible(actionsRestricted);
            }
            case "equippable" -> {
                Set<ItemComponentTemplateEquippable.EquippableSlotsData> equipSlots = new HashSet<>();
                for (Element slotGroupElement : LoadUtils.directChildrenWithName(componentElement, "slotGroup")) {
                    Set<String> slotGroup = LoadUtils.setOfTags(slotGroupElement, "slot");
                    Set<String> exposedComponents = LoadUtils.setOfTags(slotGroupElement, "exposedComponent");
                    List<String> equippedEffects = LoadUtils.listOfTags(componentElement, "effect");
                    List<ActionCustom.CustomActionHolder> equippedActions = loadCustomActions(componentElement, "equippedAction", "ItemComponent(" + itemID + ")");
                    equipSlots.add(new ItemComponentTemplateEquippable.EquippableSlotsData(slotGroup, exposedComponents, equippedEffects, equippedActions));
                }
                return new ItemComponentTemplateEquippable(actionsRestricted, equipSlots);
            }
            case "magazine" -> {
                Set<String> ammoTypes = LoadUtils.setOfTags(componentElement, "ammoType");
                int magazineSize = LoadUtils.singleTagInt(componentElement, "size", 1);
                int reloadActionPoints = LoadUtils.singleTagInt(componentElement, "reloadActionPoints", 1);
                return new ItemComponentTemplateMagazine(actionsRestricted, ammoTypes, magazineSize, reloadActionPoints);
            }
            case "mod" -> {
                String modSlot = LoadUtils.attribute(componentElement, "modSlot", null);
                List<String> effects = LoadUtils.listOfTags(componentElement, "effect");
                return new ItemComponentTemplateMod(actionsRestricted, modSlot, effects);
            }
            case "moddable" -> {
                Map<String, Integer> modSlots = new HashMap<>();
                for (Element modSlotElement : LoadUtils.directChildrenWithName(componentElement, "modSlot")) {
                    String slotName = LoadUtils.attribute(modSlotElement, "name", null);
                    int slotCount = LoadUtils.attributeInt(modSlotElement, "count", 1);
                    modSlots.put(slotName, slotCount);
                }
                return new ItemComponentTemplateModdable(actionsRestricted, modSlots);
            }
            case "weapon" -> {
                String weaponClass = LoadUtils.attribute(componentElement, "class", null);
                int weaponRate = LoadUtils.singleTagInt(componentElement, "rate", 1);
                Element damageElement = LoadUtils.singleChildWithName(componentElement, "damage");
                int weaponDamage = LoadUtils.attributeInt(damageElement, "base", 0);
                int critDamage = LoadUtils.attributeInt(damageElement, "crit", 0);
                float critChance = LoadUtils.attributeFloat(componentElement, "critChance", 0.0f);
                String weaponDamageType = LoadUtils.attribute(damageElement, "type", game.data().getConfig("defaultDamageType"));
                float weaponArmorMult = LoadUtils.singleTagFloat(componentElement, "armorMult", 1.0f);
                boolean weaponSilenced = LoadUtils.singleTagBoolean(componentElement, "silenced", false);
                Set<String> weaponTargetEffects = LoadUtils.setOfTags(componentElement, "targetEffect");
                return new ItemComponentTemplateWeapon(actionsRestricted, weaponClass, weaponDamage, weaponRate, critDamage, critChance, weaponArmorMult, weaponSilenced, weaponDamageType, weaponTargetEffects);
            }
            default -> {
                return null;
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
        Condition conditionAdd = loadCondition(LoadUtils.singleChildWithName(effectElement, "conditionAdd"), "Effect(" + ID + ") - add condition");
        Condition conditionRemove = loadCondition(LoadUtils.singleChildWithName(effectElement, "conditionRemove"), "Effect(" + ID + ") - remove condition");
        Condition conditionActive = loadCondition(LoadUtils.singleChildWithName(effectElement, "conditionActive"), "Effect(" + ID + ") - active condition");
        Script scriptAdd = loadScript(LoadUtils.singleChildWithName(effectElement, "scriptAdd"), "Effect(" + ID + ") - add script");
        Script scriptRemove = loadScript(LoadUtils.singleChildWithName(effectElement, "scriptRemove"), "Effect(" + ID + ") - remove script");
        Script scriptRound = loadScript(LoadUtils.singleChildWithName(effectElement, "scriptRound"), "Effect(" + ID + ") - round script");
        switch (effectType) {
            case "add" -> {
                String statMod = LoadUtils.attribute(effectElement, "stat", null);
                String statModValue = LoadUtils.attribute(effectElement, "amount", "0");
                Condition statCondition = loadCondition(LoadUtils.singleChildWithName(effectElement, "statCondition"), "Effect(" + ID + ") - stat condition");
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
                Condition statCondition = loadCondition(LoadUtils.singleChildWithName(effectElement, "statCondition"), "Effect(" + ID + ") - stat condition");
                return new EffectStatMult(game, ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound, statMult, statMultAmount, statCondition);
            }
            case "boolean" -> {
                String statBoolean = LoadUtils.attribute(effectElement, "stat", null);
                boolean statBooleanValue = LoadUtils.attributeBool(effectElement, "value", true);
                Condition statCondition = loadCondition(LoadUtils.singleChildWithName(effectElement, "statCondition"), "Effect(" + ID + ") - stat condition");
                return new EffectStatBoolean(game, ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound, statBoolean, statBooleanValue, statCondition);
            }
            case "string" -> {
                String statString = LoadUtils.attribute(effectElement, "stat", null);
                String statStringValue = LoadUtils.attribute(effectElement, "value", null);
                Condition statCondition = loadCondition(LoadUtils.singleChildWithName(effectElement, "statCondition"), "Effect(" + ID + ") - stat condition");
                return new EffectStatString(game, ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound, statString, statStringValue, statCondition);
            }
            case "stringSet" -> {
                String statStringSet = LoadUtils.attribute(effectElement, "stat", null);
                Set<String> stringSetValuesAdd = LoadUtils.setOfTags(effectElement, "add");
                Set<String> stringSetValuesRemove = LoadUtils.setOfTags(effectElement, "remove");
                Condition statCondition = loadCondition(LoadUtils.singleChildWithName(effectElement, "statCondition"), "Effect(" + ID + ") - stat condition");
                return new EffectStatStringSet(game, ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound, statStringSet, stringSetValuesAdd, stringSetValuesRemove, statCondition);
            }
            case "compound" -> {
                List<Effect> compoundEffects = loadEffects(game, effectElement);
                return new EffectCompound(game, ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound, compoundEffects);
            }
            case null, default -> { // "basic"
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
        float modChance = LoadUtils.attributeFloat(entryElement, "modChance", 1.0f);
        return new LootTableEntry(referenceID, isTable, chance, countMin, countMax, modTable, modChance);
    }

    private static Room loadRoom(Game game, Element roomElement) {
        if (roomElement == null) return null;
        String roomID = roomElement.getAttribute("id");
        Element roomNameElement = LoadUtils.singleChildWithName(roomElement, "name");
        String roomName = roomNameElement.getTextContent();
        boolean roomNameIsProper = LoadUtils.attributeBool(roomNameElement, "proper", false);
        Area.AreaNameType roomNameType = LoadUtils.attributeEnum(roomNameElement, "type", Area.AreaNameType.class, Area.AreaNameType.IN);
        Scene roomDescription = loadScene(game, LoadUtils.singleChildWithName(roomElement, "description"));
        String roomOwnerFaction = LoadUtils.attribute(roomElement, "faction", null);
        Area.RestrictionType restrictionType = LoadUtils.attributeEnum(roomElement, "restriction", Area.RestrictionType.class, Area.RestrictionType.PUBLIC);
        boolean allowAllies = LoadUtils.attributeBool(roomElement, "allowAllies", false);
        Map<String, List<Script>> roomScripts = loadScriptsWithTriggers(roomElement, "Room(" + roomID + ")");
        return new Room(game, roomID, roomName, roomNameType, roomNameIsProper, roomDescription, roomOwnerFaction, restrictionType, allowAllies, roomScripts);
    }

    private static Area loadArea(Game game, Element areaElement) {
        if (areaElement == null) return null;
        String areaID = LoadUtils.attribute(areaElement, "id", null);
        String roomID = LoadUtils.attribute(areaElement, "room", null);
        String landmarkID = LoadUtils.attribute(areaElement, "landmark", null);
        Element nameElement = LoadUtils.singleChildWithName(areaElement, "name");
        String name = (nameElement == null ? null : nameElement.getTextContent());
        Area.AreaNameType nameType = LoadUtils.attributeEnum(nameElement, "type", Area.AreaNameType.class, (landmarkID != null ? Area.AreaNameType.NEAR : Area.AreaNameType.IN));
        boolean nameIsPlural = LoadUtils.attributeBool(nameElement, "plural", false);
        Scene description = loadScene(game, LoadUtils.singleChildWithName(areaElement, "description"));
        String areaOwnerFaction = LoadUtils.attribute(areaElement, "faction", null);
        Area.RestrictionType restrictionType = LoadUtils.attributeEnum(areaElement, "restriction", Area.RestrictionType.class, Area.RestrictionType.PUBLIC);
        boolean allowAllies = LoadUtils.attributeBool(areaElement, "allowAllies", false);

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

        Map<String, List<Script>> areaScripts = loadScriptsWithTriggers(areaElement, "Area(" + areaID + ")");

        Area area = new Area(game, areaID, landmarkID, name, nameType, nameIsPlural, description, roomID, areaOwnerFaction, restrictionType, allowAllies, linkSet, areaScripts);

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
        String instanceID = LoadUtils.attribute(itemElement, "id", null);
        if (instanceID == null) {
            return ItemFactory.create(game, itemTemplate);
        } else {
            return ItemFactory.create(game, itemTemplate, instanceID);
        }
    }

    private static ObjectTemplate loadObjectTemplate(Game game, Element objectElement) throws GameDataException {
        String ID = LoadUtils.attribute(objectElement, "id", null);
        Element nameElement = LoadUtils.singleChildWithName(objectElement, "name");
        String name = nameElement != null ? nameElement.getTextContent() : null;
        boolean isProperName = LoadUtils.attributeBool(nameElement, "proper", false);
        Scene description = loadScene(game, LoadUtils.singleChildWithName(objectElement, "description"));
        int maxHP = LoadUtils.attributeInt(objectElement, "maxHP", 0);
        Map<String, Integer> damageResistances = new HashMap<>();
        Map<String, Float> damageMults = new HashMap<>();
        for (Element damageElement : LoadUtils.directChildrenWithName(objectElement, "damage")) {
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
        Map<String, List<Script>> scripts = loadScriptsWithTriggers(objectElement, "ObjectTemplate(" + ID + ")");
        List<ActionCustom.CustomActionHolder> customActions = loadCustomActions(objectElement, "action", "ObjectTemplate(" + ID + ")");
        List<ActionCustom.CustomActionHolder> networkActions = loadCustomActions(objectElement, "networkAction", "ObjectTemplate(" + ID + ")");
        List<ObjectComponentTemplate> components = new ArrayList<>();
        for (Element componentElement : LoadUtils.directChildrenWithName(objectElement, "component")) {
            ObjectComponentTemplate componentTemplate = loadObjectComponentTemplate(componentElement, ID);
            components.add(componentTemplate);
        }
        Map<String, Expression> localVarsDefault = new HashMap<>();
        for (Element varDefaultElement : LoadUtils.directChildrenWithName(objectElement, "localVar")) {
            String varName = LoadUtils.attribute(varDefaultElement, "name", null);
            Expression varExpression = loadExpressionOrAttribute(varDefaultElement, "ObjectTemplate(" + ID + ") - local var: " + varName);
            localVarsDefault.put(varName, varExpression);
        }
        return new ObjectTemplate(game, ID, name, isProperName, description, maxHP, damageResistances, damageMults, scripts, customActions, networkActions, components, localVarsDefault);
    }

    private static WorldObject loadObject(Game game, Element objectElement, Area area) {
        if (objectElement == null) return null;
        String template = LoadUtils.attribute(objectElement, "template", null);
        String id = LoadUtils.attribute(objectElement, "id", null);
        boolean startDisabled = LoadUtils.attributeBool(objectElement, "startDisabled", false);
        boolean startHidden = LoadUtils.attributeBool(objectElement, "startHidden", false);
        Map<String, Expression> localVarsDefault = new HashMap<>();
        for (Element varDefaultElement : LoadUtils.directChildrenWithName(objectElement, "localVar")) {
            String varName = LoadUtils.attribute(varDefaultElement, "name", null);
            Expression varExpression = loadExpressionOrAttribute(varDefaultElement, "Object(" + id + ") - local var: " + varName);
            localVarsDefault.put(varName, varExpression);
        }
        return new WorldObject(game, id, template, area, startDisabled, startHidden, localVarsDefault);
    }

    private static ObjectComponentTemplate loadObjectComponentTemplate(Element componentElement, String objectID) throws GameDataException {
        String type = LoadUtils.attribute(componentElement, "type", null);
        boolean startEnabled = LoadUtils.attributeBool(componentElement, "startEnabled", true);
        boolean actionsRestricted = LoadUtils.attributeBool(componentElement, "restricted", false);
        switch (type) {
            case "inventory" -> {
                LootTable lootTable = loadLootTable(LoadUtils.singleChildWithName(componentElement, "inventory"), true);
                String takePrompt = LoadUtils.singleTag(componentElement, "takePrompt", null);
                String takePhrase = LoadUtils.singleTag(componentElement, "takePhrase", null);
                String storePrompt = LoadUtils.singleTag(componentElement, "storePrompt", null);
                String storePhrase = LoadUtils.singleTag(componentElement, "storePhrase", null);
                boolean enableTake = LoadUtils.attributeBool(componentElement, "enableTake", true);
                boolean enableStore = LoadUtils.attributeBool(componentElement, "enableStore", true);
                List<ActionCustom.CustomActionHolder> perItemActions = loadCustomActions(componentElement, "itemAction", "ObjectComponentInventory(" + objectID + ")");
                return new ObjectComponentTemplateInventory(startEnabled, actionsRestricted, lootTable, takePrompt, takePhrase, storePrompt, storePhrase, enableTake, enableStore, perItemActions);
            }
            case "network" -> {
                return new ObjectComponentTemplateNetwork(startEnabled, actionsRestricted);
            }
            case "link" -> {
                Map<String, ObjectComponentTemplateLink.ObjectLinkData> linkData = new HashMap<>();
                for (Element linkDataElement : LoadUtils.directChildrenWithName(componentElement, "link")) {
                    String linkID = LoadUtils.attribute(linkDataElement, "id", null);
                    String moveAction = LoadUtils.attribute(linkDataElement, "moveAction", null);
                    Condition conditionVisible = loadCondition(LoadUtils.singleChildWithName(linkDataElement, "conditionVisible"), "ObjectComponentLink(" + objectID + ") - link visible condition");
                    boolean isVisible = LoadUtils.attributeBool(linkDataElement, "visible", false);
                    linkData.put(linkID, new ObjectComponentTemplateLink.ObjectLinkData(moveAction, conditionVisible, isVisible));
                }
                return new ObjectComponentTemplateLink(startEnabled, actionsRestricted, linkData);
            }
            case "usable" -> {
                Map<String, ObjectComponentTemplateUsable.UsableSlotData> usableSlotData = new HashMap<>();
                for (Element slotElement : LoadUtils.directChildrenWithName(componentElement, "slot")) {
                    String slotID = LoadUtils.attribute(slotElement, "id", null);
                    String startPhrase = LoadUtils.singleTag(slotElement, "startPhrase", null);
                    String endPhrase = LoadUtils.singleTag(slotElement, "endPhrase", null);
                    String startPrompt = LoadUtils.singleTag(slotElement, "startPrompt", null);
                    String endPrompt = LoadUtils.singleTag(slotElement, "endPrompt", null);
                    boolean userIsInCover = LoadUtils.attributeBool(slotElement, "cover", false);
                    boolean userIsHidden = LoadUtils.attributeBool(slotElement, "hidden", false);
                    boolean userCanSeeOtherAreas = LoadUtils.attributeBool(slotElement, "seeOtherAreas", true);
                    boolean userCanPerformLocalActions = LoadUtils.attributeBool(slotElement, "localActions", true);
                    boolean userCanPerformParentActions = LoadUtils.attributeBool(slotElement, "parentActions", true);
                    Set<String> componentsExposed = LoadUtils.setOfTags(slotElement, "exposedComponent");
                    List<ActionCustom.CustomActionHolder> usingActions = loadCustomActions(slotElement, "usingAction", "ObjectComponentUsable(" + objectID + ")");
                    usableSlotData.put(slotID, new ObjectComponentTemplateUsable.UsableSlotData(startPhrase, endPhrase, startPrompt, endPrompt, userIsInCover, userIsHidden, userCanSeeOtherAreas, userCanPerformLocalActions, userCanPerformParentActions, componentsExposed, usingActions));
                }
                return new ObjectComponentTemplateUsable(startEnabled, actionsRestricted, usableSlotData);
            }
            case "vehicle" -> {
                String vehicleType = LoadUtils.attribute(componentElement, "vehicleType", null);
                return new ObjectComponentTemplateVehicle(startEnabled, actionsRestricted, vehicleType);
            }
            default -> throw new GameDataException("ObjectComponentTemplate has invalid or missing type");
        }
    }

    private static ActionTemplate loadActionTemplate(Game game, Element actionElement) {
        String ID = LoadUtils.attribute(actionElement, "id", null);
        String prompt = LoadUtils.singleTag(actionElement, "prompt", null);
        Map<String, Script> parameters = new HashMap<>();
        for (Element parameterElement : LoadUtils.directChildrenWithName(actionElement, "parameter")) {
            String parameterName = LoadUtils.attribute(parameterElement, "name", null);
            Script parameterValue = loadExpressionScript(parameterElement, "ActionTemplate(" + ID + ") - parameter: " + parameterName);
            parameters.put(parameterName, parameterValue);
        }
        int actionPoints = LoadUtils.attributeInt(actionElement, "actionPoints", 0);
        List<ActionTemplate.ConditionWithMessage> selectConditions = new ArrayList<>();
        int conditionNum = 1;
        for (Element conditionElement : LoadUtils.directChildrenWithName(actionElement, "condition")) {
            Condition condition = loadCondition(LoadUtils.singleChildWithName(conditionElement, "script"), "ActionTemplate(" + ID + ") - condition " + conditionNum);
            String blockMessage = LoadUtils.singleTag(conditionElement, "blockMessage", null);
            selectConditions.add(new ActionTemplate.ConditionWithMessage(condition, blockMessage));
            conditionNum += 1;
        }
        Condition showCondition = loadCondition(LoadUtils.singleChildWithName(actionElement, "conditionShow"), "ActionTemplate(" + ID + ") - show condition");
        Script script = loadScript(LoadUtils.singleChildWithName(actionElement, "script"), "ActionTemplate(" + ID + ") - script");
        return new ActionTemplate(game, ID, prompt, parameters, actionPoints, selectConditions, showCondition, script);
    }

    private static List<ActionCustom.CustomActionHolder> loadCustomActions(Element parentElement, String name, String traceString) {
        List<ActionCustom.CustomActionHolder> customActions = new ArrayList<>();
        if (parentElement != null) {
            for (Element actionElement : LoadUtils.directChildrenWithName(parentElement, name)) {
                String action = LoadUtils.attribute(actionElement, "template", null);
                Map<String, Script> parameters = new HashMap<>();
                for (Element variableElement : LoadUtils.directChildrenWithName(actionElement, "parameter")) {
                    String parameterName = LoadUtils.attribute(variableElement, "name", null);
                    Script parameterValue = loadExpressionScript(variableElement, traceString + " - custom action parameter: " + parameterName);
                    parameters.put(parameterName, parameterValue);
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
        String nameDescriptor = LoadUtils.singleTag(actorElement, "descriptor", null);
        List<Behavior> behaviors = loadBehaviors(LoadUtils.singleChildWithName(actorElement, "behaviors"), ID);
        boolean startDead = LoadUtils.attributeBool(actorElement, "startDead", false);
        boolean startDisabled = LoadUtils.attributeBool(actorElement, "startDisabled", false);
        return ActorFactory.create(game, ID, nameDescriptor, area, template, behaviors, startDead, startDisabled);
    }

    private static List<Behavior> loadBehaviors(Element behaviorsElement, String actorID) {
        if (behaviorsElement == null) return new ArrayList<>();
        List<Behavior> behaviors = new ArrayList<>();
        for (Element behaviorElement : LoadUtils.directChildrenWithName(behaviorsElement, "behavior")) {
            Behavior behavior = loadBehavior(behaviorElement, actorID);
            behaviors.add(behavior);
        }
        return behaviors;
    }

    private static Behavior loadBehavior(Element behaviorElement, String actorID) {
        String type = LoadUtils.attribute(behaviorElement, "type", null);
        Condition condition = loadCondition(LoadUtils.singleChildWithName(behaviorElement, "condition"), "Behavior(" + actorID + ") - condition");
        Script startScript = loadScript(LoadUtils.singleChildWithName(behaviorElement, "scriptStart"), "Behavior(" + actorID + ") - start script");
        Script eachRoundScript = loadScript(LoadUtils.singleChildWithName(behaviorElement, "scriptEachRound"), "Behavior(" + actorID + ") - round script");
        int duration = LoadUtils.attributeInt(behaviorElement, "duration", 0);
        List<Idle> idles = new ArrayList<>();
        List<Element> idleElements = LoadUtils.directChildrenWithName(behaviorElement, "idle");
        for (Element idleElement : idleElements) {
            Idle idle = loadIdle(idleElement, actorID);
            idles.add(idle);
        }
        switch (type) {
            case "move" -> {
                String areaTarget = LoadUtils.attribute(behaviorElement, "area", null);
                return new BehaviorMove(condition, startScript, eachRoundScript, duration, idles, areaTarget);
            }
            case "use" -> {
                String objectTarget = LoadUtils.attribute(behaviorElement, "object", null);
                String slotTarget = LoadUtils.attribute(behaviorElement, "slot", null);
                return new BehaviorUse(condition, startScript, eachRoundScript, duration, idles, objectTarget, slotTarget);
            }
            case "guard" -> {
                String guardTarget = LoadUtils.attribute(behaviorElement, "object", null);
                return new BehaviorGuard(condition, startScript, eachRoundScript, duration, idles, guardTarget);
            }
            case "follow" -> {
                String actorTarget = LoadUtils.attribute(behaviorElement, "actor", null);
                return new BehaviorFollow(condition, startScript, eachRoundScript, duration, idles, actorTarget);
            }
            case "action" -> {
                String actionID = LoadUtils.attribute(behaviorElement, "action", null);
                Condition actionCondition = loadCondition(LoadUtils.singleChildWithName(behaviorElement, "actionCondition"), "Behavior(" + actorID + ") - action condition");
                return new BehaviorAction(condition, startScript, eachRoundScript, duration, idles, actionID, actionCondition);
            }
            case "procedure" -> {
                List<Behavior> procedureBehaviors = loadBehaviors(behaviorElement, actorID);
                boolean isCycle = LoadUtils.attributeBool(behaviorElement, "isCycle", false);
                return new BehaviorProcedure(condition, startScript, eachRoundScript, isCycle, procedureBehaviors);
            }
            default -> throw new IllegalArgumentException("Behavior type is not valid: " + type);
        }
    }

    private static Idle loadIdle(Element idleElement, String actorID) {
        Condition condition = loadCondition(LoadUtils.singleChildWithName(idleElement, "condition"), "Idle(" + actorID + ") - condition");
        String phrase = LoadUtils.singleTag(idleElement, "phrase", null);
        return new Idle(condition, phrase);
    }

    private static WeaponClass loadWeaponClass(Element weaponClassElement) {
        String ID = LoadUtils.attribute(weaponClassElement, "id", null);
        String name = LoadUtils.singleTag(weaponClassElement, "name", null);
        boolean isRanged = LoadUtils.attributeBool(weaponClassElement, "isRanged", false);
        boolean isLoud = LoadUtils.attributeBool(weaponClassElement, "isLoud", false);
        String skill = LoadUtils.attribute(weaponClassElement, "skill", null);
        Set<AreaLink.DistanceCategory> primaryRanges = LoadUtils.setOfEnumTags(weaponClassElement, "range", AreaLink.DistanceCategory.class);
        Set<String> ammoTypes = LoadUtils.setOfTags(weaponClassElement, "ammo");
        Set<String> attackTypes = LoadUtils.setOfTags(weaponClassElement, "attackType");
        return new WeaponClass(ID, name, isRanged, isLoud, skill, primaryRanges, ammoTypes, attackTypes);
    }

    private static WeaponAttackType loadWeaponAttackType(Element attackTypeElement) {
        String ID = LoadUtils.attribute(attackTypeElement, "id", null);
        WeaponAttackType.AttackCategory category = LoadUtils.attributeEnum(attackTypeElement, "category", WeaponAttackType.AttackCategory.class, WeaponAttackType.AttackCategory.SINGLE);
        String prompt = LoadUtils.singleTag(attackTypeElement, "prompt", null);
        String attackPhrase = LoadUtils.singleTag(attackTypeElement, "attackPhrase", null);
        String attackOverallPhrase = LoadUtils.singleTag(attackTypeElement, "attackOverallPhrase", null);
        String attackPhraseAudible = LoadUtils.singleTag(attackTypeElement, "attackPhraseAudible", null);
        String attackOverallPhraseAudible = LoadUtils.singleTag(attackTypeElement, "attackOverallPhraseAudible", null);
        int ammoConsumed = LoadUtils.attributeInt(attackTypeElement, "ammoConsumed", 1);
        int actionPoints = LoadUtils.attributeInt(attackTypeElement, "actionPoints", 1);
        WeaponAttackType.WeaponConsumeType weaponConsumeType = LoadUtils.attributeEnum(attackTypeElement, "weaponConsumeType", WeaponAttackType.WeaponConsumeType.class, WeaponAttackType.WeaponConsumeType.NONE);
        boolean useNonIdealRange = LoadUtils.attributeBool(attackTypeElement, "nonIdealRange", false);
        Set<AreaLink.DistanceCategory> rangeOverride = LoadUtils.setOfEnumTags(attackTypeElement, "range", AreaLink.DistanceCategory.class);
        Integer rateOverride = LoadUtils.attributeInt(attackTypeElement, "rate", null);
        Script damageOverride = loadExpressionScript(LoadUtils.singleChildWithName(attackTypeElement, "damage"), "WeaponAttackType(" + ID + ") - damage");
        float damageMult = LoadUtils.attributeFloat(attackTypeElement, "damageMult", 0.0f);
        String damageTypeOverride = LoadUtils.attribute(attackTypeElement, "damageType", null);
        Float armorMultOverride = LoadUtils.attributeFloat(attackTypeElement, "armorMult", null);
        List<String> targetEffects = LoadUtils.listOfTags(attackTypeElement, "targetEffect");
        boolean overrideTargetEffects = LoadUtils.attributeBool(attackTypeElement, "overrideEffects", false);
        Script hitChance = loadExpressionScript(LoadUtils.singleChildWithName(attackTypeElement, "hitChance"), "WeaponAttackType(" + ID + ") - hit chance");
        Script hitChanceOverall = loadExpressionScript(LoadUtils.singleChildWithName(attackTypeElement, "hitChanceOverall"), "WeaponAttackType(" + ID + ") - overall hit chance");
        float hitChanceMult = LoadUtils.attributeFloat(attackTypeElement, "hitChanceMult", 0.0f);
        Boolean isLoudOverride = LoadUtils.attributeBool(attackTypeElement, "isLoudOverride", null);
        return new WeaponAttackType(ID, category, prompt, attackPhrase, attackOverallPhrase, attackPhraseAudible, attackOverallPhraseAudible, ammoConsumed, actionPoints, weaponConsumeType, useNonIdealRange, rangeOverride, rateOverride, damageOverride, damageMult, damageTypeOverride, armorMultOverride, targetEffects, overrideTargetEffects, hitChance, hitChanceOverall, hitChanceMult, isLoudOverride);
    }

    private static NetworkNode loadNetworkNode(Game game, Element nodeElement) {
        String type = LoadUtils.attribute(nodeElement, "type", null);
        String ID = LoadUtils.attribute(nodeElement, "id", null);
        String templateID = LoadUtils.attribute(nodeElement, "template", null);
        String name = LoadUtils.singleTag(nodeElement, "name", null);
        switch (type) {
            case "data" -> {
                String dataSceneID = LoadUtils.attribute(nodeElement, "scene", null);
                return new NetworkNodeData(game, ID, templateID, name, dataSceneID);
            }
            case "control" -> {
                String controlObjectID = LoadUtils.attribute(nodeElement, "object", null);
                return new NetworkNodeControl(game, ID, templateID, name, controlObjectID);
            }
            case null, default -> {
                Set<NetworkNode> groupNodes = new HashSet<>();
                for (Element childNodeElement : LoadUtils.directChildrenWithName(nodeElement, "node")) {
                    NetworkNode childNode = loadNetworkNode(game, childNodeElement);
                    groupNodes.add(childNode);
                }
                return new NetworkNodeGroup(game, ID, templateID, name, groupNodes);
            }
        }
    }

    private static NetworkNodeTemplate loadNetworkNodeTemplate(Element templateElement) {
        String ID = LoadUtils.attribute(templateElement, "id", null);
        return new NetworkNodeTemplate(ID);
    }

}
