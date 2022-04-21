package com.github.finley243.adventureengine.handler;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.Room;
import com.github.finley243.adventureengine.world.object.ObjectDoor;
import com.google.common.eventbus.Subscribe;

import java.util.HashSet;
import java.util.Set;

public class PerceptionHandler {

	@Subscribe
	public void onSensoryEvent(SensoryEvent e) {
		Set<Actor> actors = new HashSet<>();
		Set<Room> visitedRooms = new HashSet<>();
		for (Area origin : e.getOrigins()) {
			if (!visitedRooms.contains(origin.getRoom())) {
				actors.addAll(origin.getRoom().getActors());
				if (e.isLoud()) {
					for (Object areaObject : origin.getRoom().getObjects()) {
						if (areaObject instanceof ObjectDoor) {
							actors.addAll(((ObjectDoor) areaObject).getLinkedArea().getRoom().getActors());
						}
					}
				}
				visitedRooms.add(origin.getRoom());
			}
		}
		for (Actor actor : actors) {
			boolean actorCanSeeEvent = false;
			for (Area origin : e.getOrigins()) {
				if (actor.getArea().getVisibleAreas(actor).contains(origin)) {
					actorCanSeeEvent = true;
					break;
				}
			}
			// TODO - Test non-visible event functionality
			actor.onSensoryEvent(e, actorCanSeeEvent);
		}
	}
	
}
