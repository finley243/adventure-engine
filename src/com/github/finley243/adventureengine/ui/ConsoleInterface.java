package com.github.finley243.adventureengine.ui;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.event.ui.MenuSelectEvent;
import com.github.finley243.adventureengine.event.ui.NumericMenuEvent;
import com.github.finley243.adventureengine.event.ui.RenderMenuEvent;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.menu.ConsoleUtils;
import com.github.finley243.adventureengine.menu.MenuChoice;

import java.util.ArrayList;
import java.util.List;

public class ConsoleInterface implements UserInterface {

	private final Game game;

	public ConsoleInterface(Game game) {
		this.game = game;
	}
	
	@Override
	public void onTextEvent(RenderTextEvent e) {
		System.out.println(e.getText());
	}
	
	@Override
	public void onMenuEvent(RenderMenuEvent e) {
		List<MenuChoice> validChoices = new ArrayList<>();
		for (MenuChoice choice : e.getMenuChoices()) {
			if(choice.isEnabled()) {
				validChoices.add(choice);
			}
		}
		for (int i = 0; i < validChoices.size(); i++) {
			System.out.println((i + 1) + ") " + validChoices.get(i).getFullPrompt());
		}
		int response = ConsoleUtils.intInRange(1, validChoices.size());
		System.out.println();
		game.eventBus().post(new MenuSelectEvent(validChoices.get(response - 1).getIndex()));
	}

	@Override
	public void onNumericMenuEvent(NumericMenuEvent event) {

	}

}
