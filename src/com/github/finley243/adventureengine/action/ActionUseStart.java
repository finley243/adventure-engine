package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.ObjectChair;
import com.github.finley243.adventureengine.world.object.ObjectCover;
import com.github.finley243.adventureengine.world.object.UsableObject;

public class ActionUseStart extends Action {

	private final UsableObject object;
	
	public ActionUseStart(UsableObject object) {
		this.object = object;
	}
	
	@Override
	public void choose(Actor subject) {
		object.setUser(subject);
		subject.startUsingObject(object);
		Context context = new Context(subject, false, object, false);
		String phrase = null;
		if(object instanceof ObjectCover) {
			phrase = "takeCover";
		} else if(object instanceof ObjectChair) {
			phrase = "sit";
		}
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get(phrase), context, this, subject));
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		String prompt = null;
		String fullPrompt = null;
		if(object instanceof ObjectCover) {
			prompt = "Take cover";
			fullPrompt = "Take cover behind " + object.getFormattedName(false);
		} else if(object instanceof ObjectChair) {
			prompt = "Sit";
			fullPrompt = "Sit in " + object.getFormattedName(false);
		}
		return new MenuData(prompt, fullPrompt, canChoose(subject), new String[]{object.getName()});
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionUseStart)) {
            return false;
        } else {
            ActionUseStart other = (ActionUseStart) o;
            return other.object == this.object;
        }
    }

}
