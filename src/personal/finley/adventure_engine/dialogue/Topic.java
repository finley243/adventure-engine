package personal.finley.adventure_engine.dialogue;

import java.util.List;

public class Topic {

	public enum TopicType {
		SEQUENTIAL, SELECTOR, RANDOM
	}

	private String ID;
	
	private List<Line> lines;
	private List<Choice> choices;

	private TopicType type;
	
	public Topic(String ID, List<Line> lines, List<Choice> choices, TopicType type) {
		this.ID = ID;
		this.lines = lines;
		this.choices = choices;
	}
	
	public String getID() {
		return ID;
	}
	
	public List<Line> getLines() {
		return lines;
	}

	public List<Choice> getChoices() {
		return choices;
	}

	public TopicType getType() {
		return type;
	}
	
}
