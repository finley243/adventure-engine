package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.actor.ai.behavior.Behavior;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.List;

public class ActorFactory {
	
	public static Actor create(String ID, String nameDescriptor, Area area, String template, List<Behavior> behaviors, boolean startDead, boolean startDisabled) {
		return new Actor(ID, nameDescriptor, area, template, behaviors, startDead, startDisabled, false);
	}
	
	public static Actor createPlayer(String ID, Area area, String template) {
		return new Actor(ID, null, area, template, null, false, false, true);
	}
	
}
