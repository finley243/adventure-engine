package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.action.ActionTemplate;
import com.github.finley243.adventureengine.combat.DamageType;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.item.LootTable;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.world.object.template.*;
import org.w3c.dom.Element;

import java.util.*;

public class ObjectTemplateLoader {

    private static final String NAME_OBJECT_TEMPLATE = "object";

    private final ScriptPipeline scriptPipeline;
    private final ScriptRuntime scriptRuntime;
    private final SceneLoader sceneLoader;
    private final LootTableLoader lootTableLoader;
    private final Registry<ActionTemplate> actionRegistry;
    private final Registry<LootTable> lootTableRegistry;
    private final Registry<DamageType> damageTypeRegistry;

    public ObjectTemplateLoader(ScriptPipeline scriptPipeline, ScriptRuntime scriptRuntime, SceneLoader sceneLoader, LootTableLoader lootTableLoader, Registry<ActionTemplate> actionRegistry, Registry<LootTable> lootTableRegistry, Registry<DamageType> damageTypeRegistry) {
        this.scriptPipeline = scriptPipeline;
        this.scriptRuntime = scriptRuntime;
        this.sceneLoader = sceneLoader;
        this.lootTableLoader = lootTableLoader;
        this.actionRegistry = actionRegistry;
        this.lootTableRegistry = lootTableRegistry;
        this.damageTypeRegistry = damageTypeRegistry;
    }

    public Map<String, ObjectTemplate> load(Element element) {
        return LoadUtils.loadAll(element, NAME_OBJECT_TEMPLATE, this::parseObjectTemplate, ObjectTemplate::getID);
    }

    private ObjectTemplate parseObjectTemplate(Element element) {
        String ID = LoadUtils.attribute(element, "id", null);
        Element nameElement = LoadUtils.singleChildWithName(element, "name");
        String name = nameElement != null ? nameElement.getTextContent() : null;
        boolean isProperName = LoadUtils.attributeBool(nameElement, "proper", false);
        Scene description = sceneLoader.parseScene(LoadUtils.singleChildWithName(element, "description"));
        int maxHP = LoadUtils.attributeInt(element, "maxHP", 0);
        Map<DamageType, Integer> damageResistances = new HashMap<>();
        Map<DamageType, Float> damageMults = new HashMap<>();
        for (Element damageElement : LoadUtils.directChildrenWithName(element, "damage")) {
            String damageTypeID = LoadUtils.attribute(damageElement, "type", null);
            DamageType damageType = damageTypeRegistry.getFromID(damageTypeID);
            if (damageType == null) throw new GameDataException("ObjectTemplate has invalid damage type: " + damageTypeID);
            Integer damageResistance = LoadUtils.attributeInt(damageElement, "resistance", null);
            Float damageMult = LoadUtils.attributeFloat(damageElement, "mult", null);
            if (damageResistance != null) {
                damageResistances.put(damageType, damageResistance);
            }
            if (damageMult != null) {
                damageMults.put(damageType, damageMult);
            }
        }
        Map<String, List<Script>> scripts = LoadUtils.loadScriptsWithTriggers(element, scriptPipeline, "ObjectTemplate(" + ID + ")");
        List<ActionCustom.CustomActionHolder> customActions = LoadUtils.loadCustomActions(element, "action", scriptPipeline, actionRegistry, "ObjectTemplate(" + ID + ")");
        List<ActionCustom.CustomActionHolder> networkActions = LoadUtils.loadCustomActions(element, "networkAction", scriptPipeline, actionRegistry, "ObjectTemplate(" + ID + ")");
        List<ObjectComponentTemplate> components = new ArrayList<>();
        for (Element componentElement : LoadUtils.directChildrenWithName(element, "component")) {
            ObjectComponentTemplate componentTemplate = parseObjectComponentTemplate(componentElement, ID);
            components.add(componentTemplate);
        }
        Map<String, Expression> localVarsDefault = new HashMap<>();
        for (Element varDefaultElement : LoadUtils.directChildrenWithName(element, "localVar")) {
            String varName = LoadUtils.attribute(varDefaultElement, "name", null);
            Expression varExpression = LoadUtils.loadScriptLiteral(varDefaultElement, scriptPipeline, "ObjectTemplate(" + ID + ") - local var: " + varName);
            localVarsDefault.put(varName, varExpression);
        }
        return new ObjectTemplate(ID, name, isProperName, description, maxHP, damageResistances, damageMults, scripts, customActions, networkActions, components, localVarsDefault);
    }

