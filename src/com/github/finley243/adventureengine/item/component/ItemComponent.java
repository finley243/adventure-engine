package com.github.finley243.adventureengine.item.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.ItemComponentTemplate;

import java.util.ArrayList;
import java.util.List;

public abstract class ItemComponent {

    private final Item item;
    private final ItemComponentTemplate template;

    public ItemComponent(Item item, ItemComponentTemplate template) {
        this.item = item;
        this.template = template;
    }

    public void onInit() {

    }

    public List<Action> inventoryActions(Actor subject) {
        return new ArrayList<>();
    }

}
