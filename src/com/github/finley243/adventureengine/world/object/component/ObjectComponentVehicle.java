package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateVehicle;

import java.util.ArrayList;
import java.util.List;

public class ObjectComponentVehicle extends ObjectComponent {

    public ObjectComponentVehicle(String ID, WorldObject object) {
        super(ID, object);
    }

    @Override
    public ObjectComponentTemplate getTemplate() {
        return getTemplateVehicle();
    }

    public ObjectComponentTemplateVehicle getTemplateVehicle() {
        return (ObjectComponentTemplateVehicle) getObject().getTemplate().getComponents().get(getID());
    }

    public WorldObject getObjectOverride() {
        if (getObject().getValueString(getID() + "_object_override", new Context(getObject().game(), getObject())) != null) {
            return getObject().game().data().getObject(getObject().getValueString(getID() + "_object_override", new Context(getObject().game(), getObject())));
        }
        return null;
    }

    @Override
    public List<Action> getActions(Actor subject) {
        List<Action> actions = new ArrayList<>();
        WorldObject objectOverride = getObjectOverride();
        actions.addAll(getObject().getArea().getMoveActions(subject, getTemplateVehicle().getVehicleType(), objectOverride == null ? getObject() : objectOverride, getTemplateVehicle().getMoveMenuName()));
        return actions;
    }

}
