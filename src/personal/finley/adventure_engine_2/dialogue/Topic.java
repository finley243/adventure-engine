package personal.finley.adventure_engine_2.dialogue;

import java.util.List;

public class Topic {

	private List<Line> lines;
	private List<Choice> choices;
	
	public Topic(List<Line> lines, List<Choice> choices) {
		this.lines = lines;
		this.choices = choices;
	}
	
	public List<Line> getLines() {
		return lines;
	}

	public List<Choice> getChoices() {
		return choices;
	}
	
}
