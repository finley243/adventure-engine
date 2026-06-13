package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.world.environment.Area;

public class ActorFactory {
	
	public static Actor createPlayer(String ID, Area area, String template) {
		return new Actor(ID, null, area, template, null, false, false, true);
	}
	
}
