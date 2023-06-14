package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.item.LootTable;

import java.util.List;

public class ObjectComponentTemplateInventory extends ObjectComponentTemplate {

    private final LootTable lootTable;
    private final boolean isExposed;
    private final boolean enableTake;
    private final boolean enableStore;
    private final List<ActionCustom.CustomActionHolder> perItemActions;

    public ObjectComponentTemplateInventory(Game game, String ID, boolean startEnabled, boolean actionsRestricted, String name, LootTable lootTable, boolean isExposed, boolean enableTake, boolean enableStore, List<ActionCustom.CustomActionHolder> perItemActions) {
        super(game, ID, startEnabled, actionsRestricted, name);
        this.lootTable = lootTable;
        this.isExposed = isExposed;
        this.enableTake = enableTake;
        this.enableStore = enableStore;
        this.perItemActions = perItemActions;
    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public boolean isExposed() {
        return isExposed;
    }

    public boolean enableTake() {
        return enableTake;
    }

    public boolean enableStore() {
        return enableStore;
    }

    public List<ActionCustom.CustomActionHolder> getPerItemActions() {
        return perItemActions;
    }

}
