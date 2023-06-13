package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.item.LootTable;

import java.util.List;

public class ObjectComponentTemplateInventory extends ObjectComponentTemplate {

    private final LootTable lootTable;
    private final boolean isExposed;
    private final List<ActionCustom.CustomActionHolder> perItemActions;

    public ObjectComponentTemplateInventory(Game game, String ID, boolean startEnabled, boolean actionsRestricted, String name, LootTable lootTable, boolean isExposed, List<ActionCustom.CustomActionHolder> perItemActions) {
        super(game, ID, startEnabled, actionsRestricted, name);
        this.lootTable = lootTable;
        this.isExposed = isExposed;
        this.perItemActions = perItemActions;
    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public boolean isExposed() {
        return isExposed;
    }

    public List<ActionCustom.CustomActionHolder> getPerItemActions() {
        return perItemActions;
    }

}
