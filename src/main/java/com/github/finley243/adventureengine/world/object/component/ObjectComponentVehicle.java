package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateVehicle;

import java.util.ArrayList;
import java.util.List;

public class ObjectComponentVehicle extends ObjectComponent {

    private WorldObject objectOverride;

    ObjectComponentVehicle(WorldObject object, ObjectComponentTemplate template) {
        super(object, template);
    }

    private ObjectComponentTemplateVehicle getTemplateVehicle() {
        return (ObjectComponentTemplateVehicle) getTemplate();
    }

    public void resolveObjectOverride(WorldObject objectOverride) {
        if (this.objectOverride != null) throw new IllegalStateException("Object override is already set");
        this.objectOverride = objectOverride;
    }

    @Override
    protected List<Action> getPossibleActions(Actor subject, ScriptRuntime scriptRuntime) {
        List<Action> actions = new ArrayList<>();
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

