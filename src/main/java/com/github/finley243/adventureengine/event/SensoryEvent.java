package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Bark;
import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.textgen.TextGen;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SensoryEvent {

	private final Area[] origins;
	private final String lineVisible;
	private final String lineAudible;
	private final Context context;
	private final Action action;
	private final Bark bark;
	private final boolean isDetectedBySelf;
	private final boolean isLoud;

	public SensoryEvent(Area origin, String lineVisible, Context context, boolean isDetectedBySelf, Action action, Bark bark) {
		this(new Area[]{origin}, lineVisible, null, context, isDetectedBySelf, false, action, bark);
	}

	public SensoryEvent(Area[] origins, String lineVisible, Context context, boolean isDetectedBySelf, Action action, Bark bark) {
		this(origins, lineVisible, null, context, isDetectedBySelf, false, action, bark);
	}

	public SensoryEvent(Area origin, String lineVisible, String lineAudible, Context context, boolean isDetectedBySelf, boolean isLoud, Action action, Bark bark) {
		this(new Area[]{origin}, lineVisible, lineAudible, context, isDetectedBySelf, isLoud, action, bark);
	}

	public SensoryEvent(Area[] origins, String lineVisible, String lineAudible, Context context, boolean isDetectedBySelf, boolean isLoud, Action action, Bark bark) {
		this.origins = origins;
		this.lineVisible = lineVisible;
		this.lineAudible = lineAudible;
		this.context = context;
		this.action = action;
		this.bark = bark;
		this.isDetectedBySelf = isDetectedBySelf;
		this.isLoud = isLoud;
	}
	
	public Area[] getOrigins() {
		return origins;
	}
	
	public String getTextVisible() {
		if (lineVisible == null) return null;
		return TextGen.generate(lineVisible, context, context.generateTextContext());
	}

	public String getTextAudible() {
		if (lineAudible == null) return null;
		return TextGen.generate(lineAudible, context, context.generateTextContext());
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

	public Context getContext() {
		return context;
	}

	public boolean isDetectedBySelf() {
		return isDetectedBySelf;
	}

	public boolean isLoud() {
		return isLoud;
	}

	public static void execute(Game game, SensoryEvent event) {
		Map<Area, Map<Area, Pathfinder.VisibleAreaData>> lineOfSightAreas = new HashMap<>(); // Key = origin, Value = line of sight areas
		for (Area origin : event.getOrigins()) {
			lineOfSightAreas.put(origin, Pathfinder.getLineOfSightAreas(game, origin, Set.of(), true));
		}

		Map<Actor, Map<Area, Pathfinder.VisibleAreaData>> reverseActorMap = new HashMap<>();
		for (Area origin : event.getOrigins()) {
			Map<Area, Pathfinder.VisibleAreaData> areaDataMap = lineOfSightAreas.get(origin);
			for (Map.Entry<Area, Pathfinder.VisibleAreaData> areaEntry : areaDataMap.entrySet()) {
				for (Actor actor : areaEntry.getKey().getActors()) {
					if (!event.isDetectedBySelf() && event.getContext().getSubject() != null && actor.equals(event.getContext().getSubject())) {
						continue;
					}
					if (! reverseActorMap.containsKey(actor)) {
						reverseActorMap.put(actor, new HashMap<>());
					}
					reverseActorMap.get(actor).put(origin, areaEntry.getValue());
				}
			}
		}

		for (Map.Entry<Actor, Map<Area, Pathfinder.VisibleAreaData>> actorEntry : reverseActorMap.entrySet()) {
			Actor actor = actorEntry.getKey();
			boolean actorCanSeeEvent = true;
			if (event.getContext().getSubject() != null) {
				actorCanSeeEvent = event.getContext().getSubject().isVisible(actor);
			}
			if (actorCanSeeEvent) {
				Set<String> bypassedObstructions = actor.getAllBypassedObstructionTypes(game);
				boolean hasVisibleOrigin = false;
				for (Map.Entry<Area, Pathfinder.VisibleAreaData> originEntry : actorEntry.getValue().entrySet()) {
					if (!originEntry.getKey().isVisible(actor)) continue;
					boolean pathObstructed = false;
					for (Area pathArea : originEntry.getValue().path()) {
						if (pathArea.hasUnbypassedObstruction(game, bypassedObstructions)) {
							pathObstructed = true;
							break;
						}
					}
					if (!pathObstructed) {
						hasVisibleOrigin = true;
						break;
					}
				}
				actorCanSeeEvent = hasVisibleOrigin;
			}
			// TODO - Add system for audible events (simple path length check?)
			actor.onSensoryEvent(game, event, actorCanSeeEvent);
		}
	}

}
