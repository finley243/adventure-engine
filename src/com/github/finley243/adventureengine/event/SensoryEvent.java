package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Bark;
import com.github.finley243.adventureengine.textgen.TextContext;
import com.github.finley243.adventureengine.textgen.TextGen;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.Room;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.component.ObjectComponentLink;

import java.util.HashSet;
import java.util.Set;

public class SensoryEvent implements QueuedEvent {

	private final Area[] origins;
	private final String lineVisible;
	private final String lineAudible;
	private final TextContext context;
	private final Action action;
	private final Bark bark;
	private final Actor subject;
	private final Actor target;
	private final boolean isLoud;

	public SensoryEvent(Area origin, String lineVisible, TextContext context, Action action, Bark bark, Actor subject, Actor target) {
		this(new Area[]{origin}, lineVisible, null, context, false, action, bark, subject, target);
	}

	public SensoryEvent(Area[] origins, String lineVisible, TextContext context, Action action, Bark bark, Actor subject, Actor target) {
		this(origins, lineVisible, null, context, false, action, bark, subject, target);
	}

	public SensoryEvent(Area origin, String lineVisible, String lineAudible, TextContext context, boolean isLoud, Action action, Bark bark, Actor subject, Actor target) {
		this(new Area[]{origin}, lineVisible, lineAudible, context, isLoud, action, bark, subject, target);
	}

	public SensoryEvent(Area[] origins, String lineVisible, String lineAudible, TextContext context, boolean isLoud, Action action, Bark bark, Actor subject, Actor target) {
		this.origins = origins;
		this.lineVisible = lineVisible;
		this.lineAudible = lineAudible;
		this.context = context;
		this.action = action;
		this.bark = bark;
		this.subject = subject;
		this.target = target;
		this.isLoud = isLoud;
	}
	
	public Area[] getOrigins() {
		return origins;
	}
	
	public String getTextVisible() {
		if (lineVisible == null) return null;
		return TextGen.generate(lineVisible, context);
	}

	public String getTextAudible() {
		if (lineAudible == null) return null;
		return TextGen.generate(lineAudible, context);
	}

	public Action getAction() {
		return action;
	}

	public Bark getBark() {
		return bark;
	}

	public boolean isAction() {
		return action != null;
	}

	public boolean isBark() {
		return bark != null;
	}

	public Actor getSubject() {
		return subject;
	}

	public Actor getTarget() {
		return target;
	}

	public boolean isLoud() {
		return isLoud;
	}

	@Override
	public void execute(Game game) {
		Set<Actor> actors = new HashSet<>();
		Set<Room> visitedRooms = new HashSet<>();
		// TODO - Adjust to account for direct links between areas in different rooms
		for (Area origin : getOrigins()) {
			if (!visitedRooms.contains(origin.getRoom())) {
				actors.addAll(origin.getRoom().getActors());
				if (isLoud()) {
					for (WorldObject areaObject : origin.getRoom().getObjects()) {
						ObjectComponentLink linkComponent = areaObject.getComponentOfType(ObjectComponentLink.class);
						if (linkComponent == null) continue;
						for (Area audibleArea : linkComponent.getLinkedAreasAudible()) {
							actors.addAll(audibleArea.getRoom().getActors());
						}
					}
				}
				visitedRooms.add(origin.getRoom());
			}
		}
		for (Actor actor : actors) {
			boolean actorCanSeeEvent = false;
			for (Area origin : getOrigins()) {
				if (actor.getVisibleAreas().contains(origin)) {
					actorCanSeeEvent = true;
					break;
				}
			}
			actor.onSensoryEvent(this, actorCanSeeEvent);
		}
		game.eventQueue().executeNext();
	}

}
