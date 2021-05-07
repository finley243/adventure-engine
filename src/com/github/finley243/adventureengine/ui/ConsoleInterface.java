package com.github.finley243.adventureengine.ui;

import java.util.List;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.event.RenderMenuEvent;
import com.github.finley243.adventureengine.event.MenuSelectEvent;
import com.github.finley243.adventureengine.event.RenderTextEvent;
import com.github.finley243.adventureengine.menu.ConsoleUtils;
import com.google.common.eventbus.Subscribe;

public class ConsoleInterface implements UserInterface {

	private RenderMenuEvent lastMenuEvent;
	
	public ConsoleInterface() {
		
	}
	
	@Override
	@Subscribe
	public void onTextEvent(RenderTextEvent event) {
		System.out.println(event.getText());
	}
	
	@Override
	@Subscribe
	public void onMenuEvent(RenderMenuEvent event) {
		lastMenuEvent = event;
		List<String> choices = event.getChoices();
		for(int i = 0; i < choices.size(); i++) {
			System.out.println((i + 1) + ") " + choices.get(i));
		}
		int response = ConsoleUtils.intInRange(1, lastMenuEvent.getChoices().size());
		System.out.println();
		Game.EVENT_BUS.post(new MenuSelectEvent(response - 1));
	}

}
