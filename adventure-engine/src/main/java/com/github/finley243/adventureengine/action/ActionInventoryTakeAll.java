package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Inventory;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataActorInventory;
import com.github.finley243.adventureengine.menu.action.MenuDataObjectInventory;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.PluralNoun;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class ActionInventoryTakeAll extends Action {

    private final Noun owner;
    private final Inventory inventory;
    private final Item item;
    private final String prompt;
    private final String phrase;

    public ActionInventoryTakeAll(Actor subject, ActionDependencies dependencies, Noun owner, Inventory inventory, Item item, String prompt, String phrase) {
        super(subject, dependencies);
        if (item.hasState()) throw new IllegalArgumentException("Cannot perform ActionInventoryTakeAll on item with state");
        this.owner = owner;
        this.inventory = inventory;
        this.item = item;
        this.prompt = prompt;
        this.phrase = phrase;
    }

    @Override
    public String getID() {
        return "inventory_take_all";
    }

    @Override
    public Context getContext() {
        int count = subject.getInventory().itemCount(item);
        Context context = Context.builder().subject(subject).build();
        context.setLocalVariable("inventory", Expression.noun(owner));
        context.setLocalVariable("count", Expression.integer(inventory.itemCount(item)));
        context.setLocalVariable("item", Expression.noun(new PluralNoun(item, count)));
        return context;
    }

    @Override
    public void choose(int repeatActionCount) {
        int count = inventory.itemCount(item);
        inventory.removeItems(item.getTemplateID(), count);
        subject.getInventory().addItems(item.getTemplateID(), count);
        Context context = getContext();
        sensoryEventDispatcher.dispatch(new SensoryEvent(subject.getArea(), phrase, context, true, this, null));
    }

    @Override
    public int actionPoints() {
        return 0;
    }

    @Override
    public MenuData getMenuData() {
        if (owner instanceof Actor actor) {
            return new MenuDataActorInventory(actor, item, true, false);
        } else {
            WorldObject object = (WorldObject) owner;
            return new MenuDataObjectInventory(object, item, true, false);
        }
    }

    @Override
    public String getPrompt() {
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