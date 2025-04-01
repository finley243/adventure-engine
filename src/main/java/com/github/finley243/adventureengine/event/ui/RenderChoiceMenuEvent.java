package com.github.finley243.adventureengine.event.ui;

import com.github.finley243.adventureengine.menu.MenuCategory;
import com.github.finley243.adventureengine.menu.MenuChoice;

import java.util.List;

public class RenderChoiceMenuEvent {

	private final List<MenuChoice> menuChoices;
	private final List<MenuCategory> menuCategories;
	// Set to -1 when there is no available end turn action
	private final int endTurnIndex;
	// If true, will use a prompt-based system even with a parser interface (primarily for dialogue)
	private final boolean forcePrompts;
	
	public RenderChoiceMenuEvent(List<MenuChoice> menuChoices, List<MenuCategory> menuCategories, int endTurnIndex, boolean forcePrompts) {
		this.menuChoices = menuChoices;
		this.menuCategories = menuCategories;
		this.endTurnIndex = endTurnIndex;
		this.forcePrompts = forcePrompts;
		for (int i = 0; i < menuChoices.size(); i++) {
			menuChoices.get(i).setIndex(i);
		}
	}

	public List<MenuChoice> getMenuChoices() {
		return menuChoices;
	}

	public List<MenuCategory> getMenuCategories() {
		return menuCategories;
	}

	public int getEndTurnIndex() {
		return endTurnIndex;
	}

	public boolean shouldForcePrompts() {
		return forcePrompts;
	}
	
}
