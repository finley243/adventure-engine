package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.*;

public class ObjectComponentFactory {

    public static ObjectComponent create(ObjectComponentTemplate template, String ID, WorldObject object) {
        return switch (template) {
            case ObjectComponentTemplateInventory ignored -> new ObjectComponentInventory(ID, object, template);
            case ObjectComponentTemplateNetwork ignored -> new ObjectComponentNetwork(ID, object, template);
            case ObjectComponentTemplateLink ignored -> new ObjectComponentLink(ID, object, template);
            case ObjectComponentTemplateUsable ignored -> new ObjectComponentUsable(ID, object, template);
            case ObjectComponentTemplateVehicle ignored -> new ObjectComponentVehicle(ID, object, template);
            default -> null;
        };
    }

}
