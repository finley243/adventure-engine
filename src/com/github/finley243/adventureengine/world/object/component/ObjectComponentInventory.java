package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.item.LootTable;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateInventory;

import java.util.ArrayList;
import java.util.List;

public class ObjectComponentInventory extends ObjectComponent {

    private final ObjectComponentTemplateInventory template;
    // TODO - Add lock functionality
    private final Inventory inventory;

    public ObjectComponentInventory(String ID, WorldObject object, ObjectComponentTemplateInventory template) {
        super(ID, object, template.startEnabled());
        this.inventory = new Inventory(object.game(), null);
        this.template = template;
    }

    @Override
    public List<Action> getActions(Actor subject) {
        if (isEnabled()) {
            return new ArrayList<>(inventory.getExternalActions(getObject(), template.getName(), subject, template.isExposed()));
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void onNewGameInit() {
        if (template.getLootTable() != null) {
            inventory.addItems(template.getLootTable().generateItems(getObject().game()));
        }
    }

}
