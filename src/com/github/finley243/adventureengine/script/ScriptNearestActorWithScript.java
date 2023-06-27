package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScriptNearestActorWithScript extends Script {

    private final Expression trigger;

    public ScriptNearestActorWithScript(Condition condition, Map<String, Expression> localParameters, Expression trigger) {
        super(condition, localParameters);
        if (trigger == null) throw new IllegalArgumentException("ScriptNearestActorWithScript trigger is null");
        if (trigger.getDataType() != Expression.DataType.STRING) throw new IllegalArgumentException("ScriptNearestActorWithScript trigger is not a string");
        this.trigger = trigger;
    }

    @Override
    protected void executeSuccess(Context context) {
        // TODO - Improve efficiency of nearest actor check
        List<Actor> nearestActor = new ArrayList<>();
        List<Integer> nearestActorDist = new ArrayList<>();
        for (Actor visibleActor : context.game().data().getPlayer().getVisibleActors()) {
            // TODO - Replace with dedicated pathfinding function
            int distance = Pathfinder.findPath(visibleActor.getArea(), context.game().data().getPlayer().getArea(), null).size() - 1;
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
        String triggerValue = trigger.getValueString(context);
        for (Actor nearActor : nearestActor) {
            boolean executed = nearActor.triggerScript(triggerValue, context.getTarget());
            if (executed) break;
        }
    }

}
