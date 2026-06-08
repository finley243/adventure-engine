package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.action.MenuData;

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

	public abstract String getID();

	public abstract Context getContext(Game game, Actor subject);

	public abstract void choose(Game game, int repeatActionCount, Actor subject);

	public abstract MenuData getMenuData(Actor subject);

	public abstract String getPrompt(Game game, Actor subject);

	public CanChooseResult canChoose(Game game, Actor subject) {
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

	public int actionPoints(Game game, Actor subject) {
		return 1;
	}
	
	public int repeatCount(Actor subject) {
		return 0;
	}

	public boolean repeatsUseNoActionPoints() {
		return false;
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

	public boolean canShow(Game game, Actor subject) {
		return true;
	}

	public record CanChooseResult(boolean canChoose, String reason) {}

}
