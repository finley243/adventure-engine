package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.menu.data.MenuData;

import java.util.List;

public class RenderMenuEvent {

	private final List<String> choices;
	private final List<MenuData> menuData;
	
	public RenderMenuEvent(List<String> choices, List<MenuData> menuData) {
		if (choices.size() != menuData.size()) throw new IllegalArgumentException();
		this.choices = choices;
		this.menuData = menuData;
		for(int i = 0; i < menuData.size(); i++) {
			menuData.get(i).setIndex(i);
		}
	}
	
	public List<String> getChoices() {
		return choices;
	}
	
	public List<MenuData> getMenuData() {
		return menuData;
	}
	
}
