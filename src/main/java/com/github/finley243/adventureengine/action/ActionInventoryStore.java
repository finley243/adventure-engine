package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
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

public class ActionInventoryStore extends Action {

    private final Noun owner;
    private final Inventory inventory;
    private final Item item;
    private final String prompt;
    private final String phrase;

    public ActionInventoryStore(Noun owner, Inventory inventory, Item item, String prompt, String phrase) {
        this.owner = owner;
        this.inventory = inventory;
        this.item = item;
        this.prompt = prompt;
        this.phrase = phrase;
    }

    @Override
    public String getID() {
        return "inventory_store";
    }

    @Override
    public Context getContext(Game game, Actor subject) {
        Context context = Context.builder(game).subject(subject).parentItem(item).build();
        context.setLocalVariable("inventory", Expression.constantNoun(owner));
        return context;
    }

    @Override
    public void choose(Game game, int repeatActionCount, Actor subject) {
        subject.getInventory().removeItem(item, game);
        inventory.addItem(item, game);
        Context context = getContext(game, subject);
        SensoryEvent.execute(game, new SensoryEvent(subject.getArea(), Phrases.get(phrase), context, true, this, null));
    }

    @Override
    public int actionPoints(Game game, Actor subject) {
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
    public String getPrompt(Game game, Actor subject) {
        return prompt;
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
