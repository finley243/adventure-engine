package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionMoveLink;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.AreaLink;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateLink;

import java.util.ArrayList;
import java.util.List;

public class ObjectComponentLink extends ObjectComponent {

    private final String templateID;

    public ObjectComponentLink(String ID, WorldObject object, String templateID) {
        super(ID, object);
        this.templateID = templateID;
    }

    @Override
    public ObjectComponentTemplate getTemplate() {
        return getTemplateLink();
    }

    public ObjectComponentTemplateLink getTemplateLink() {
        return (ObjectComponentTemplateLink) getObject().game().data().getObjectComponentTemplate(templateID);
    }

    public WorldObject getLinkedObject() {
        return getObject().game().data().getObject(getObject().getComponentLink(getID()).getObject());
    }

    public AreaLink.CompassDirection getDirection() {
        return getObject().getComponentLink(getID()).getDirection();
    }

    @Override
    public List<Action> getActions(Actor subject) {
        List<Action> actions = new ArrayList<>();
        if (getTemplateLink().isMovable()) {
            actions.add(new ActionMoveLink(this));
        }
        return actions;
    }

}
