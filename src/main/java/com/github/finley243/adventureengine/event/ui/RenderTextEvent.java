package com.github.finley243.adventureengine.event.ui;

public class RenderTextEvent extends UIEvent {

	private final String text;
	
	public RenderTextEvent(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
}
