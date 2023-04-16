package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.variable.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScriptNearestActorWithScript extends Script {

    private final String trigger;

    public ScriptNearestActorWithScript(Condition condition, Map<String, Variable> localParameters, String trigger) {
        super(condition, localParameters);
        this.trigger = trigger;
    }

    @Override
    protected void executeSuccess(ContextScript context) {
        // TODO - Improve efficiency of nearest actor check
        List<Actor> nearestActor = new ArrayList<>();
        List<Integer> nearestActorDist = new ArrayList<>();
        for (Actor visibleActor : context.game().data().getPlayer().getVisibleActors()) {
            // TODO - Replace with dedicated pathfinding function
            //int distance = visibleActor.getArea().getDistanceTo(subject.game().data().getPlayer().getArea().getID());
            int distance = Pathfinder.findPath(visibleActor.getArea(), context.game().data().getPlayer().getArea()).size() - 1;
            int addAtIndex = nearestActor.size();
            for (int i = 0; i < nearestActor.size(); i++) {
                if (distance <= nearestActorDist.get(i)) {
                    addAtIndex = i;
                    break;
                }
            }
            nearestActor.add(addAtIndex, visibleActor);
            nearestActorDist.add(addAtIndex, distance);
        }
        for (Actor nearActor : nearestActor) {
            boolean executed = nearActor.triggerScript(trigger, context.getTarget());
            if (executed) break;
        }
    }

}
