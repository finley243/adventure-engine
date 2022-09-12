package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.item.LootTable;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.ArrayList;
import java.util.List;

public class ObjectComponentInventory implements ObjectComponent {

    private boolean isEnabled;
    private final WorldObject object;
    private final Inventory inventory;
    private final String name;
    private final LootTable lootTable;
    private final boolean isOpen;

    public ObjectComponentInventory(WorldObject object, String name, LootTable lootTable, boolean isOpen) {
        this.object = object;
        this.inventory = new Inventory(object.game(), null);
        this.name = name;
        this.lootTable = lootTable;
        this.isOpen = isOpen;
    }

    @Override
    public List<Action> getActions(Actor subject) {
        if (isEnabled) {
            return new ArrayList<>(inventory.getExternalActions(object, subject, isOpen));
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    @Override
    public void onGameInit() {
        inventory.addItems(lootTable.generateItems(object.game()));
    }

}
