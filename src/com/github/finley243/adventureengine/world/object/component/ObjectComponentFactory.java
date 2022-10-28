package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateInventory;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateNetwork;

public class ObjectComponentFactory {

    public static ObjectComponent create(ObjectComponentTemplate template, String ID, WorldObject object, boolean startEnabled) {
        if (template instanceof ObjectComponentTemplateInventory) {
            return new ObjectComponentInventory(ID, object, startEnabled, (ObjectComponentTemplateInventory) template);
        } else if (template instanceof ObjectComponentTemplateNetwork) {
            return new ObjectComponentNetwork(ID, object, startEnabled, (ObjectComponentTemplateNetwork) template);
        }
        return null;
    }

}
