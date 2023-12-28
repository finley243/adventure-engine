package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.event.ScriptResumeEvent;
import com.github.finley243.adventureengine.event.SkillMenuEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.StatHolderReference;

public class ScriptSkillMenu extends Script {

    private final StatHolderReference actorReference;
    private final Expression points;

    public ScriptSkillMenu(Condition condition, StatHolderReference actorReference, Expression points) {
        super(condition);
        if (points == null) throw new IllegalArgumentException("Points expression is null");
        this.actorReference = actorReference;
        this.points = points;
    }

    @Override
    protected void executeSuccess(RuntimeStack runtimeStack) {
        Context context = runtimeStack.getContext();
        if (points.getDataType(context) != Expression.DataType.INTEGER) throw new IllegalArgumentException("Points expression is not an integer");
        if (!(actorReference.getHolder(context) instanceof Actor actor)) throw new IllegalArgumentException("Actor reference is not a valid actor");
        int pointsValue = points.getValueInteger(context);
        //context.game().eventQueue().addToFront(new SkillMenuEvent(actor, pointsValue));
        //context.game().menuManager().skillMenu(this, context.game(), actor, pointsValue);
        context.game().eventQueue().addToFront(new ScriptResumeEvent(runtimeStack, new ScriptReturn(null, false, false, null)));
        context.game().eventQueue().addToFront(new SkillMenuEvent(actor, pointsValue));
        context.game().eventQueue().startExecution();
    }

    /*@Override
    public void onNumericMenuInput(Map<String, Integer> changedValues) {
        Actor actor = (Actor) actorReference.getHolder(context);
        for (Map.Entry<String, Integer> entry : changedValues.entrySet()) {
            actor.setSkillBase(entry.getKey(), entry.getValue());
        }
        sendReturn(, new ScriptReturn(null, false, false, null));
    }*/

}
