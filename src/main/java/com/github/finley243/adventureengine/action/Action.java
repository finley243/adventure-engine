package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEventDispatcher;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.textgen.TextGen;

/**
 * An action that an actor can take
 */
public abstract class Action {

	public enum ActionDetectionChance {
		HIGH, LOW, NONE
	}

	protected final Actor subject;
	protected final ScriptRuntime scriptRuntime;
	protected final SensoryEventDispatcher sensoryEventDispatcher;
	protected final TextGen textGen;

	private boolean disabled;
	private String disabledReason;

	public Action(Actor subject, ActionDependencies dependencies) {
		this.subject = subject;
		this.scriptRuntime = dependencies.scriptRuntime();
		this.sensoryEventDispatcher = dependencies.sensoryEventDispatcher();
		this.textGen = dependencies.textGen();
	}

	public abstract String getID();

	public abstract Context getContext();

	public abstract void choose(int repeatActionCount);

	public abstract MenuData getMenuData();

	public abstract String getPrompt();

	public CanChooseResult canChoose() {
		if (disabled) {
			return new CanChooseResult(false, disabledReason);
		}
		return new CanChooseResult(true, null);
	}

	public void setDisabled(boolean disabled, String reason) {
		this.disabled = disabled;
		this.disabledReason = reason;
	}
	
	public float utility() {
		return 0.0f;
	}

	public int actionPoints() {
		return 1;
	}
	
	public int repeatCount() {
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

	public boolean canShow() {
		return true;
	}

	public record CanChooseResult(boolean canChoose, String reason) {}

}
