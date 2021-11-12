package com.github.finley243.adventureengine.ui;

import java.util.List;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.event.ui.RenderMenuEvent;
import com.github.finley243.adventureengine.event.ui.MenuSelectEvent;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.menu.ConsoleUtils;
import com.google.common.eventbus.Subscribe;

public class ConsoleInterface implements UserInterface {

	public ConsoleInterface() {
		
	}
	
	@Override
	@Subscribe
	public void onTextEvent(RenderTextEvent e) {
		System.out.println(e.getText());
	}
	
	@Override
	@Subscribe
	public void onMenuEvent(RenderMenuEvent e) {
		List<String> choices = e.getChoices();
		for(int i = 0; i < choices.size(); i++) {
			System.out.println((i + 1) + ") " + choices.get(i));
		}
		int response = ConsoleUtils.intInRange(1, e.getChoices().size());
		System.out.println();
		Game.EVENT_BUS.post(new MenuSelectEvent(response - 1));
	}

}
