package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuData;

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
	private final ActionDetectionChance detectionChance;

	public Action(ActionDetectionChance detectionChance) {
		this.detectionChance = detectionChance;
	}

	public abstract void choose(Actor subject, int repeatActionCount);

	public abstract MenuData getMenuData(Actor subject);

	public boolean canChoose(Actor subject) {
		return !disabled;
	}

	public void disable() {
		disabled = true;
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
		return detectionChance;
	}

	public ActionResponseType responseType() {
		return ActionResponseType.NONE;
	}

}
