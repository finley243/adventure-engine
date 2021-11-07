package com.github.finley243.adventureengine.event;

public class MenuSelectEvent {

	private final int index;
	
	public MenuSelectEvent(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	
}
