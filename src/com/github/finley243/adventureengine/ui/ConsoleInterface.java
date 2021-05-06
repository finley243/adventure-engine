package com.github.finley243.adventureengine.ui;

import com.github.finley243.adventureengine.event.TextEvent;
import com.google.common.eventbus.Subscribe;

public class ConsoleInterface implements UserInterface {

	public ConsoleInterface() {
		
	}
	
	@Override
	@Subscribe
	public void onTextEvent(TextEvent event) {
		System.out.println(event.getText());
		System.out.println();
	}

}
