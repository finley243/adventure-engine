package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.ObjectCover;
import com.github.finley243.adventureengine.world.object.UsableObject;

public class ActionStand extends Action {

	private final UsableObject object;
	
	public ActionStand(UsableObject object) {
		this.object = object;
	}
	
	@Override
	public void choose(Actor subject) {
		object.removeUser();
		subject.stopUsingObject();
		Context context = new Context(subject, false, object, false);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("stand"), context, this, subject));
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		if(object instanceof ObjectCover) {
			return new MenuData("Leave cover", "Leave cover", canChoose(subject), new String[]{object.getName()});
		} else {
			return new MenuData("Stand", "Stand up", canChoose(subject), new String[]{object.getName()});
		}
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionStand)) {
            return false;
        } else {
            ActionStand other = (ActionStand) o;
            return other.object == this.object;
        }
    }

}
