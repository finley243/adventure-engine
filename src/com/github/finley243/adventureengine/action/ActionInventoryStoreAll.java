package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.CompleteActionEvent;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.*;
import com.github.finley243.adventureengine.item.Item;

public class ActionInventoryStoreAll extends Action {

    private final Noun owner;
    private final String name;
    private final Inventory inventory;
    private final Item item;
    private final String prompt;
    private final String phrase;

    public ActionInventoryStoreAll(Noun owner, String name, Inventory inventory, Item item, String prompt, String phrase) {
        if (item.hasState()) throw new IllegalArgumentException("Cannot perform ActionInventoryStoreAll on item with state");
        this.owner = owner;
        this.name = name;
        this.inventory = inventory;
        this.item = item;
        this.prompt = prompt;
        this.phrase = phrase;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        int count = subject.getInventory().itemCount(item);
        subject.getInventory().removeItems(item.getTemplateID(), count);
        inventory.addItems(item.getTemplateID(), count);
        TextContext context = new TextContext(new MapBuilder<String, Noun>().put("actor", subject).put("item", new PluralNoun(item, count)).put("inventory", owner).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(phrase), context, this, null, subject, null));
        subject.game().eventQueue().addToEnd(new CompleteActionEvent(subject, this, repeatActionCount));
    }

    @Override
    public int actionPoints(Actor subject) {
        return 0;
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        String[] menuPath;
        if (name == null) {
            menuPath = new String[]{LangUtils.titleCase(owner.getName()), "Transfer", Inventory.getItemNameFormatted(item, subject.getInventory())};
        } else {
            menuPath = new String[]{LangUtils.titleCase(owner.getName()), LangUtils.titleCase(name), "Transfer", Inventory.getItemNameFormatted(item, subject.getInventory())};
        }
        return new MenuChoice(prompt + " all", canChoose(subject).canChoose(), menuPath, new String[]{prompt + " all " + LangUtils.pluralizeNoun(item.getName()) + " in " + owner.getName(), prompt + " all " + LangUtils.pluralizeNoun(item.getName()) + " in " + owner.getName()});
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ActionInventoryStoreAll other)) {
            return false;
        } else {
            return other.owner == this.owner && other.item.equals(this.item) && other.inventory == this.inventory;
        }
    }

}
