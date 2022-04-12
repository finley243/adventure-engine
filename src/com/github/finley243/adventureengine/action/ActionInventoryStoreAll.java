package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.AudioVisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.MultiNoun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ActionInventoryStoreAll extends Action {

    private final Noun owner;
    private final Inventory inventory;
    private final List<Item> items;

    public ActionInventoryStoreAll(Noun owner, Inventory inventory, List<Item> items) {
        this.owner = owner;
        this.inventory = inventory;
        this.items = new ArrayList<>(items);
    }

    @Override
    public void choose(Actor subject) {
        for(Item item : items) {
            subject.inventory().removeItem(item);
            inventory.addItem(item);
        }
        Context context = new Context(new NounMapper().put("actor", subject).put("item", new MultiNoun(items)).put("inventory", owner).build());
        subject.game().eventBus().post(new AudioVisualEvent(subject.getArea(), Phrases.get("storeIn"), context, this, subject));
    }

    @Override
    public int actionPoints(Actor subject) {
        return 0;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData("Store all", canChoose(subject), new String[]{owner.getName(), "transfer", items.get(0).getName() + subject.inventory().itemCountLabel(items.get(0).getTemplateID())});
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionInventoryStoreAll)) {
            return false;
        } else {
            ActionInventoryStoreAll other = (ActionInventoryStoreAll) o;
            return other.owner == this.owner && other.items.equals(this.items) && other.inventory == this.inventory;
        }
    }

}
