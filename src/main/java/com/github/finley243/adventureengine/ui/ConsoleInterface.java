package com.github.finley243.adventureengine.ui;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.event.ui.ChoiceMenuInputEvent;
import com.github.finley243.adventureengine.event.ui.RenderNumericMenuEvent;
import com.github.finley243.adventureengine.event.ui.RenderChoiceMenuEvent;
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
	public void onMenuEvent(RenderChoiceMenuEvent e) {
		List<MenuChoice> validChoices = new ArrayList<>();
		for (MenuChoice choice : e.getMenuChoices()) {
			if (choice.isEnabled()) {
				validChoices.add(choice);
			}
		}
		for (int i = 0; i < validChoices.size(); i++) {
			System.out.println((i + 1) + ") " + validChoices.get(i).getPrompt());
		}
		int response = ConsoleUtils.intInRange(1, validChoices.size());
		System.out.println();
		game.eventBus().post(new ChoiceMenuInputEvent(validChoices.get(response - 1).getIndex()));
	}

	@Override
	public void onNumericMenuEvent(RenderNumericMenuEvent event) {

	}

}
