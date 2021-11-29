package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorPlayer;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.menu.data.*;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class ActionInspect extends Action {

	public enum InspectType {
		WORLD, INVENTORY, EQUIPPED
	}

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
	public boolean usesAction() {
		return false;
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		switch(type) {
		case EQUIPPED:
		case INVENTORY:
			return new MenuDataNested("Inspect", "Inspect " + object.getFormattedName(false), canChoose(subject), new String[]{"inventory", object.getName()});
		case WORLD:
			return new MenuDataNested("Inspect", "Inspect " + object.getFormattedName(false), canChoose(subject), new String[]{object.getName()});
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
