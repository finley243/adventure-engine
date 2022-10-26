package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.*;
import com.github.finley243.adventureengine.item.Item;

public class ActionInventoryStoreAll extends Action {

    private final Noun owner;
    private final String name;
    private final Inventory inventory;
    private final Item item;
    private final boolean containerIsOpen;

    public ActionInventoryStoreAll(Noun owner, String name, Inventory inventory, Item item, boolean containerIsOpen) {
        super(ActionDetectionChance.LOW);
        this.owner = owner;
        this.name = name;
        this.inventory = inventory;
        this.item = item;
        this.containerIsOpen = containerIsOpen;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        int count = subject.inventory().itemCount(item);
        subject.inventory().removeItems(item, count);
        inventory.addItems(item, count);
        Context context = new Context(new NounMapper().put("actor", subject).put("item", new PluralNoun(item, count)).put("inventory", owner).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get((containerIsOpen ? "placeOn" : "storeIn")), context, this, null, subject, null));
    }

    @Override
    public int actionPoints(Actor subject) {
        return 0;
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        String[] menuPath;
        if (name == null) {
            menuPath = new String[]{owner.getName(), "transfer", item.getName() + subject.inventory().itemCountLabel(item)};
        } else {
            menuPath = new String[]{owner.getName(), name, "transfer", item.getName() + subject.inventory().itemCountLabel(item)};
        }
        return new MenuChoice((containerIsOpen ? "Place all" : "Store all"), canChoose(subject), menuPath);
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
