package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionThrow;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.List;

public class ItemDistraction extends Item {

    public ItemDistraction(String name) {
        super(name);
    }

    public String getID() {
        return "TEMP";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public boolean equalsInventory(Item other) {
        return false;
    }

    @Override
    public List<Action> inventoryActions(Actor subject) {
        List<Action> actions = super.inventoryActions(subject);
        for(Area area : subject.getArea().getRoom().getAreas()) {
            actions.add(new ActionThrow(area, this));
        }
        return actions;
    }
}
