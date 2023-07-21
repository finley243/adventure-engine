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

	private boolean disabled;
	private String disabledReason;

	public Action() {}

	public abstract void choose(Actor subject, int repeatActionCount);

	public abstract MenuChoice getMenuChoices(Actor subject);

	public CanChooseResult canChoose(Actor subject) {
		if (disabled) {
			return new CanChooseResult(false, disabledReason);
		}
		return new CanChooseResult(true, null);
	}

	public void setDisabled(boolean disabled, String reason) {
		this.disabled = disabled;
		this.disabledReason = reason;
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

	public boolean canShow(Actor subject) {
		return true;
	}

	public record CanChooseResult(boolean canChoose, String reason) {}

}
