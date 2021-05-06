package com.github.finley243.adventureengine.ui;

import java.util.List;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.event.DisplayMenuEvent;
import com.github.finley243.adventureengine.event.MenuSelectEvent;
import com.github.finley243.adventureengine.event.DisplayTextEvent;
import com.github.finley243.adventureengine.menu.InputUtils;
import com.google.common.eventbus.Subscribe;

public class ConsoleInterface implements UserInterface {

	private DisplayMenuEvent lastMenuEvent;
	
	public ConsoleInterface() {
		
	}
	
	@Override
	@Subscribe
	public void onTextEvent(DisplayTextEvent event) {
		System.out.println(event.getText());
		System.out.println();
	}
	
	@Override
	@Subscribe
	public void onMenuEvent(DisplayMenuEvent event) {
		lastMenuEvent = event;
		List<String> choices = event.getChoices();
		for(int i = 0; i < choices.size(); i++) {
			System.out.println((i + 1) + ") " + choices.get(i));
		}
		int response = InputUtils.intInRange(1, lastMenuEvent.getChoices().size());
		System.out.println();
		Game.EVENT_BUS.post(new MenuSelectEvent(response));
	}

}
