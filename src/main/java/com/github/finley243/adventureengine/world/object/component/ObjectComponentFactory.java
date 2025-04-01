package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.*;

public class ObjectComponentFactory {

    public static ObjectComponent create(ObjectComponentTemplate template, WorldObject object) {
        return switch (template) {
            case ObjectComponentTemplateInventory ignored -> new ObjectComponentInventory(object, template);
            case ObjectComponentTemplateNetwork ignored -> new ObjectComponentNetwork(object, template);
            case ObjectComponentTemplateLink ignored -> new ObjectComponentLink(object, template);
            case ObjectComponentTemplateUsable ignored -> new ObjectComponentUsable(object, template);
            case ObjectComponentTemplateVehicle ignored -> new ObjectComponentVehicle(object, template);
            default -> null;
        };
    }

    public static Class<? extends ObjectComponent> getClassFromName(String name) {
        return switch (name) {
            case "inventory" -> ObjectComponentInventory.class;
            case "link" -> ObjectComponentLink.class;
            case "network" -> ObjectComponentNetwork.class;
            case "usable" -> ObjectComponentUsable.class;
            case "vehicle" -> ObjectComponentVehicle.class;
            case null, default -> null;
        };
    }

}
