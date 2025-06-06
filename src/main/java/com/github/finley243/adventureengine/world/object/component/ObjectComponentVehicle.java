package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.DebugLogger;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateVehicle;

import java.util.ArrayList;
import java.util.List;

public class ObjectComponentVehicle extends ObjectComponent {

    public ObjectComponentVehicle(WorldObject object, ObjectComponentTemplate template) {
        super(object, template);
    }

    private ObjectComponentTemplateVehicle getTemplateVehicle() {
        return (ObjectComponentTemplateVehicle) getTemplate();
    }

    public WorldObject getObjectOverride() {
        Expression objectOverrideExpression = getObject().getLocalVariable("vehicle_object_override");
        if (objectOverrideExpression == null) return null;
        if (objectOverrideExpression.getDataType() != Expression.DataType.STRING) {
            DebugLogger.print("ObjectComponentVehicle " + getObject() + " - object override local variable is not a string");
            return null;
        }
        String objectOverrideID = objectOverrideExpression.getValueString();
        return getObject().game().data().getObject(objectOverrideID);
    }

    @Override
    protected List<Action> getPossibleActions(Actor subject) {
        List<Action> actions = new ArrayList<>();
        WorldObject objectOverride = getObjectOverride();
        if (objectOverride != null) {
            actions.addAll(objectOverride.getArea().getMoveActions(subject, getTemplateVehicle().getVehicleType(), objectOverride));
        } else {
            actions.addAll(getObject().getArea().getMoveActions(subject, getTemplateVehicle().getVehicleType(), getObject()));
        }
        return actions;
    }

    @Override
    protected String getStatName() {
        return "vehicle";
    }

}

