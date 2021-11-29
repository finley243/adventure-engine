package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuData;

/**
 * An action that an actor can take
 */
public abstract class Action {

	protected boolean disabled;

	public abstract void choose(Actor subject);

	public boolean canChoose(Actor subject) {
		return !disabled;
	}

	public void disable() {
		disabled = true;
	}
	
	public float utility(Actor subject) {
		return 0.0f;
	}

	public boolean usesAction() {
		return true;
	}
	
	public boolean canRepeat() {
		return true;
	}

	public boolean isRepeatMatch(Action action) {
		return false;
	}
	
	public int actionCount() {
		return 1;
	}
	
	public abstract MenuData getMenuData(Actor subject);
	
}
