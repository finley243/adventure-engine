package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuChoice;

/**
 * An action that an actor can take
 */
public abstract class Action {

	public enum ActionDetectionChance {
		HIGH, LOW, NONE
	}

	public enum ActionResponseType {
		NONE, STEAL, ATTACK, BREAK_LOCK
	}

	private boolean disabled;

	public Action() {}

	public abstract void choose(Actor subject, int repeatActionCount);

	public abstract MenuChoice getMenuChoices(Actor subject);

	public boolean canChoose(Actor subject) {
		return !disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	
	public float utility(Actor subject) {
		return 0.0f;
	}

	public int actionPoints(Actor subject) {
		return 1;
	}
	
	public int repeatCount(Actor subject) {
		return 0;
	}

	public boolean isRepeatMatch(Action action) {
		return false;
	}

	public boolean isBlockedMatch(Action action) {
		return false;
	}

	public ActionDetectionChance detectionChance() {
		return ActionDetectionChance.NONE;
	}

	public ActionResponseType responseType() {
		return ActionResponseType.NONE;
	}

}
