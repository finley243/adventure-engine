package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.item.LootTable;

public class ObjectComponentTemplateInventory extends ObjectComponentTemplate {

    private final String name;
    private final LootTable lootTable;
    private final boolean isExposed;

    public ObjectComponentTemplateInventory(Game game, String ID, boolean startEnabled, String name, LootTable lootTable, boolean isExposed) {
        super(game, ID, startEnabled);
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
