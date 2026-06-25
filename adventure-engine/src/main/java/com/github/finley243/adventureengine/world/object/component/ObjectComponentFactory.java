package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.item.ItemFactory;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.*;

public class ObjectComponentFactory {

    private final ItemFactory itemFactory;

    public ObjectComponentFactory(ItemFactory itemFactory) {
        this.itemFactory = itemFactory;
    }

    public ObjectComponent create(ObjectComponentTemplate template, WorldObject object) {
        return switch (template) {
            case InventoryObjectComponentTemplate ignored -> new InventoryObjectComponent(object, template, itemFactory);
            case NetworkObjectComponentTemplate ignored -> new NetworkObjectComponent(object, template);
            case LinkObjectComponentTemplate ignored -> new LinkObjectComponent(object, template);
            case UsableObjectComponentTemplate ignored -> new UsableObjectComponent(object, template);
            case VehicleObjectComponentTemplate ignored -> new VehicleObjectComponent(object, template);
            default -> throw new UnsupportedOperationException("Object template type is unsupported by ObjectComponentFactory");
        };
    }

    public static Class<? extends ObjectComponent> getClassFromName(String name) {
        return switch (name) {
            case "inventory" -> InventoryObjectComponent.class;
            case "link" -> LinkObjectComponent.class;
            case "network" -> NetworkObjectComponent.class;
            case "usable" -> UsableObjectComponent.class;
            case "vehicle" -> VehicleObjectComponent.class;
            case null, default -> null;
        };
    }

}
