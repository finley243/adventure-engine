package com.github.finley243.adventureengine.event;

import java.util.List;

public class DisplayMenuEvent {

	private List<String> choices;
	
	public DisplayMenuEvent(List<String> choices) {
		this.choices = choices;
	}
	
	public List<String> getChoices() {
		return choices;
	}
	
}
