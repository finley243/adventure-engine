package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.item.LootTable;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.ArrayList;
import java.util.List;

public class ObjectComponentInventory extends ObjectComponent {

    // TODO - Add lock functionality
    private final Inventory inventory;
    private final String name;
    private final LootTable lootTable;
    private final boolean isExposed;

    public ObjectComponentInventory(String ID, WorldObject object, boolean startEnabled, String name, LootTable lootTable, boolean isExposed) {
        super(ID, object, startEnabled);
        this.inventory = new Inventory(object.game(), null);
        this.name = name;
        this.lootTable = lootTable;
        this.isExposed = isExposed;
    }

    @Override
    public List<Action> getActions(Actor subject) {
        if (isEnabled()) {
            return new ArrayList<>(inventory.getExternalActions(getObject(), name, subject, isExposed));
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void newGameInit() {
        if (lootTable != null) {
            inventory.addItems(lootTable.generateItems(getObject().game()));
        }
    }

}
