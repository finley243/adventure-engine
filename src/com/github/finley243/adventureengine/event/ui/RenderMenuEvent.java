package com.github.finley243.adventureengine.event.ui;

import com.github.finley243.adventureengine.menu.MenuChoice;

import java.util.List;

public class RenderMenuEvent {

	private final List<MenuChoice> menuChoices;
	// If true, will use a prompt-based system even with a parser interface (primarily for dialogue)
	private final boolean forcePrompts;
	
	public RenderMenuEvent(List<MenuChoice> menuChoices, boolean forcePrompts) {
		this.menuChoices = menuChoices;
		this.forcePrompts = forcePrompts;
		for (int i = 0; i < menuChoices.size(); i++) {
			menuChoices.get(i).setIndex(i);
		}
	}

	public List<MenuChoice> getMenuChoices() {
		return menuChoices;
	}

	public boolean shouldForcePrompts() {
		return forcePrompts;
	}
	
}
