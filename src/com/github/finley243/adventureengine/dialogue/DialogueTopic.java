package com.github.finley243.adventureengine.dialogue;

import com.github.finley243.adventureengine.load.SaveData;

import java.util.ArrayList;
import java.util.List;

public class DialogueTopic {

	public enum TopicType {
		SEQUENTIAL, SELECTOR
	}

	private final String ID;
	
	private final List<DialogueLine> lines;
	private final List<DialogueChoice> choices;

	private final TopicType type;

	private boolean hasVisited;
	
	public DialogueTopic(String ID, List<DialogueLine> lines, List<DialogueChoice> choices, TopicType type) {
		this.ID = ID;
		this.lines = lines;
		this.choices = choices;
		this.type = type;
	}
	
	public String getID() {
		return ID;
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

	public void setVisited() {
		hasVisited = true;
	}

	public boolean hasVisited() {
		return hasVisited;
	}

	public void loadState(SaveData saveData) {
		switch(saveData.getParameter()) {
			case "hasVisited":
				this.hasVisited = saveData.getValueBoolean();
				break;
		}
	}

	public List<SaveData> saveState() {
		List<SaveData> state = new ArrayList<>();
		if(hasVisited) {
			state.add(new SaveData(SaveData.DataType.TOPIC, this.getID(), "hasVisited", hasVisited));
		}
		return state;
	}
	
}
