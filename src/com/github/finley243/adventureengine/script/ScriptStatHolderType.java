package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.Room;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class ScriptStatHolderType extends Script {

    @Override
    public ScriptReturnData execute(Context context) {
        Expression statHolderExpression = context.getLocalVariables().get("holder").getExpression();
        if (statHolderExpression.getDataType() != Expression.DataType.STAT_HOLDER) return new ScriptReturnData(null, null, "Holder parameter is not a stat holder");
        StatHolder statHolder = statHolderExpression.getValueStatHolder();
        String typeString = switch (statHolder) {
            case Actor ignored -> "actor";
            case WorldObject ignored -> "object";
            case Item ignored -> "item";
            case ItemTemplate ignored -> "item_template";
            case Area ignored -> "area";
            case Room ignored -> "room";
            case Scene ignored -> "scene";
            case null, default -> null;
        };
        if (typeString == null) return new ScriptReturnData(null, null, "Holder parameter is not a recognized type of StatHolder");
        return new ScriptReturnData(Expression.constant(typeString), FlowStatementType.RETURN, null);
    }

}
