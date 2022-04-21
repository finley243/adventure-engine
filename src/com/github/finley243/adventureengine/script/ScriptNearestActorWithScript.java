package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;

import java.util.ArrayList;
import java.util.List;

public class ScriptNearestActorWithScript extends Script {

    private final String trigger;

    public ScriptNearestActorWithScript(Condition condition, String trigger) {
        super(condition);
        this.trigger = trigger;
    }

    @Override
    protected void executeSuccess(Actor subject) {
        // TODO - Improve efficiency of nearest actor check
        List<Actor> nearestActor = new ArrayList<>();
        List<Integer> nearestActorDist = new ArrayList<>();
        for(Actor visibleActor : subject.game().data().getPlayer().getVisibleActors()) {
            int distance = visibleActor.getArea().getDistanceTo(subject.game().data().getPlayer().getArea().getID());
            int addAtIndex = nearestActor.size();
            for(int i = 0; i < nearestActor.size(); i++) {
                if(distance <= nearestActorDist.get(i)) {
                    addAtIndex = i;
                    break;
                }
            }
            nearestActor.add(addAtIndex, visibleActor);
            nearestActorDist.add(addAtIndex, distance);
        }
        for(Actor nearActor : nearestActor) {
            boolean executed = nearActor.triggerScript(trigger);
            if(executed) {
                break;
            }
        }
    }

}
