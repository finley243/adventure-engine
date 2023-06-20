package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.*;
import com.github.finley243.adventureengine.item.Item;

public class ActionInventoryTakeAll extends Action {

    private final Noun owner;
    private final String name;
    private final Inventory inventory;
    private final Item item;

    public ActionInventoryTakeAll(Noun owner, String name, Inventory inventory, Item item) {
        if (item.hasState()) throw new IllegalArgumentException("Cannot perform ActionInventoryTakeAll on item with state");
        this.owner = owner;
        this.name = name;
        this.inventory = inventory;
        this.item = item;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        int count = inventory.itemCount(item);
        inventory.removeItems(item.getTemplateID(), count);
        subject.getInventory().addItems(item.getTemplateID(), count);
        TextContext context = new TextContext(new MapBuilder<String, Noun>().put("actor", subject).put("item", new PluralNoun(item, count)).put("inventory", owner).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("takeFrom"), context, this, null, subject, null));
    }

    @Override
    public int actionPoints(Actor subject) {
        return 0;
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        String[] menuPath;
        if (name == null) {
            menuPath = new String[]{LangUtils.titleCase(owner.getName()), Inventory.getItemNameFormatted(item, inventory)};
        } else {
            menuPath = new String[]{LangUtils.titleCase(owner.getName()), LangUtils.titleCase(name), Inventory.getItemNameFormatted(item, inventory)};
        }
        return new MenuChoice("Take all", canChoose(subject), menuPath, new String[]{"take everything from " + owner.getName(), "pick up everything from " + owner.getName(), "pickup everything from " + owner.getName(), "take all items from " + owner.getName(), "pick up all items from " + owner.getName(), "pickup all items from " + owner.getName()});
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ActionInventoryTakeAll other)) {
            return false;
        } else {
            return other.owner == this.owner && other.item == this.item && other.inventory == this.inventory;
        }
    }
    
}