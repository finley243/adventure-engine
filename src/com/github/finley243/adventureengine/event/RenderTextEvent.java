package com.github.finley243.adventureengine.event;

public class RenderTextEvent {

	private String text;
	
	public RenderTextEvent(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
}
