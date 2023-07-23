package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionMoveLink;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.load.LoadUtils;
import com.github.finley243.adventureengine.world.environment.Area;
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

    public boolean movableConditionIsMet(Actor subject) {
        return getTemplateLink().getConditionMovable() == null || getTemplateLink().getConditionMovable().isMet(new Context(getObject().game(), subject, subject, getObject()));
    }

    public boolean visibleConditionIsMet(Actor subject) {
        return getTemplateLink().getConditionVisible() == null || getTemplateLink().getConditionVisible().isMet(new Context(getObject().game(), subject, subject, getObject()));
    }

    public WorldObject getLinkedObject() {
        Context context = new Context(getObject().game(), getObject());
        Expression linkedObjectExpression = getObject().getStatValue(getID() + "_object", context);
        if (linkedObjectExpression == null) {
            getObject().game().log().print("ObjectComponentLink " + getObject() + "/" + this + " - linked object local variable is missing");
            return null;
        }
        if (linkedObjectExpression.getDataType() != Expression.DataType.STRING) {
            getObject().game().log().print("ObjectComponentLink " + getObject() + "/" + this + " - linked object local variable is not a string");
            return null;
        }
        String linkedObjectID = linkedObjectExpression.getValueString(context);
        return getObject().game().data().getObject(linkedObjectID);
    }

    public AreaLink.CompassDirection getDirection() {
        Context context = new Context(getObject().game(), getObject());
        Expression directionExpression = getObject().getStatValue(getID() + "_dir", context);
        if (directionExpression == null) {
            getObject().game().log().print("ObjectComponentLink " + getObject() + "/" + this + " - direction local variable is missing");
            return null;
        }
        if (directionExpression.getDataType() != Expression.DataType.STRING) {
            getObject().game().log().print("ObjectComponentLink " + getObject() + "/" + this + " - direction local variable is not a string");
            return null;
        }
        String directionString = directionExpression.getValueString(context);
        return LoadUtils.stringToEnum(directionString, AreaLink.CompassDirection.class);
    }

    public boolean isVisible() {
        return getTemplateLink().isVisible();
    }

    public boolean isMovable() {
        return getTemplateLink().isMovable();
    }

    public AreaLink.DistanceCategory getDistanceThrough(Area originArea, Area targetArea) {
        WorldObject linkedObject = getLinkedObject();
        AreaLink.DistanceCategory distanceOriginToLink = getObject().getArea().getDistanceTo(linkedObject.getArea().getID());
        AreaLink.DistanceCategory distanceLinkToTarget = linkedObject.getArea().getDistanceTo(targetArea.getID());
        if (distanceOriginToLink == null) {
            return null;
        }
        if (distanceLinkToTarget == null) {
            return null;
        }
        return AreaLink.combinedDistance(distanceOriginToLink, distanceLinkToTarget);
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
