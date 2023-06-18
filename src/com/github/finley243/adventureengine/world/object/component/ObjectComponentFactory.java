package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.*;

public class ObjectComponentFactory {

    public static ObjectComponent create(ObjectComponentTemplate template, String ID, WorldObject object) {
        return switch (template) {
            case ObjectComponentTemplateInventory ignored -> new ObjectComponentInventory(ID, object, template.getID());
            case ObjectComponentTemplateNetwork ignored -> new ObjectComponentNetwork(ID, object, template.getID());
            case ObjectComponentTemplateLink ignored -> new ObjectComponentLink(ID, object, template.getID());
            case ObjectComponentTemplateUsable ignored -> new ObjectComponentUsable(ID, object, template.getID());
            case ObjectComponentTemplateVehicle ignored -> new ObjectComponentVehicle(ID, object, template.getID());
            default -> null;
        };
    }

}
