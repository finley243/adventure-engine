package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;

public interface Action {
	
	public enum ActionLegality {
		LEGAL, ILLEGAL, HOSTILE
	}
	
	public void choose(Actor subject);
	
	public String getPrompt();
	
	public float utility(Actor subject);
	
	public int actionPoints();
	
	public String[] getMenuStructure();
	
	public ActionLegality getLegality();
	
}
