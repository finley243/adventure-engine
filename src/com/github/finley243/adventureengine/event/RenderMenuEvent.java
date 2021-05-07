package com.github.finley243.adventureengine.event;

import java.util.List;

public class RenderMenuEvent {

	private List<String> choices;
	
	public RenderMenuEvent(List<String> choices) {
		this.choices = choices;
	}
	
	public List<String> getChoices() {
		return choices;
	}
	
}
