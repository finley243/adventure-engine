package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataActorInventory;
import com.github.finley243.adventureengine.menu.action.MenuDataObjectInventory;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class ActionInventoryStoreAll extends Action {

    private final Noun owner;
    private final Inventory inventory;
    private final Item item;
    private final String prompt;
    private final String phrase;

    public ActionInventoryStoreAll(Noun owner, Inventory inventory, Item item, String prompt, String phrase) {
        if (item.hasState()) throw new IllegalArgumentException("Cannot perform ActionInventoryStoreAll on item with state");
        this.owner = owner;
        this.inventory = inventory;
        this.item = item;
        this.prompt = prompt;
        this.phrase = phrase;
    }

    @Override
    public String getID() {
        return "inventory_store_all";
    }

    @Override
    public Context getContext(Actor subject) {
        Context context = new Context(subject.game(), subject, null, item);
        context.setLocalVariable("inventory", Expression.constantNoun(owner));
        context.setLocalVariable("count", Expression.constant(subject.getInventory().itemCount(item)));
        return context;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        int count = subject.getInventory().itemCount(item);
        subject.getInventory().removeItems(item.getTemplateID(), count);
        inventory.addItems(item.getTemplateID(), count);
        Context context = getContext(subject);
        //TextContext textContext = new TextContext(new MapBuilder<String, Noun>().put("actor", subject).put("item", new PluralNoun(item, count)).put("inventory", owner).build());
        SensoryEvent.execute(new SensoryEvent(subject.getArea(), Phrases.get(phrase), context, true, this, null));
    }

    @Override
    public int actionPoints(Actor subject) {
        return 0;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        if (owner instanceof Actor actor) {
            return new MenuDataActorInventory(actor, item, true, true);
        } else {
            WorldObject object = (WorldObject) owner;
            return new MenuDataObjectInventory(object, item, true, true);
        }
    }

    @Override
    public String getPrompt(Actor subject) {
        return prompt + " All";
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
