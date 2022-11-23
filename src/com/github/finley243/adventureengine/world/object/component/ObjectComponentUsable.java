package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateUsable;

import java.util.ArrayList;
import java.util.List;

public class ObjectComponentUsable extends ObjectComponent {

    private final ObjectComponentTemplateUsable template;

    public ObjectComponentUsable(String ID, WorldObject object, ObjectComponentTemplateUsable template) {
        super(ID, object, template.startEnabled());
        this.template = template;
    }

    @Override
    public ObjectComponentTemplate getTemplate() {
        return template;
    }

    @Override
    public List<Action> getActions(Actor subject) {
        List<Action> actions = new ArrayList<>();
        return actions;
    }

}
