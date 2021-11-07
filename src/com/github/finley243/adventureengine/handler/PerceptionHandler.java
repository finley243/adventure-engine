package com.github.finley243.adventureengine.handler;

import java.util.HashSet;
import java.util.Set;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SoundEvent;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.world.environment.Area;
import com.google.common.eventbus.Subscribe;

public class PerceptionHandler {

	public PerceptionHandler() {}
	
	@Subscribe
	public void onVisualEvent(VisualEvent event) {
		Set<Area> visibleFromAreas = event.getOrigin().getRoom().getAreas();
		Set<Actor> visibleActors = new HashSet<>();
		for(Area area : visibleFromAreas) {
			visibleActors.addAll(area.getActors());
		}
		for(Actor actor : visibleActors) {
			actor.onVisualEvent(event);
		}
	}
	
	@Subscribe
	public void onSoundEvent(SoundEvent event) {
		
	}
	
}
