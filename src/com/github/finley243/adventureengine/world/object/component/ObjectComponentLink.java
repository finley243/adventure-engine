package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionMoveLink;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.load.LoadUtils;
import com.github.finley243.adventureengine.world.environment.AreaLink;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateLink;

import java.util.ArrayList;
import java.util.List;

public class ObjectComponentLink extends ObjectComponent {

    public ObjectComponentLink(String ID, WorldObject object, ObjectComponentTemplate template) {
        super(ID, object, template);
    }

    private ObjectComponentTemplateLink getTemplateLink() {
        return (ObjectComponentTemplateLink) getTemplate();
    }

    public Condition getCondition() {
        return getTemplateLink().getCondition();
    }

    public WorldObject getLinkedObject() {
        return getObject().game().data().getObject(getObject().getValueString(getID() + "_object", new Context(getObject().game(), getObject())));
    }

    public AreaLink.CompassDirection getDirection() {
        return LoadUtils.stringToEnum(getObject().getValueString(getID() + "_dir", new Context(getObject().game(), getObject())), AreaLink.CompassDirection.class);
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
