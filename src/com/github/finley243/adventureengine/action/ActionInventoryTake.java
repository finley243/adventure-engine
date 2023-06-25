package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.TextContext;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.item.Item;

public class ActionInventoryTake extends Action {

    private final Noun owner;
    private final String name;
    private final Inventory inventory;
    private final Item item;

    public ActionInventoryTake(Noun owner, String name, Inventory inventory, Item item) {
        this.owner = owner;
        this.name = name;
        this.inventory = inventory;
        this.item = item;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        inventory.removeItem(item);
        subject.getInventory().addItem(item);
        TextContext context = new TextContext(new MapBuilder<String, Noun>().put("actor", subject).put("item", item).put("inventory", owner).build());
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
        return new MenuChoice("Take", canChoose(subject).canChoose(), menuPath, new String[]{"take " + item.getName() + " from " + owner.getName(), "pick up " + item.getName() + " from " + owner.getName(), "pickup " + item.getName() + " from " + owner.getName()});
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ActionInventoryTake other)) {
            return false;
        } else {
            return other.owner == this.owner && other.item == this.item && other.inventory == this.inventory;
        }
    }
    
}
