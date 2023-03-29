package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.item.LootTable;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateInventory;

import java.util.ArrayList;
import java.util.List;

public class ObjectComponentInventory extends ObjectComponent {

    private final String templateID;
    private final Inventory inventory;

    public ObjectComponentInventory(String ID, WorldObject object, String templateID) {
        super(ID, object);
        this.inventory = new Inventory(object.game(), null);
        this.templateID = templateID;
    }

    @Override
    public ObjectComponentTemplate getTemplate() {
        return getTemplateInventory();
    }

    public ObjectComponentTemplateInventory getTemplateInventory() {
        return (ObjectComponentTemplateInventory) getObject().game().data().getObjectComponentTemplate(templateID);
    }

    @Override
    public List<Action> getActions(Actor subject) {
        if (isEnabled()) {
            return new ArrayList<>(inventory.getExternalActions(getObject(), getTemplateInventory().getName(), subject, getTemplateInventory().isExposed()));
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void onNewGameInit() {
        super.onNewGameInit();
        if (getTemplateInventory().getLootTable() != null) {
            inventory.addItems(getTemplateInventory().getLootTable().generateItems(getObject().game()));
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

}
