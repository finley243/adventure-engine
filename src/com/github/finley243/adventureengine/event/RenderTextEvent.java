package com.github.finley243.adventureengine.event;

public class RenderTextEvent {

	private final String text;
	
	public RenderTextEvent(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
}
