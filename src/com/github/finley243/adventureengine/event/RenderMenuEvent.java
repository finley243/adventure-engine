package com.github.finley243.adventureengine.event;

import java.util.List;

public class RenderMenuEvent {

	private List<String> choices;
	private List<String[]> menuStructures;
	
	public RenderMenuEvent(List<String> choices, List<String[]> menuStructures) {
		this.choices = choices;
		this.menuStructures = menuStructures;
	}
	
	public List<String> getChoices() {
		return choices;
	}
	
	public List<String[]> getMenuStructures() {
		return menuStructures;
	}
	
}
