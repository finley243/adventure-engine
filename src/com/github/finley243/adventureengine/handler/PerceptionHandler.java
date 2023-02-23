package com.github.finley243.adventureengine.handler;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.Room;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.component.ObjectComponentLink;
import com.google.common.eventbus.Subscribe;

import java.util.HashSet;
import java.util.Set;

public class PerceptionHandler {

	@Subscribe
	public void onSensoryEvent(SensoryEvent e) {
		Set<Actor> actors = new HashSet<>();
		Set<Room> visitedRooms = new HashSet<>();
		// TODO - Adjust to account for direct links between areas in different rooms
		for (Area origin : e.getOrigins()) {
			if (!visitedRooms.contains(origin.getRoom())) {
				actors.addAll(origin.getRoom().getActors());
				if (e.isLoud()) {
					for (WorldObject areaObject : origin.getRoom().getObjects()) {
						for (ObjectComponentLink linkComponent : areaObject.getLinkComponents()) {
							actors.addAll(linkComponent.getLinkedObject().getArea().getRoom().getActors());
						}
					}
				}
				visitedRooms.add(origin.getRoom());
			}
		}
		for (Actor actor : actors) {
			boolean actorCanSeeEvent = false;
			for (Area origin : e.getOrigins()) {
				if (actor.getVisibleAreas().contains(origin)) {
					actorCanSeeEvent = true;
					break;
				}
			}
			actor.onSensoryEvent(e, actorCanSeeEvent);
		}
	}
	
}
