package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.action.ActionTemplate;
import com.github.finley243.adventureengine.combat.DamageType;
import com.github.finley243.adventureengine.combat.WeaponClass;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.gamedata.ConfigHandler;
import com.github.finley243.adventureengine.gamedata.ConfigOption;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.item.template.*;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;
import org.w3c.dom.Element;

import java.util.*;

public class ItemTemplateLoader {

    private static final String NAME_ITEM = "item";

    private final ConfigHandler configHandler;
    private final ScriptParser scriptParser;
    private final SceneLoader sceneLoader;
    private final Registry<ActionTemplate> actionRegistry;
    private final Registry<Effect> effectRegistry;
    private final Registry<WeaponClass> weaponClassRegistry;
    private final Registry<DamageType> damageTypeRegistry;

    public ItemTemplateLoader(ConfigHandler configHandler, ScriptParser scriptParser, SceneLoader sceneLoader, Registry<ActionTemplate> actionRegistry, Registry<Effect> effectRegistry, Registry<WeaponClass> weaponClassRegistry, Registry<DamageType> damageTypeRegistry) {
        this.configHandler = configHandler;
        this.scriptParser = scriptParser;
        this.sceneLoader = sceneLoader;
        this.actionRegistry = actionRegistry;
        this.effectRegistry = effectRegistry;
        this.weaponClassRegistry = weaponClassRegistry;
        this.damageTypeRegistry = damageTypeRegistry;
    }

    public Map<String, ItemTemplate> load(Element element) {
        return LoadUtils.loadAll(element, NAME_ITEM, this::parseItemTemplate, ItemTemplate::getID);
    }

    private ItemTemplate parseItemTemplate(Element element) {
        if (element == null) return null;
        String id = element.getAttribute("id");
        String name = LoadUtils.singleTag(element, "name", null);
        Scene description = sceneLoader.parseScene(LoadUtils.singleChildWithName(element, "description"));
        Map<String, List<Script>> scripts = LoadUtils.loadScriptsWithTriggers(element, scriptParser, "ItemTemplate(" + id + ")");
        List<ActionCustom.CustomActionHolder> customActions = LoadUtils.loadCustomActions(element, "action", scriptParser, actionRegistry, "ItemTemplate(" + id + ")");
        int price = LoadUtils.attributeInt(element, "price", 0);
        List<ItemComponentTemplate> components = new ArrayList<>();
        for (Element componentElement : LoadUtils.directChildrenWithName(element, "component")) {
            ItemComponentTemplate componentTemplate = parseItemComponentTemplate(componentElement, id);
            components.add(componentTemplate);
        }
        return new ItemTemplate(id, name, description, scripts, components, customActions, price);
    }

