package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.Noun;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.object.ObjectContainer;

public class ActionInventoryStore extends Action {

    private final Noun owner;
    private final Inventory inventory;
    private final Item item;

    public ActionInventoryStore(Noun owner, Inventory inventory, Item item) {
        this.owner = owner;
        this.inventory = inventory;
        this.item = item;
    }

    @Override
    public void choose(Actor subject) {
        subject.inventory().removeItem(item);
        inventory.addItem(item);
        Context context = new Context(subject, false, item, true, owner, false);
        Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("storeIn"), context, this, subject));
    }

    @Override
    public int actionPoints() {
        return 0;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        String fullPrompt = "Put " + item.getFormattedName(true) + " in " + owner.getFormattedName(false);
        return new MenuData(LangUtils.titleCase(item.getName()) + inventory.itemCountLabel(item.getStatsID()), fullPrompt, canChoose(subject), new String[]{owner.getName(), "store"});
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionInventoryStore)) {
            return false;
        } else {
            ActionInventoryStore other = (ActionInventoryStore) o;
            return other.owner == this.owner && other.item == this.item && other.inventory == this.inventory;
        }
    }

}
