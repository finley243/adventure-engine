package com.github.finley243.adventureengine.event;

public class DisplayTextEvent {

	private String text;
	
	public DisplayTextEvent(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
}
