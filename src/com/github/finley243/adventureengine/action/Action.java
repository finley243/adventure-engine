package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.data.MenuData;

/**
 * An action that an actor can take
 */
public interface Action {
	
	public enum ActionLegality {
		LEGAL, ILLEGAL, HOSTILE
	}
	
	public void choose(Actor subject);
	
	public String getPrompt();
	
	public float utility(Actor subject);
	
	public boolean usesAction();
	
	public int actionCount();
	
	public ActionLegality getLegality();
	
	public MenuData getMenuData();
	
}
