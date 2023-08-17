package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.CompleteActionEvent;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataActorInventory;
import com.github.finley243.adventureengine.menu.action.MenuDataObjectInventory;
import com.github.finley243.adventureengine.textgen.*;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class ActionInventoryTakeAll extends Action {

    private final Noun owner;
    private final String name;
    private final Inventory inventory;
    private final Item item;
    private final String prompt;
    private final String phrase;

    public ActionInventoryTakeAll(Noun owner, String name, Inventory inventory, Item item, String prompt, String phrase) {
        if (item.hasState()) throw new IllegalArgumentException("Cannot perform ActionInventoryTakeAll on item with state");
        this.owner = owner;
        this.name = name;
        this.inventory = inventory;
        this.item = item;
        this.prompt = prompt;
        this.phrase = phrase;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        int count = inventory.itemCount(item);
        inventory.removeItems(item.getTemplateID(), count);
        subject.getInventory().addItems(item.getTemplateID(), count);
        TextContext context = new TextContext(new MapBuilder<String, Noun>().put("actor", subject).put("item", new PluralNoun(item, count)).put("inventory", owner).build());
        subject.game().eventQueue().addToEnd(new SensoryEvent(subject.getArea(), Phrases.get(phrase), context, this, null, subject, null));
        subject.game().eventQueue().addToEnd(new CompleteActionEvent(subject, this, repeatActionCount));
    }

    @Override
    public int actionPoints(Actor subject) {
        return 0;
    }

    @Override
    public ActionCategory getCategory(Actor subject) {
        if (owner instanceof Actor) {
            return ActionCategory.ACTOR_INV;
        } else {
            return ActionCategory.OBJECT_INV;
        }
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        if (owner instanceof Actor actor) {
            return new MenuDataActorInventory(actor, item, false);
        } else {
            WorldObject object = (WorldObject) owner;
            return new MenuDataObjectInventory(object, item, false);
        }
    }

    @Override
    public String getPrompt(Actor subject) {
        return prompt + " All";
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