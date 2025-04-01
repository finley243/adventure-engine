package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.*;

public class ItemComponentFactory {

    public static ItemComponent create(ItemComponentTemplate template, Item item) {
        return switch (template) {
            case ItemComponentTemplateAmmo typeTemplate -> new ItemComponentAmmo(item, typeTemplate);
            case ItemComponentTemplateArmor typeTemplate -> new ItemComponentArmor(item, typeTemplate);
            case ItemComponentTemplateConsumable typeTemplate -> new ItemComponentConsumable(item, typeTemplate);
            case ItemComponentTemplateEffectible typeTemplate -> new ItemComponentEffectible(item, typeTemplate);
            case ItemComponentTemplateEquippable typeTemplate -> new ItemComponentEquippable(item, typeTemplate);
            case ItemComponentTemplateMagazine typeTemplate -> new ItemComponentMagazine(item, typeTemplate);
            case ItemComponentTemplateMod typeTemplate -> new ItemComponentMod(item, typeTemplate);
            case ItemComponentTemplateModdable typeTemplate -> new ItemComponentModdable(item, typeTemplate);
            case ItemComponentTemplateWeapon typeTemplate -> new ItemComponentWeapon(item, typeTemplate);
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
