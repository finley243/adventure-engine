package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionDependencies;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.VehicleObjectComponentTemplate;

import java.util.ArrayList;
import java.util.List;

public class VehicleObjectComponent extends ObjectComponent {

    private WorldObject objectOverride;

    VehicleObjectComponent(WorldObject object, ObjectComponentTemplate template) {
        super(object, template);
    }

    private VehicleObjectComponentTemplate getTemplateVehicle() {
        return (VehicleObjectComponentTemplate) getTemplate();
    }

    public void resolveObjectOverride(WorldObject objectOverride) {
        if (this.objectOverride != null) throw new IllegalStateException("Object override is already set");
        this.objectOverride = objectOverride;
    }

    @Override
    protected List<Action> getPossibleActions(Actor subject, ActionDependencies dependencies) {
        List<Action> actions = new ArrayList<>();
        if (objectOverride != null) {
            actions.addAll(objectOverride.getArea().getMoveActions(subject, dependencies, getTemplateVehicle().getVehicleType(), objectOverride));
        } else {
            actions.addAll(getObject().getArea().getMoveActions(subject, dependencies, getTemplateVehicle().getVehicleType(), getObject()));
        }
        return actions;
    }

    @Override
    protected String getStatName() {
        return "vehicle";
    }

}