    private ObjectComponentTemplate parseObjectComponentTemplate(Element element, String objectID) {
        String type = LoadUtils.attribute(element, "type", null);
        boolean startEnabled = LoadUtils.attributeBool(element, "startEnabled", true);
        boolean actionsRestricted = LoadUtils.attributeBool(element, "restricted", false);
        switch (type) {
            case "inventory" -> {
                LootTable lootTable = lootTableLoader.parseLootTable(LoadUtils.singleChildWithName(element, "inventory"), lootTableRegistry::getFromID, true);
                String takePrompt = LoadUtils.singleTag(element, "takePrompt", null);
                String takePhrase = LoadUtils.singleTag(element, "takePhrase", null);
                String storePrompt = LoadUtils.singleTag(element, "storePrompt", null);
                String storePhrase = LoadUtils.singleTag(element, "storePhrase", null);
                boolean enableTake = LoadUtils.attributeBool(element, "enableTake", true);
                boolean enableStore = LoadUtils.attributeBool(element, "enableStore", true);
                List<ActionCustom.CustomActionHolder> perItemActions = LoadUtils.loadCustomActions(element, "itemAction", scriptPipeline, actionRegistry, "ObjectComponentInventory(" + objectID + ")");
                return new InventoryObjectComponentTemplate(startEnabled, actionsRestricted, lootTable, takePrompt, takePhrase, storePrompt, storePhrase, enableTake, enableStore, perItemActions);
            }
            case "network" -> {
                return new NetworkObjectComponentTemplate(startEnabled, actionsRestricted);
            }
            case "link" -> {
                Map<String, LinkObjectComponentTemplate.ObjectLinkData> linkData = new HashMap<>();
                for (Element linkDataElement : LoadUtils.directChildrenWithName(element, "link")) {
                    String linkID = LoadUtils.attribute(linkDataElement, "id", null);
                    String moveActionID = LoadUtils.attribute(linkDataElement, "moveAction", null);
                    ActionTemplate moveAction = actionRegistry.getFromID(moveActionID);
                    if (moveAction == null) throw new GameDataException("ObjectComponentTemplate has invalid move action reference: " + moveActionID);
                    Condition conditionVisible = LoadUtils.loadCondition(LoadUtils.singleChildWithName(linkDataElement, "conditionVisible"), scriptPipeline, "ObjectComponentLink(" + objectID + ") - link visible condition", scriptRuntime);
                    boolean isVisible = LoadUtils.attributeBool(linkDataElement, "visible", false);
                    linkData.put(linkID, new LinkObjectComponentTemplate.ObjectLinkData(moveAction, conditionVisible, isVisible));
                }
                return new LinkObjectComponentTemplate(startEnabled, actionsRestricted, linkData);
            }
            case "usable" -> {
                Map<String, UsableObjectComponentTemplate.UsableSlotData> usableSlotData = new HashMap<>();
                for (Element slotElement : LoadUtils.directChildrenWithName(element, "slot")) {
                    String slotID = LoadUtils.attribute(slotElement, "id", null);
                    String startPhrase = LoadUtils.singleTag(slotElement, "startPhrase", null);
                    String endPhrase = LoadUtils.singleTag(slotElement, "endPhrase", null);
                    String endDeathPhrase = LoadUtils.singleTag(slotElement, "endDeathPhrase", null);
                    String startPrompt = LoadUtils.singleTag(slotElement, "startPrompt", null);
                    String endPrompt = LoadUtils.singleTag(slotElement, "endPrompt", null);
                    boolean userIsInCover = LoadUtils.attributeBool(slotElement, "cover", false);
                    boolean userIsHidden = LoadUtils.attributeBool(slotElement, "hidden", false);
                    boolean userCanSeeOtherAreas = LoadUtils.attributeBool(slotElement, "seeOtherAreas", true);
                    boolean userCanPerformLocalActions = LoadUtils.attributeBool(slotElement, "localActions", true);
                    boolean userCanPerformParentActions = LoadUtils.attributeBool(slotElement, "parentActions", true);
                    boolean shouldRemoveUserOnDeath = LoadUtils.attributeBool(slotElement, "removeUserOnDeath", false);
                    Set<String> componentsExposed = LoadUtils.setOfTags(slotElement, "exposedComponent");
                    List<ActionCustom.CustomActionHolder> usingActions = LoadUtils.loadCustomActions(slotElement, "usingAction", scriptPipeline, actionRegistry, "ObjectComponentUsable(" + objectID + ")");
                    usableSlotData.put(slotID, new UsableObjectComponentTemplate.UsableSlotData(startPhrase, endPhrase, endDeathPhrase, startPrompt, endPrompt, userIsInCover, userIsHidden, userCanSeeOtherAreas, userCanPerformLocalActions, userCanPerformParentActions, shouldRemoveUserOnDeath, componentsExposed, usingActions));
                }
                return new UsableObjectComponentTemplate(startEnabled, actionsRestricted, usableSlotData);
            }
            case "vehicle" -> {
                String vehicleType = LoadUtils.attribute(element, "vehicleType", null);
                return new VehicleObjectComponentTemplate(startEnabled, actionsRestricted, vehicleType);
            }
            default -> throw new GameDataException("ObjectComponentTemplate has invalid type: " + type);
        }
    }

}
