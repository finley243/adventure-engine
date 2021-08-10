package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataWorldObject;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.ObjectChair;

public class ActionSit implements Action {

	private ObjectChair chair;
	
	public ActionSit(ObjectChair chair) {
		this.chair = chair;
	}
	
	@Override
	public void choose(Actor subject) {
		chair.setUser(subject);
		subject.startUsingObject(chair);
		Context context = new Context(subject, false, chair, false);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("sit"), context));
	}

	@Override
	public String getPrompt() {
		return "Sit in " + chair.getFormattedName(false);
	}

	@Override
	public float utility(Actor subject) {
		return 0;
	}
	
	@Override
	public boolean usesAction() {
		return true;
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
	public ActionLegality getLegality() {
		return ActionLegality.LEGAL;
	}
	
	@Override
	public MenuData getMenuData() {
		return new MenuDataWorldObject("Sit", chair);
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
