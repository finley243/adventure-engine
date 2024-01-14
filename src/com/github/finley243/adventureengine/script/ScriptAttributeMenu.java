package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.AttributeMenuEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.StatHolderReference;

public class ScriptAttributeMenu extends Script {

    private final StatHolderReference actorReference;
    private final Expression points;

    public ScriptAttributeMenu(StatHolderReference actorReference, Expression points) {
        if (points == null) throw new IllegalArgumentException("Points expression is null");
        this.actorReference = actorReference;
        this.points = points;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        if (points.getDataType(context) != Expression.DataType.INTEGER) throw new IllegalArgumentException("Points expression is not an integer");
        if (!(actorReference.getHolder(context) instanceof Actor actor)) throw new IllegalArgumentException("Actor reference is not a valid actor");
        int pointsValue = points.getValueInteger(context);
        AttributeMenuEvent menuEvent = new AttributeMenuEvent(actor, pointsValue);
        context.game().menuManager().attributeMenu(menuEvent, context.game(), actor, pointsValue);
        return new ScriptReturnData(null, false, false, null);
    }

}
