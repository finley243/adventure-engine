package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.Context;
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
        Context context = new Context(getObject().game(), getObject());
        Expression objectOverrideExpression = getObject().getStatValue("vehicle_object_override", context);
        if (objectOverrideExpression == null) return null;
        if (objectOverrideExpression.getDataType(context) != Expression.DataType.STRING) {
            getObject().game().log().print("ObjectComponentVehicle " + getObject() + " - object override local variable is not a string");
            return null;
        }
        String objectOverrideID = objectOverrideExpression.getValueString(context);
        return getObject().game().data().getObject(objectOverrideID);
    }

    @Override
    public List<Action> getActions(Actor subject) {
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
