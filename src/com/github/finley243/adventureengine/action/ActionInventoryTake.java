package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataWorldActor;
import com.github.finley243.adventureengine.menu.data.MenuDataWorldObject;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.Noun;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class ActionInventoryTake implements Action {

    private final Noun owner;
    private final Inventory inventory;
    private final Item item;

    public ActionInventoryTake(Noun owner, Inventory inventory, Item item) {
        this.owner = owner;
        this.inventory = inventory;
        this.item = item;
    }

    @Override
    public void choose(Actor subject) {
        inventory.removeItem(item);
        subject.inventory().addItem(item);
        Context context = new Context(subject, false, item, true, owner, false);
        Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("takeFrom"), context, this, subject));
    }

    @Override
    public String getPrompt() {
        return "Take " + item.getFormattedName(true) + " from " + owner.getFormattedName(false);
    }

    @Override
    public float utility(Actor subject) {
        return 0;
    }
    
    @Override
	public boolean usesAction() {
		return false;
	}
    
    @Override
	public boolean canRepeat() {
		return true;
	}

    @Override
	public boolean isRepeatMatch(Action action) {
		return false;
	}
    
    @Override
	public int actionCount() {
		return 1;
	}

    @Override
    public MenuData getMenuData() {
        if(owner instanceof Actor) {
            return new MenuDataWorldActor("Take " + item.getName(), (Actor) owner);
        } else {
            return new MenuDataWorldObject("Take " + item.getName(), (WorldObject) owner);
        }
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionInventoryTake)) {
            return false;
        } else {
            ActionInventoryTake other = (ActionInventoryTake) o;
            return other.owner == this.owner && other.item == this.item && other.inventory == this.inventory;
        }
    }
    
}
