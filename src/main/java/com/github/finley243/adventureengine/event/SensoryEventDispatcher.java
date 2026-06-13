package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.textgen.TextContext;
import com.github.finley243.adventureengine.textgen.TextGen;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SensoryEventDispatcher {

    private final Pathfinder pathfinder;
    private final TextGen textGen;

    public SensoryEventDispatcher(Pathfinder pathfinder, TextGen textGen) {
        this.pathfinder = pathfinder;
        this.textGen = textGen;
    }

    public void dispatch(SensoryEvent event) {
        Map<Area, Map<Area, Pathfinder.VisibleAreaData>> lineOfSightAreas = new HashMap<>(); // Key = origin, Value = line of sight areas
        for (Area origin : event.getOrigins()) {
            lineOfSightAreas.put(origin, pathfinder.getLineOfSightAreas(origin, Set.of(), true));
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

        if (reverseActorMap.isEmpty()) return;

        TextContext textContext = event.getContext().generateTextContext();
        String textVisible = textGen.generate(event.getLineVisible(), event.getContext(), textContext);
        String textAudible = textGen.generate(event.getLineAudible(), event.getContext(), textContext);

        for (Map.Entry<Actor, Map<Area, Pathfinder.VisibleAreaData>> actorEntry : reverseActorMap.entrySet()) {
            Actor actor = actorEntry.getKey();
            boolean actorCanSeeEvent = true;
            if (event.getContext().getSubject() != null) {
                actorCanSeeEvent = event.getContext().getSubject().isVisible(actor);
            }
            if (actorCanSeeEvent) {
                Set<String> bypassedObstructions = actor.getAllBypassedObstructionTypes();
                boolean hasVisibleOrigin = false;
                for (Map.Entry<Area, Pathfinder.VisibleAreaData> originEntry : actorEntry.getValue().entrySet()) {
                    if (!originEntry.getKey().isVisible(actor)) continue;
                    boolean pathObstructed = false;
                    for (Area pathArea : originEntry.getValue().path()) {
                        if (pathArea.hasUnbypassedObstruction(bypassedObstructions)) {
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
            actor.onSensoryEvent(event, actorCanSeeEvent, textVisible, textAudible);
        }
    }

}
