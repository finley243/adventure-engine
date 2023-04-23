package com.github.finley243.adventureengine.scene;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.stat.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Scene extends GameInstanced implements StatHolder {

	public enum SceneType {
		SEQUENTIAL, SELECTOR, RANDOM
	}

	private final Condition condition;
	private final boolean once;
	private final int priority;
	private final List<SceneLine> lines;
	private final List<SceneChoice> choices;

	private final SceneType type;

	private boolean hasTriggered;
	
	public Scene(Game game, String ID, Condition condition, boolean once, int priority, List<SceneLine> lines, List<SceneChoice> choices, SceneType type) {
		super(game, ID);
		this.condition = condition;
		this.once = once;
		this.priority = priority;
		this.lines = lines;
		this.choices = choices;
		this.type = type;
		this.hasTriggered = false;
	}

	public boolean canChoose(Actor subject, Actor target) {
		return (condition == null || condition.isMet(new Context(game(), subject, target))) && !(once && hasTriggered);
	}
	
	public List<SceneLine> getLines() {
		return lines;
	}

	public List<SceneChoice> getChoices() {
		return choices;
	}

	public SceneType getType() {
		return type;
	}

	public int getPriority() {
		return priority;
	}

	public void setTriggered() {
		hasTriggered = true;
	}

	@Override
	public int getValueInt(String name) {
		return 0;
	}

	@Override
	public float getValueFloat(String name) {
		return 0;
	}

	@Override
	public boolean getValueBoolean(String name) {
		if ("triggered".equals(name)) {
			return hasTriggered;
		}
		return false;
	}

	@Override
	public String getValueString(String name) {
		if ("id".equals(name)) {
			return getID();
		}
		return null;
	}

	@Override
	public Set<String> getValueStringSet(String name) {
		return null;
	}

	@Override
	public void setStateBoolean(String name, boolean value) {
		if ("triggered".equals(name)) {
			hasTriggered = value;
		}
	}

	@Override
	public void setStateInteger(String name, int value) {

	}

	@Override
	public void setStateFloat(String name, float value) {

	}

	@Override
	public void setStateString(String name, String value) {

	}

	@Override
	public void setStateStringSet(String name, Set<String> value) {

	}

	@Override
	public void modStateInteger(String name, int amount) {

	}

	@Override
	public void modStateFloat(String name, float amount) {

	}

	@Override
	public Inventory getInventory() {
		return null;
	}

	@Override
	public StatHolder getSubHolder(String name, String ID) {
		return null;
	}

	public void loadState(SaveData saveData) {
		if ("hasTriggered".equals(saveData.getParameter())) {
			this.hasTriggered = saveData.getValueBoolean();
		}
	}

	public List<SaveData> saveState() {
		List<SaveData> state = new ArrayList<>();
		if (hasTriggered) {
			state.add(new SaveData(SaveData.DataType.SCENE, this.getID(), "hasTriggered", hasTriggered));
		}
		return state;
	}
	
}
