package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataWorldObject;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.ObjectChair;

public class ActionSit extends Action {

	private final ObjectChair chair;
	
	public ActionSit(ObjectChair chair) {
		this.chair = chair;
	}
	
	@Override
	public void choose(Actor subject) {
		chair.setUser(subject);
		subject.startUsingObject(chair);
		Context context = new Context(subject, false, chair, false);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("sit"), context, this, subject));
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuDataWorldObject("Sit", "Sit in " + chair.getFormattedName(false), canChoose(subject), chair);
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionSit)) {
            return false;
        } else {
            ActionSit other = (ActionSit) o;
            return other.chair == this.chair;
        }
    }

}
