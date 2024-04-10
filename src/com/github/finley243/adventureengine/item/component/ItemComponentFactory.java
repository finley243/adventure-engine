package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.*;

public class ItemComponentFactory {

    public static ItemComponent create(ItemComponentTemplate template, Item item) {
        return switch (template) {
            case ItemComponentTemplateConsumable typeTemplate -> new ItemComponentConsumable(item, typeTemplate);
            case ItemComponentTemplateEquippable typeTemplate -> new ItemComponentEquippable(item, typeTemplate);
            case ItemComponentTemplateMod typeTemplate -> new ItemComponentMod(item, typeTemplate);
            case ItemComponentTemplateWeapon typeTemplate -> new ItemComponentWeapon(item, typeTemplate);
            default -> null;
        };
    }

    public static Class<? extends ItemComponent> getClassFromName(String name) {
        return switch (name) {
            case "consumable" -> ItemComponentConsumable.class;
            case "equippable" -> ItemComponentEquippable.class;
            case "mod" -> ItemComponentMod.class;
            case "weapon" -> ItemComponentWeapon.class;
            case null, default -> null;
        };
    }

}
