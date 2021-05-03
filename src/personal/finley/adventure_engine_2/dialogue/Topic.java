package personal.finley.adventure_engine_2.dialogue;

import java.util.List;

public class Topic {

	private String ID;
	
	private List<Line> lines;
	private List<Choice> choices;
	
	public Topic(String ID, List<Line> lines, List<Choice> choices) {
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
	
}
