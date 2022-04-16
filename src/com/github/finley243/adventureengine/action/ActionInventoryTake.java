package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.AudioVisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.world.item.Item;

public class ActionInventoryTake extends Action {

    private final Noun owner;
    private final Inventory inventory;
    private final Item item;

    public ActionInventoryTake(Noun owner, Inventory inventory, Item item) {
        this.owner = owner;
        this.inventory = inventory;
        this.item = item;
    }

    @Override
    public void choose(Actor subject) {
        inventory.removeItem(item);
        subject.inventory().addItem(item);
        Context context = new Context(new NounMapper().put("actor", subject).put("item", item).put("inventory", owner).build());
        subject.game().eventBus().post(new AudioVisualEvent(subject.getArea(), Phrases.get("takeFrom"), context, this, subject));
    }

    @Override
    public int actionPoints(Actor subject) {
        return 0;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData("Take", canChoose(subject), new String[]{owner.getName(), item.getName() + inventory.itemCountLabel(item.getTemplate().getID())});
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionInventoryTake)) {
            return false;
        } else {
            ActionInventoryTake other = (ActionInventoryTake) o;
            return other.owner == this.owner && other.item == this.item && other.inventory == this.inventory;
        }
    }
    
}
