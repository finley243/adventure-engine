package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorPlayer;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataEquipped;
import com.github.finley243.adventureengine.menu.data.MenuDataInventory;
import com.github.finley243.adventureengine.menu.data.MenuDataWorldObject;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class ActionInspect implements Action {

	public enum InspectType {
		WORLD, INVENTORY, EQUIPPED
	}

	private boolean disabled;
	private final WorldObject object;
	private final InspectType type;
	
	public ActionInspect(WorldObject object, InspectType type) {
		this.object = object;
		this.type = type;
	}
	
	@Override
	public void choose(Actor subject) {
		if(subject instanceof ActorPlayer) {
			Game.EVENT_BUS.post(new RenderTextEvent(object.getDescription()));
		}
	}

	@Override
	public boolean canChoose(Actor subject) {
		return !disabled;
	}

	@Override
	public void disable() {
		disabled = true;
	}

	@Override
	public float utility(Actor subject) {
		// TODO Auto-generated method stub
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
	public MenuData getMenuData(Actor subject) {
		switch(type) {
		case EQUIPPED:
			return new MenuDataEquipped("Inspect", "Inspect " + object.getFormattedName(false), canChoose(subject), (Item) object);
		case INVENTORY:
			return new MenuDataInventory("Inspect", "Inspect " + object.getFormattedName(false), canChoose(subject), (Item) object);
		case WORLD:
			return new MenuDataWorldObject("Inspect", "Inspect " + object.getFormattedName(false), canChoose(subject), object);
		default:
			return null;
		}
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionInspect)) {
            return false;
        } else {
            ActionInspect other = (ActionInspect) o;
            return other.object == this.object;
        }
    }

}
