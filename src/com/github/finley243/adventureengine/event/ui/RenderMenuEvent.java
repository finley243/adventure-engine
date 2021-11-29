package com.github.finley243.adventureengine.event.ui;

import com.github.finley243.adventureengine.menu.MenuData;

import java.util.List;

public class RenderMenuEvent {

	private final List<MenuData> menuData;
	
	public RenderMenuEvent(List<MenuData> menuData) {
		this.menuData = menuData;
		for(int i = 0; i < menuData.size(); i++) {
			menuData.get(i).setIndex(i);
		}
	}

	public List<MenuData> getMenuData() {
		return menuData;
	}
	
}
