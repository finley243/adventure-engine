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
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class ActionInventoryStoreAll extends Action {

    private final Noun owner;
    private final Inventory inventory;
    private final Item item;
    private final String prompt;
    private final String phrase;

    public ActionInventoryStoreAll(Actor subject, ActionDependencies dependencies, Noun owner, Inventory inventory, Item item, String prompt, String phrase) {
        super(subject, dependencies);
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
    public Context getContext() {
        Context context = Context.builder().subject(subject).parentItem(item).build();
        context.setLocalVariable("inventory", Expression.noun(owner));
        context.setLocalVariable("count", Expression.integer(subject.getInventory().itemCount(item)));
        return context;
    }

    @Override
    public void choose(int repeatActionCount) {
        int count = subject.getInventory().itemCount(item);
        subject.getInventory().removeItems(item.getTemplateID(), count);
        inventory.addItems(item.getTemplateID(), count);
        Context context = getContext();
        //TextContext textContext = new TextContext(new MapBuilder<String, Noun>().put("actor", subject).put("item", new PluralNoun(item, count)).put("inventory", owner).build());
        sensoryEventDispatcher.dispatch(new SensoryEvent(subject.getArea(), Phrases.get(phrase), context, true, this, null));
    }

    @Override
    public int actionPoints() {
        return 0;
    }

    @Override
    public MenuData getMenuData() {
        if (owner instanceof Actor actor) {
            return new MenuDataActorInventory(actor, item, true, true);
        } else {
            WorldObject object = (WorldObject) owner;
            return new MenuDataObjectInventory(object, item, true, true);
        }
    }

    @Override
    public String getPrompt() {
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
