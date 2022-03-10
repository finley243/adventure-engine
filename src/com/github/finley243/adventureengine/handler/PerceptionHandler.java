package com.github.finley243.adventureengine.handler;

import java.util.HashSet;
import java.util.Set;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SoundEvent;
import com.github.finley243.adventureengine.event.AudioVisualEvent;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.ObjectExit;
import com.google.common.eventbus.Subscribe;

public class PerceptionHandler {

	@Subscribe
	public void onVisualEvent(AudioVisualEvent e) {
		Set<Actor> roomActors = new HashSet<>();
		for(Area origin : e.getOrigins()) {
			roomActors.addAll(origin.getRoom().getActors());
		}
		for(Actor actor : roomActors) {
			if(e.getSubject() != null) {
				if(actor.canSee(e.getSubject())) {
					actor.onVisualEvent(e);
				}
			} else {
				for(Area origin : e.getOrigins()) {
					if (actor.getArea().getVisibleAreas(actor).contains(origin)) {
						actor.onVisualEvent(e);
						break;
					}
				}
			}
		}
	}
	
	@Subscribe
	public void onSoundEvent(SoundEvent e) {
		Set<Actor> audibleActors = e.getOrigin().getRoom().getActors();
		if(e.isLoud()) {
			for(Object areaObject : e.getOrigin().getRoom().getObjects()) {
				if(areaObject instanceof ObjectExit) {
					audibleActors.addAll(((ObjectExit) areaObject).getLinkedArea().getRoom().getActors());
				}
			}
		}
		for(Actor actor : audibleActors) {
			actor.onSoundEvent(e);
		}
	}
	
}
