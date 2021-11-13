package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataUsing;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.ObjectChair;
import com.github.finley243.adventureengine.world.object.ObjectCover;
import com.github.finley243.adventureengine.world.object.UsableObject;

public class ActionStand implements Action {

	private final UsableObject object;
	
	public ActionStand(UsableObject object) {
		this.object = object;
	}
	
	@Override
	public void choose(Actor subject) {
		object.removeUser();
		subject.stopUsingObject();
		Context context = new Context(subject, false, object, false);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("stand"), context));
	}

	@Override
	public String getPrompt() {
		if(object instanceof ObjectCover) {
			return "Leave cover";
		} else {
			return "Stand up";
		}
	}

	@Override
	public float utility(Actor subject) {
		// TODO Auto-generated method stub
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
	public MenuData getMenuData() {
		if(object instanceof ObjectCover) {
			return new MenuDataUsing("Leave cover", object);
		} else {
			return new MenuDataUsing("Stand", object);
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
