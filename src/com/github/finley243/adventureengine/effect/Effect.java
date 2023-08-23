package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.MutableStatController;
import com.github.finley243.adventureengine.stat.MutableStatHolder;

/**
 * An effect that modifies an EffectableStatHolder
 */
public class Effect extends GameInstanced {

	private final boolean manualRemoval;
	/** If set to -1, the effect will remain active until the endCondition is met. If set to 0, the effect is instant. */
	private final int duration;
	private final boolean stackable;
	private final Condition conditionAdd;
	private final Condition conditionRemove;
	private final Condition conditionActive;
	private final Script scriptAdd;
	private final Script scriptRemove;
	private final Script scriptRound;

	public Effect(Game game, String ID, int duration, boolean manualRemoval, boolean stackable, Condition conditionAdd, Condition conditionRemove, Condition conditionActive, Script scriptAdd, Script scriptRemove, Script scriptRound) {
		super(game, ID);
		this.manualRemoval = manualRemoval;
		this.duration = duration;
		this.stackable = stackable;
		this.conditionAdd = conditionAdd;
		this.conditionRemove = conditionRemove;
		this.conditionActive = conditionActive;
		this.scriptAdd = scriptAdd;
		this.scriptRemove = scriptRemove;
		this.scriptRound = scriptRound;
	}

	public void start(MutableStatController controller) {}

	public void end(MutableStatController controller) {}

	public void eachRound(MutableStatController controller) {}

	public boolean manualRemoval() {
		return manualRemoval;
	}

	public int getDuration() {
		return duration;
	}

	public boolean isStackable() {
		return stackable;
	}

	public boolean isInstant() {
		return !manualRemoval && duration == 0;
	}

	public boolean needsSaveData() {
		return !manualRemoval;
	}

	public Condition getConditionAdd() {
		return conditionAdd;
	}

	public Condition getConditionRemove() {
		return conditionRemove;
	}

	public Condition getConditionActive() {
		return conditionActive;
	}

	public Script getScriptAdd() {
		return scriptAdd;
	}

	public Script getScriptRemove() {
		return scriptRemove;
	}

	public Script getScriptRound() {
		return scriptRound;
	}
	
}
