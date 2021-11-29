package com.github.finley243.adventureengine.ui;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.event.ui.MenuSelectEvent;
import com.github.finley243.adventureengine.event.ui.RenderMenuEvent;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.menu.ConsoleUtils;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConsoleInterface implements UserInterface {

	public ConsoleInterface() {}
	
	@Override
	@Subscribe
	public void onTextEvent(RenderTextEvent e) {
		System.out.println(e.getText());
	}
	
	@Override
	@Subscribe
	public void onMenuEvent(RenderMenuEvent e) {
		List<MenuData> validChoices = new ArrayList<>();
		for(MenuData choice : e.getMenuData()) {
			if(choice.isEnabled()) {
				validChoices.add(choice);
			}
		}
		for(int i = 0; i < validChoices.size(); i++) {
			System.out.println((i + 1) + ") " + validChoices.get(i));
		}
		int response = ConsoleUtils.intInRange(1, validChoices.size());
		System.out.println();
		Game.EVENT_BUS.post(new MenuSelectEvent(validChoices.get(response - 1).getIndex()));
	}

}
