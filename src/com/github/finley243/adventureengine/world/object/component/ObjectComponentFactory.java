package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateInventory;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateNetwork;

import java.util.Map;

public class ObjectComponentFactory {

    public static ObjectComponent create(ObjectComponentTemplate template, String ID, WorldObject object) {
        if (template instanceof ObjectComponentTemplateInventory) {
            return new ObjectComponentInventory(ID, object, (ObjectComponentTemplateInventory) template);
        } else if (template instanceof ObjectComponentTemplateNetwork) {
            return new ObjectComponentNetwork(ID, object, (ObjectComponentTemplateNetwork) template);
        }
        return null;
    }

    public static void loadComponents(Map<String, String> templates, WorldObject object) {
        for (String ID : templates.keySet()) {
            String templateID = templates.get(ID);
            ObjectComponent component = create(object.game().data().getObjectComponentTemplate(templateID), ID, object);
            object.addComponent(ID, component);
        }
    }

}
