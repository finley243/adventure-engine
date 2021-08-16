package com.github.finley243.adventureengine.dialogue;

import java.util.List;

public class DialogueTopic {

	public enum TopicType {
		SEQUENTIAL, SELECTOR
	}

	private String ID;
	
	private List<DialogueLine> lines;
	private List<DialogueChoice> choices;

	private TopicType type;
	
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
	
}
