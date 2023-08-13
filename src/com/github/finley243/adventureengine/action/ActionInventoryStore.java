package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.CompleteActionEvent;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.TextContext;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.item.Item;

public class ActionInventoryStore extends Action {

    private final Noun owner;
    private final String name;
    private final Inventory inventory;
    private final Item item;
    private final String prompt;
    private final String phrase;

    public ActionInventoryStore(Noun owner, String name, Inventory inventory, Item item, String prompt, String phrase) {
        this.owner = owner;
        this.name = name;
        this.inventory = inventory;
        this.item = item;
        this.prompt = prompt;
        this.phrase = phrase;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        subject.getInventory().removeItem(item);
        inventory.addItem(item);
        TextContext context = new TextContext(new MapBuilder<String, Noun>().put("actor", subject).put("item", item).put("inventory", owner).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(phrase), context, this, null, subject, null));
        subject.onCompleteAction(new CompleteActionEvent(this, repeatActionCount));
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
        return new MenuChoice(prompt, canChoose(subject).canChoose(), menuPath, new String[]{prompt + " " + item.getName() + " in " + owner.getName(), prompt + " " + item.getName() + " on " + owner.getName()});
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ActionInventoryStore other)) {
            return false;
        } else {
            return other.owner == this.owner && other.item == this.item && other.inventory == this.inventory;
        }
    }

}
