package com.github.finley243.adventureengine.scene;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.load.SaveData;

import java.util.ArrayList;
import java.util.List;

public class Scene {

	public enum SceneType {
		SEQUENTIAL, SELECTOR, RANDOM
	}

	private final String ID;

	private final Condition condition;
	private final boolean once;
	private final int priority;
	private final List<SceneLine> lines;
	private final List<SceneChoice> choices;

	private final SceneType type;

	private boolean hasTriggered;
	
	public Scene(String ID, Condition condition, boolean once, int priority, List<SceneLine> lines, List<SceneChoice> choices, SceneType type) {
		this.ID = ID;
		this.condition = condition;
		this.once = once;
		this.priority = priority;
		this.lines = lines;
		this.choices = choices;
		this.type = type;
		this.hasTriggered = false;
	}
	
	public String getID() {
		return ID;
	}

	public boolean canChoose(Actor subject) {
		return (condition == null || condition.isMet(subject)) && !(once && hasTriggered);
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

	public void setVisited() {
		hasTriggered = true;
	}

	public boolean hasVisited() {
		return hasTriggered;
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
