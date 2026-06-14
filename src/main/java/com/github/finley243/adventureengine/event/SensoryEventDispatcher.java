package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.event.ui.RenderGeneratedTextEvent;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.textgen.TextContext;
import com.github.finley243.adventureengine.textgen.TextGen;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.obstruction.ObstructionType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SensoryEventDispatcher {

    private final Pathfinder pathfinder;
    private final TextGen textGen;
    private final UIEventBus eventBus;

    public SensoryEventDispatcher(Pathfinder pathfinder, TextGen textGen, UIEventBus eventBus) {
        this.pathfinder = pathfinder;
        this.textGen = textGen;
        this.eventBus = eventBus;
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

        String textVisible = event.getLineVisible();
        String textAudible = event.getLineAudible();

        for (Map.Entry<Actor, Map<Area, Pathfinder.VisibleAreaData>> actorEntry : reverseActorMap.entrySet()) {
            Actor actor = actorEntry.getKey();
            boolean actorCanSeeEvent = true;
            if (event.getContext().getSubject() != null) {
                actorCanSeeEvent = event.getContext().getSubject().isVisible(actor);
            }
            if (actorCanSeeEvent) {
                Set<ObstructionType> bypassedObstructions = actor.getAllBypassedObstructionTypes();
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
            processEventOnActor(actor, event, actorCanSeeEvent, textVisible, textAudible);
        }
    }

    private void processEventOnActor(Actor actor, SensoryEvent event, boolean visible, String textVisible, String textAudible) {
        if (!actor.isActive() || !actor.isEnabled()) return;
        if (actor.isPlayer()) {
            String text = null;
            if (visible) {
                text = textVisible;
            }
            if (text == null) {
                text = textAudible;
            }
            if (text != null) {
                eventBus.post(new RenderGeneratedTextEvent(text, event.getContext(), event.getContext().generateTextContext()));
            }
        } else {
            if (event.getContext().getSubject().equals(actor)) return;
            if (visible) { // Visible
                if (event.isAction()) {
                    actor.getTargetingComponent().onVisibleAction(event.getAction(), event.getContext().getSubject());
                } else if (event.isBark()) {
                    actor.getTargetingComponent().onAudibleBark(event.getBark(), event.getContext().getSubject(), event.getContext().getTarget(), true);
                }
            } else { // Audible
                if (event.isBark()) {
                    actor.getTargetingComponent().onAudibleBark(event.getBark(), event.getContext().getSubject(), event.getContext().getTarget(), false);
                }
            }
        }
    }

}
