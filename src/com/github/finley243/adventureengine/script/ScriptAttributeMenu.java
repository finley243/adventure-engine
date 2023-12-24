package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.event.AttributeMenuEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.StatHolderReference;

public class ScriptAttributeMenu extends Script {

    private final StatHolderReference actorReference;
    private final Expression points;

    public ScriptAttributeMenu(Condition condition, StatHolderReference actorReference, Expression points) {
        super(condition);
        if (points == null) throw new IllegalArgumentException("Points expression is null");
        this.actorReference = actorReference;
        this.points = points;
    }

    @Override
    protected void executeSuccess(Context context) {
        if (points.getDataType(context) != Expression.DataType.INTEGER) throw new IllegalArgumentException("Points expression is not an integer");
        if (actorReference.getHolder(context) instanceof Actor actor) {
            int pointsValue = points.getValueInteger(context);
            context.game().eventQueue().addToFront(new AttributeMenuEvent(actor, pointsValue));
        }
    }

}
