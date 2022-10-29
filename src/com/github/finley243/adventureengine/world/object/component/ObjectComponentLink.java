package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionMoveLink;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateLink;

import java.util.ArrayList;
import java.util.List;

public class ObjectComponentLink extends ObjectComponent {

    private final ObjectComponentTemplateLink template;

    public ObjectComponentLink(String ID, WorldObject object, ObjectComponentTemplateLink template) {
        super(ID, object, template.startEnabled());
        this.template = template;
    }

    public WorldObject getLinkedObject() {
        return getObject().game().data().getObject(getObject().getLinkedObjectID(getID()));
    }

    public AreaLink.CompassDirection getDirection() {
        return template.getDirection();
    }

    @Override
    public List<Action> getActions(Actor subject) {
        List<Action> actions = new ArrayList<>();
        if (template.isMovable()) {
            Area destination = subject.game().data().getObject(getObject().getLinkedObjectID(getID())).getArea();
            actions.add(new ActionMoveLink(this));
        }
        return actions;
    }

}
