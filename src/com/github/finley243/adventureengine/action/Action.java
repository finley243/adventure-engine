package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.data.MenuData;

/**
 * An action that an actor can take
 */
public interface Action {
	
	public void choose(Actor subject);
	
	public String getPrompt();
	
	public float utility(Actor subject);
	
	public boolean usesAction();
	
	public boolean canRepeat();

	public boolean isRepeatMatch(Action action);
	
	public int actionCount();
	
	public MenuData getMenuData();
	
}
