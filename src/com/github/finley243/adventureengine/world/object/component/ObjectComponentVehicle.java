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

    @Override
    public List<Action> getActions(Actor subject) {
        List<Action> actions = new ArrayList<>();
        actions.addAll(getObject().getArea().getMoveActions(getTemplateVehicle().getVehicleType(), getObject()));
        return actions;
    }

}
