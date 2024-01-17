package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Bark;
import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.textgen.TextContext;
import com.github.finley243.adventureengine.textgen.TextGen;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.Room;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.component.ObjectComponentLink;
import org.checkerframework.checker.units.qual.A;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SensoryEvent {

	private final Area[] origins;
	private final String lineVisible;
	private final String lineAudible;
	private final TextContext context;
	private final Action action;
	private final Bark bark;
	private final Actor subject;
	private final Actor target;
	private final boolean isDetectedBySelf;
	private final boolean isLoud;

	public SensoryEvent(Area origin, String lineVisible, TextContext context, boolean isDetectedBySelf, Action action, Bark bark, Actor subject, Actor target) {
		this(new Area[]{origin}, lineVisible, null, context, isDetectedBySelf, false, action, bark, subject, target);
	}

	public SensoryEvent(Area[] origins, String lineVisible, TextContext context, boolean isDetectedBySelf, Action action, Bark bark, Actor subject, Actor target) {
		this(origins, lineVisible, null, context, isDetectedBySelf, false, action, bark, subject, target);
	}

	public SensoryEvent(Area origin, String lineVisible, String lineAudible, TextContext context, boolean isDetectedBySelf, boolean isLoud, Action action, Bark bark, Actor subject, Actor target) {
		this(new Area[]{origin}, lineVisible, lineAudible, context, isDetectedBySelf, isLoud, action, bark, subject, target);
	}

	public SensoryEvent(Area[] origins, String lineVisible, String lineAudible, TextContext context, boolean isDetectedBySelf, boolean isLoud, Action action, Bark bark, Actor subject, Actor target) {
		this.origins = origins;
		this.lineVisible = lineVisible;
		this.lineAudible = lineAudible;
		this.context = context;
		this.action = action;
		this.bark = bark;
		this.subject = subject;
		this.target = target;
		this.isDetectedBySelf = isDetectedBySelf;
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

	public boolean isDetectedBySelf() {
		return isDetectedBySelf;
	}

	public boolean isLoud() {
		return isLoud;
	}

	public static void execute(Game game, SensoryEvent event) {
		Map<Area, Set<Area>> lineOfSightAreas = new HashMap<>(); // Key = origin, Value = line of sight areas
		for (Area origin : event.getOrigins()) {
			lineOfSightAreas.put(origin, Pathfinder.getLineOfSightAreas(origin).keySet());
		}
		Map<Area, Set<Actor>> lineOfSightActors = new HashMap<>();
		for (Area origin : event.getOrigins()) {
			Set<Area> areas = lineOfSightAreas.get(origin);
			Set<Actor> originActors = new HashSet<>();
			for (Area area : areas) {
				originActors.addAll(area.getActors());
			}
			if (!event.isDetectedBySelf && event.subject != null) {
				originActors.remove(event.subject);
			}
			lineOfSightActors.put(origin, originActors);
		}
		Map<Actor, ActorSenseData> reverseActorMap = new HashMap<>();
		for (Map.Entry<Area, Set<Actor>> entry : lineOfSightActors.entrySet()) {
			for (Actor actor : entry.getValue()) {
				if (!reverseActorMap.containsKey(actor)) {
					reverseActorMap.put(actor, new ActorSenseData());
				}
				reverseActorMap.get(actor).lineOfSightOrigins.add(entry.getKey());
			}
		}
		// TODO - Add system for audible events (simple path length check?)
		for (Actor actor : reverseActorMap.keySet()) {
			boolean actorCanSeeEvent = true;
			if (event.getSubject() != null) {
				actorCanSeeEvent = event.getSubject().isVisible(actor);
			}
			if (actorCanSeeEvent) {
				boolean hasVisibleOrigin = false;
				for (Area origin : reverseActorMap.get(actor).lineOfSightOrigins) {
					if (origin.isVisible(actor)) {
						hasVisibleOrigin = true;
						break;
					}
				}
				actorCanSeeEvent = hasVisibleOrigin;
			}
			actor.onSensoryEvent(event, actorCanSeeEvent);
		}
	}

	private static class ActorSenseData {
		public final Set<Area> lineOfSightOrigins;

		public ActorSenseData() {
			this.lineOfSightOrigins = new HashSet<>();
		}
	}

}
