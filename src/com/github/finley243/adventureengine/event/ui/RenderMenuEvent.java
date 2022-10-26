package com.github.finley243.adventureengine.event.ui;

import com.github.finley243.adventureengine.menu.MenuChoice;

import java.util.List;

public class RenderMenuEvent {

	private final List<MenuChoice> menuChoices;
	
	public RenderMenuEvent(List<MenuChoice> menuChoices) {
		this.menuChoices = menuChoices;
		for (int i = 0; i < menuChoices.size(); i++) {
			menuChoices.get(i).setIndex(i);
		}
	}

	public List<MenuChoice> getMenuChoices() {
		return menuChoices;
	}
	
}
