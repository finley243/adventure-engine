package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataNested;
import com.github.finley243.adventureengine.menu.data.MenuDataWorldActor;
import com.github.finley243.adventureengine.menu.data.MenuDataWorldObject;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.Noun;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class ActionInventoryTake extends Action {

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
	public boolean usesAction() {
		return false;
	}

    @Override
    public MenuData getMenuData(Actor subject) {
        String fullPrompt = "Take " + item.getFormattedName(true) + " from " + owner.getFormattedName(false);
        return new MenuDataNested("Take " + item.getName(), fullPrompt, canChoose(subject), new String[]{owner.getName()});
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
