package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.*;
import com.github.finley243.adventureengine.item.Item;

public class ActionInventoryTakeAll extends Action {

    private final Noun owner;
    private final Inventory inventory;
    private final Item item;

    public ActionInventoryTakeAll(Noun owner, Inventory inventory, Item item) {
        this.owner = owner;
        this.inventory = inventory;
        this.item = item;
    }

    @Override
    public void choose(Actor subject) {
        int count = inventory.itemCount(item);
        inventory.removeItems(item, count);
        subject.inventory().addItems(item, count);
        Context context = new Context(new NounMapper().put("actor", subject).put("item", new PluralNoun(item, count)).put("inventory", owner).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("takeFrom"), context, this, subject));
    }

    @Override
    public int actionPoints(Actor subject) {
        return 0;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData("Take all", canChoose(subject), new String[]{owner.getName(), item.getName() + inventory.itemCountLabel(item)});
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionInventoryTakeAll)) {
            return false;
        } else {
            ActionInventoryTakeAll other = (ActionInventoryTakeAll) o;
            return other.owner == this.owner && other.item == this.item && other.inventory == this.inventory;
        }
    }
    
}