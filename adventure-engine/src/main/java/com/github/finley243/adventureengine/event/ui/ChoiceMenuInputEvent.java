package com.github.finley243.adventureengine.event.ui;

public class ChoiceMenuInputEvent extends UIEvent {

	private final int index;
	
	public ChoiceMenuInputEvent(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	
}
