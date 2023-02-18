package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.*;

import java.util.Map;

public class ObjectComponentFactory {

    public static ObjectComponent create(ObjectComponentTemplate template, String ID, WorldObject object) {
        if (template instanceof ObjectComponentTemplateInventory) {
            return new ObjectComponentInventory(ID, object, template.getID());
        } else if (template instanceof ObjectComponentTemplateNetwork) {
            return new ObjectComponentNetwork(ID, object, template.getID());
        } else if (template instanceof ObjectComponentTemplateLink) {
            return new ObjectComponentLink(ID, object, template.getID());
        } else if (template instanceof ObjectComponentTemplateUsable) {
            return new ObjectComponentUsable(ID, object, template.getID());
        } else if (template instanceof ObjectComponentTemplateCheck) {
            return new ObjectComponentCheck(ID, object, template.getID());
        } else if (template instanceof ObjectComponentTemplateItemUse) {
            return new ObjectComponentItemUse(ID, object, template.getID());
        } else if (template instanceof ObjectComponentTemplateVending) {
            return new ObjectComponentVending(ID, object, template.getID());
        }
        return null;
    }

}
