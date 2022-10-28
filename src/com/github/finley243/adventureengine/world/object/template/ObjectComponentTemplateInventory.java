package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.item.LootTable;

public class ObjectComponentTemplateInventory extends ObjectComponentTemplate {

    private final String name;
    private final LootTable lootTable;
    private final boolean isExposed;

    public ObjectComponentTemplateInventory(String name, LootTable lootTable, boolean isExposed) {
        this.name = name;
        this.lootTable = lootTable;
        this.isExposed = isExposed;
    }

    public String getName() {
        return name;
    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public boolean isExposed() {
        return isExposed;
    }

}
