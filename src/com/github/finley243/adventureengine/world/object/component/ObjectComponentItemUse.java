package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateItemUse;

import java.util.ArrayList;
import java.util.List;

public class ObjectComponentItemUse extends ObjectComponent {

    private final ObjectComponentTemplateItemUse template;

    private boolean hasSucceeded;

    public ObjectComponentItemUse(String ID, WorldObject object, ObjectComponentTemplateItemUse template) {
        super(ID, object, template.startEnabled());
        this.template = template;
    }

    @Override
    public List<Action> getActions(Actor subject) {
        List<Action> actions = new ArrayList<>();
        return actions;
    }

}
