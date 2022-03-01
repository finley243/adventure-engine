package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorPlayer;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.menu.MenuData;
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
		Game.EVENT_BUS.post(new RenderTextEvent(object.getDescription()));
		object.triggerScript("on_inspect", subject);
	}

	@Override
	public int actionPoints() {
		return 0;
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		String[] category;
		switch(type) {
			case EQUIPPED:
			case INVENTORY:
				category = new String[]{"inventory", object.getName()};
				break;
			case WORLD:
			default:
				category = new String[]{object.getName()};
				break;
		}
		return new MenuData("Inspect", canChoose(subject), category);
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
