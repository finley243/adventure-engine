package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.combat.WeaponAttackType;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.*;
import com.github.finley243.adventureengine.script.ScriptRuntime;

public class ItemComponentFactory {

    private final ScriptRuntime scriptRuntime;
    private final Registry<WeaponAttackType> attackTypeRegistry;
    private final Registry<Effect> effectRegistry;

    public ItemComponentFactory(ScriptRuntime scriptRuntime, Registry<WeaponAttackType> attackTypeRegistry, Registry<Effect> effectRegistry) {
        this.scriptRuntime = scriptRuntime;
        this.attackTypeRegistry = attackTypeRegistry;
        this.effectRegistry = effectRegistry;
    }

    public ItemComponent create(ItemComponentTemplate template, Item item) {
        return switch (template) {
            case ItemComponentTemplateAmmo typeTemplate -> new ItemComponentAmmo(item, typeTemplate);
            case ItemComponentTemplateArmor typeTemplate -> new ItemComponentArmor(item, typeTemplate);
            case ItemComponentTemplateConsumable typeTemplate -> new ItemComponentConsumable(item, typeTemplate);
            case ItemComponentTemplateEffectible typeTemplate -> new ItemComponentEffectible(item, typeTemplate, scriptRuntime, Context.builder().parentItem(item).build());
            case ItemComponentTemplateEquippable typeTemplate -> new ItemComponentEquippable(item, typeTemplate);
            case ItemComponentTemplateMagazine typeTemplate -> new ItemComponentMagazine(item, typeTemplate);
            case ItemComponentTemplateMod typeTemplate -> new ItemComponentMod(item, typeTemplate);
            case ItemComponentTemplateModdable typeTemplate -> new ItemComponentModdable(item, typeTemplate);
            case ItemComponentTemplateWeapon typeTemplate -> new ItemComponentWeapon(item, typeTemplate, attackTypeRegistry, effectRegistry);
            default -> null;
        };
    }

    public static Class<? extends ItemComponent> getClassFromName(String name) {
        return switch (name) {
            case "ammo" -> ItemComponentAmmo.class;
            case "armor" -> ItemComponentArmor.class;
            case "consumable" -> ItemComponentConsumable.class;
            case "effectible" -> ItemComponentEffectible.class;
            case "equippable" -> ItemComponentEquippable.class;
            case "magazine" -> ItemComponentMagazine.class;
            case "mod" -> ItemComponentMod.class;
            case "moddable" -> ItemComponentModdable.class;
            case "weapon" -> ItemComponentWeapon.class;
            case null, default -> null;
        };
    }

}
