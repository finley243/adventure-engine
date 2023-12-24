package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.stat.StatHolderReference;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.Room;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class ExpressionStatHolderType extends Expression {

    private final StatHolderReference statHolderReference;

    public ExpressionStatHolderType(StatHolderReference statHolderReference) {
        this.statHolderReference = statHolderReference;
    }

    @Override
    public DataType getDataType(Context context) {
        return DataType.STRING;
    }

    @Override
    public String getValueString(Context context) {
        StatHolder statHolder = statHolderReference.getHolder(context);
        return switch (statHolder) {
            case Actor ignored -> "actor";
            case WorldObject ignored -> "object";
            case Item ignored -> "item";
            case ItemTemplate ignored -> "item_template";
            case Area ignored -> "area";
            case Room ignored -> "room";
            case Scene ignored -> "scene";
            case null, default -> null;
        };
    }

}
