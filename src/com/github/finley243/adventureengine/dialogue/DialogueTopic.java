package com.github.finley243.adventureengine.dialogue;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.load.SaveData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DialogueTopic {

	public enum TopicType {
		SEQUENTIAL, SELECTOR, RANDOM
	}

	private final String ID;

	private final Condition condition;
	private final boolean once;
	private final List<DialogueLine> lines;
	private final List<DialogueChoice> choices;

	private final TopicType type;

	private final Set<String> hasTriggered;
	
	public DialogueTopic(String ID, Condition condition, boolean once, List<DialogueLine> lines, List<DialogueChoice> choices, TopicType type) {
		this.ID = ID;
		this.condition = condition;
		this.once = once;
		this.lines = lines;
		this.choices = choices;
		this.type = type;
		this.hasTriggered = new HashSet<>();
	}
	
	public String getID() {
		return ID;
	}

	public boolean canChoose(Actor subject) {
		return (condition == null || condition.isMet(subject)) && (!once || !hasTriggered.contains(subject.getID()));
	}
	
	public List<DialogueLine> getLines() {
		return lines;
	}

	public List<DialogueChoice> getChoices() {
		return choices;
	}

	public TopicType getType() {
		return type;
	}

	public void setVisited(Actor subject) {
		hasTriggered.add(subject.getID());
	}

	public boolean hasVisited() {
		return !hasTriggered.isEmpty();
	}

	public void loadState(SaveData saveData) {
		if ("hasTriggered".equals(saveData.getParameter())) {
			this.hasTriggered.add(saveData.getValueString());
		}
	}

	public List<SaveData> saveState() {
		List<SaveData> state = new ArrayList<>();
		for (String hasTriggeredActor : hasTriggered) {
			state.add(new SaveData(SaveData.DataType.TOPIC, this.getID(), "hasTriggered", hasTriggeredActor));
		}
		return state;
	}
	
}