    private ItemComponentTemplate parseItemComponentTemplate(Element componentElement, String itemID) {
        if (componentElement == null) return null;
        String type = componentElement.getAttribute("type");
        boolean actionsRestricted = LoadUtils.attributeBool(componentElement, "restricted", false);
        switch (type) {
            case "ammo" -> {
                List<String> ammoWeaponEffectIDs = LoadUtils.listOfTags(componentElement, "weaponEffect");
                List<Effect> ammoWeaponEffects = new ArrayList<>();
                for (String effectID : ammoWeaponEffectIDs) {
                    Effect effect = effectRegistry.getFromID(effectID);
                    if (effect == null) throw new GameDataException("ItemComponentTemplateAmmo has invalid weapon effect: " + effectID);
                    ammoWeaponEffects.add(effect);
                }
                boolean ammoIsReusable = LoadUtils.attributeBool(componentElement, "isReusable", false);
                return new AmmoItemComponentTemplate(actionsRestricted, ammoWeaponEffects, ammoIsReusable);
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
                return new ArmorItemComponentTemplate(actionsRestricted, damageResistances, damageMults, coveredLimbs, coversMainBody);
            }
            case "consumable" -> {
                String consumePrompt = LoadUtils.singleTag(componentElement, "consumePrompt", null);
                String consumePhrase = LoadUtils.singleTag(componentElement, "consumePhrase", null);
                List<String> consumableEffectIDs = LoadUtils.listOfTags(componentElement, "effect");
                List<Effect> consumableEffects = new ArrayList<>();
                for (String effectID : consumableEffectIDs) {
                    Effect effect = effectRegistry.getFromID(effectID);
                    if (effect == null) throw new GameDataException("ItemComponentTemplateConsumable has invalid consumable effect: " + effectID);
                    consumableEffects.add(effect);
                }
                return new ConsumableItemComponentTemplate(actionsRestricted, consumePrompt, consumePhrase, consumableEffects);
            }
            case "effectible" -> {
                return new EffectableItemComponentTemplate(actionsRestricted);
            }
            case "equippable" -> {
                Set<EquippableItemComponentTemplate.EquippableSlotsData> equipSlots = new HashSet<>();
                for (Element slotGroupElement : LoadUtils.directChildrenWithName(componentElement, "slotGroup")) {
                    Set<String> slotGroup = LoadUtils.setOfTags(slotGroupElement, "slot");
                    Set<String> exposedComponents = LoadUtils.setOfTags(slotGroupElement, "exposedComponent");
                    List<String> equippedEffectIDs = LoadUtils.listOfTags(componentElement, "effect");
                    List<Effect> equippedEffects = new ArrayList<>();
                    for (String effectID : equippedEffectIDs) {
                        Effect effect = effectRegistry.getFromID(effectID);
                        if (effect == null) throw new GameDataException("ItemComponentTemplateEquippable has invalid equipped effect");
                        equippedEffects.add(effect);
                    }
                    List<ActionCustom.CustomActionHolder> equippedActions = LoadUtils.loadCustomActions(componentElement, "equippedAction", scriptParser, actionRegistry, "ItemComponent(" + itemID + ")");
                    equipSlots.add(new EquippableItemComponentTemplate.EquippableSlotsData(slotGroup, exposedComponents, equippedEffects, equippedActions));
                }
                return new EquippableItemComponentTemplate(actionsRestricted, equipSlots);
            }
            case "magazine" -> {
                Set<String> ammoTypeIDs = LoadUtils.setOfTags(componentElement, "ammoType");
                int magazineSize = LoadUtils.singleTagInt(componentElement, "size", 1);
                int reloadActionPoints = LoadUtils.singleTagInt(componentElement, "reloadActionPoints", 1);
                return new MagazineItemComponentTemplate(actionsRestricted, ammoTypeIDs, magazineSize, reloadActionPoints);
            }
            case "mod" -> {
                String modSlot = LoadUtils.attribute(componentElement, "modSlot", null);
                List<String> effectIDs = LoadUtils.listOfTags(componentElement, "effect");
                List<Effect> effects = new ArrayList<>();
                for (String effectID : effectIDs) {
                    Effect effect = effectRegistry.getFromID(effectID);
                    if (effect == null) throw new GameDataException("ItemComponentTemplateMod has invalid effect");
                    effects.add(effect);
                }
                return new ModItemComponentTemplate(actionsRestricted, modSlot, effects);
            }
            case "moddable" -> {
                Map<String, Integer> modSlots = new HashMap<>();
                for (Element modSlotElement : LoadUtils.directChildrenWithName(componentElement, "modSlot")) {
                    String slotName = LoadUtils.attribute(modSlotElement, "name", null);
                    int slotCount = LoadUtils.attributeInt(modSlotElement, "count", 1);
                    modSlots.put(slotName, slotCount);
                }
                return new ModdableItemComponentTemplate(actionsRestricted, modSlots);
            }
            case "weapon" -> {
                String weaponClassID = LoadUtils.attribute(componentElement, "class", null);
                WeaponClass weaponClass = weaponClassRegistry.getFromID(weaponClassID);
                if (weaponClass == null) throw new GameDataException("ItemComponentTemplateWeapon has invalid weapon class");
                int weaponRate = LoadUtils.singleTagInt(componentElement, "rate", 1);
                Element damageElement = LoadUtils.singleChildWithName(componentElement, "damage");
                int weaponDamage = LoadUtils.attributeInt(damageElement, "base", 0);
                int critDamage = LoadUtils.attributeInt(damageElement, "crit", 0);
                float critChance = LoadUtils.attributeFloat(componentElement, "critChance", 0.0f);
                String weaponDamageTypeID = LoadUtils.attribute(damageElement, "type", configHandler.get(ConfigOption.DEFAULT_DAMAGE_TYPE));
                DamageType weaponDamageType = damageTypeRegistry.getFromID(weaponDamageTypeID);
                if (weaponDamageType == null) throw new GameDataException("ItemComponentTemplateWeapon has invalid damage type");
                float weaponArmorMult = LoadUtils.singleTagFloat(componentElement, "armorMult", 1.0f);
                boolean weaponSilenced = LoadUtils.singleTagBoolean(componentElement, "silenced", false);
                Set<String> weaponTargetEffectIDs = LoadUtils.setOfTags(componentElement, "targetEffect");
                Set<Effect> weaponTargetEffects = new HashSet<>();
                for (String effectID : weaponTargetEffectIDs) {
                    Effect effect = effectRegistry.getFromID(effectID);
                    if (effect == null) throw new GameDataException("ItemComponentTemplateWeapon has invalid target effect");
                    weaponTargetEffects.add(effect);
                }
                return new WeaponItemComponentTemplate(actionsRestricted, weaponClass, weaponDamage, weaponRate, critDamage, critChance, weaponArmorMult, weaponSilenced, weaponDamageType, weaponTargetEffects);
            }
            default -> throw new GameDataException("ItemComponentTemplate has invalid or missing type");
        }
    }

}
