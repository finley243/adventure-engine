package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateVehicle;

import java.util.ArrayList;
import java.util.List;

public class ObjectComponentVehicle extends ObjectComponent {

    private final String templateID;

    public ObjectComponentVehicle(String ID, WorldObject object, String templateID) {
        super(ID, object);
        this.templateID = templateID;
    }

    @Override
    public ObjectComponentTemplate getTemplate() {
        return getTemplateVehicle();
    }

    public ObjectComponentTemplateVehicle getTemplateVehicle() {
        return (ObjectComponentTemplateVehicle) getObject().game().data().getObjectComponentTemplate(templateID);
    }

    public WorldObject getObjectOverride() {
        if (getObject().getValueString(getID() + "_object_override") != null) {
            return getObject().game().data().getObject(getObject().getValueString(getID() + "_object_override"));
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
