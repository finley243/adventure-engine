package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateLock;

import java.util.ArrayList;
import java.util.List;

public class ObjectComponentLock extends ObjectComponent {

    private final ObjectComponentTemplateLock template;

    private boolean isLocked;

    public ObjectComponentLock(String ID, WorldObject object, ObjectComponentTemplateLock template) {
        super(ID, object, template.startEnabled());
        this.template = template;
    }

    @Override
    public List<Action> getActions(Actor subject) {
        List<Action> actions = new ArrayList<>();
        if (isLocked) {
            if (template.getLockpickLevel() != null) {

            }
            if (template.getHotwireLevel() != null) {

            }
            for (String keyItem : template.getKeyItems()) {

            }
        }
        return actions;
    }

}
