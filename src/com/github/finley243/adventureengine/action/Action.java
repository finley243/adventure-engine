package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.data.MenuData;

/**
 * An action that an actor can take
 */
public interface Action {
	
	void choose(Actor subject);
	
	String getPrompt();
	
	float utility(Actor subject);
	
	boolean usesAction();
	
	boolean canRepeat();

	boolean isRepeatMatch(Action action);
	
	int actionCount();
	
	MenuData getMenuData();
	
}
