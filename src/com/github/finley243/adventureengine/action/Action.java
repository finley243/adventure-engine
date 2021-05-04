package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;

public interface Action {
	
	public void choose(Actor subject);
	
	public String getChoiceName();
	
	public float utility(Actor subject);
	
}
