package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.*;

public class ObjectComponentFactory {

    public static ObjectComponent create(Game game, ObjectComponentTemplate template, WorldObject object) {
        return switch (template) {
            case ObjectComponentTemplateInventory ignored -> new ObjectComponentInventory(game, object, template);
            case ObjectComponentTemplateNetwork ignored -> new ObjectComponentNetwork(game, object, template);
            case ObjectComponentTemplateLink ignored -> new ObjectComponentLink(game, object, template);
            case ObjectComponentTemplateUsable ignored -> new ObjectComponentUsable(game, object, template);
            case ObjectComponentTemplateVehicle ignored -> new ObjectComponentVehicle(game, object, template);
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
