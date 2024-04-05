package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplate;

public class ItemComponentFactory {

    public static ItemComponent create(ItemComponentTemplate template, Item item) {
        /*return switch (template) {
            default -> null;
        };*/
        return null;
    }

    public static Class<? extends ItemComponent> getClassFromName(String name) {
        /*return switch (name) {
            case null, default -> null;
        };*/
        return null;
    }

}
