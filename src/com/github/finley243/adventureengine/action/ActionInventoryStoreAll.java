package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.*;
import com.github.finley243.adventureengine.item.Item;

public class ActionInventoryStoreAll extends Action {

    private final Noun owner;
    private final Inventory inventory;
    private final Item item;

    public ActionInventoryStoreAll(Noun owner, Inventory inventory, Item item) {
        this.owner = owner;
        this.inventory = inventory;
        this.item = item;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        int count = subject.inventory().itemCount(item);
        subject.inventory().removeItems(item, count);
        inventory.addItems(item, count);
        Context context = new Context(new NounMapper().put("actor", subject).put("item", new PluralNoun(item, count)).put("inventory", owner).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("storeIn"), context, this, subject));
    }

    @Override
    public int actionPoints(Actor subject) {
        return 0;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData("Store all", canChoose(subject), new String[]{owner.getName(), "transfer", item.getName() + subject.inventory().itemCountLabel(item)});
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionInventoryStoreAll)) {
            return false;
        } else {
            ActionInventoryStoreAll other = (ActionInventoryStoreAll) o;
            return other.owner == this.owner && other.item.equals(this.item) && other.inventory == this.inventory;
        }
    }

}
