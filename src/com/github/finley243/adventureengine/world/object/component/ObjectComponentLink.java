package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.load.LoadUtils;
import com.github.finley243.adventureengine.menu.action.MenuDataMove;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateLink;

import java.util.*;

public class ObjectComponentLink extends ObjectComponent {

    public ObjectComponentLink(WorldObject object, ObjectComponentTemplate template) {
        super(object, template);
    }

    private ObjectComponentTemplateLink getTemplateLink() {
        return (ObjectComponentTemplateLink) getTemplate();
    }

    public WorldObject getLinkedObject(String linkID) {
        Context context = new Context(getObject().game(), getObject());
        Expression linkedObjectExpression = getObject().getLocalVariable(linkID + "_object");
        if (linkedObjectExpression == null) {
            getObject().game().log().print("ObjectComponentLink " + getObject() + " - linked object local variable is missing");
            return null;
        }
        if (linkedObjectExpression.getDataType() != Expression.DataType.STRING) {
            getObject().game().log().print("ObjectComponentLink " + getObject() + " - linked object local variable is not a string");
            return null;
        }
        String linkedObjectID = linkedObjectExpression.getValueString();
        return getObject().game().data().getObject(linkedObjectID);
    }

    public AreaLink.CompassDirection getDirection(String linkID) {
        Context context = new Context(getObject().game(), getObject());
        Expression directionExpression = getObject().getLocalVariable(linkID + "_dir");
        if (directionExpression == null) {
            getObject().game().log().print("ObjectComponentLink " + getObject() + " - direction local variable is missing");
            return null;
        }
        if (directionExpression.getDataType() != Expression.DataType.STRING) {
            getObject().game().log().print("ObjectComponentLink " + getObject() + " - direction local variable is not a string");
            return null;
        }
        String directionString = directionExpression.getValueString();
        return LoadUtils.stringToEnum(directionString, AreaLink.CompassDirection.class);
    }

    public boolean isLinkedAreaVisible(String linkID, Actor actor) {
        ObjectComponentTemplateLink.ObjectLinkData linkData = getTemplateLink().getLinkData().get(linkID);
        return linkData.isVisible() && (linkData.conditionVisible() == null || linkData.conditionVisible().isMet(new Context(getObject().game(), actor, actor, getObject())));
    }

    public Set<Area> getLinkedLineOfSightAreas() {
        Set<Area> linkedAreas = new HashSet<>();
        for (Map.Entry<String, ObjectComponentTemplateLink.ObjectLinkData> linkEntry : getTemplateLink().getLinkData().entrySet()) {
            if (linkEntry.getValue().isVisible()) {
                linkedAreas.add(getLinkedObject(linkEntry.getKey()).getArea());
            }
        }
        return linkedAreas;
    }

    public Map<Area, AreaLink.CompassDirection> getLinkedLineOfSightAreasWithDirections() {
        Map<Area, AreaLink.CompassDirection> linkedAreas = new HashMap<>();
        for (Map.Entry<String, ObjectComponentTemplateLink.ObjectLinkData> linkEntry : getTemplateLink().getLinkData().entrySet()) {
            if (linkEntry.getValue().isVisible() && (linkEntry.getValue().conditionVisible() == null || linkEntry.getValue().conditionVisible().isMet(new Context(getObject().game(), getObject().game().data().getPlayer(), getObject().game().data().getPlayer(), getObject())))) {
                linkedAreas.put(getLinkedObject(linkEntry.getKey()).getArea(), getDirection(linkEntry.getKey()));
            }
        }
        return linkedAreas;
    }

    public Set<Area> getLinkedAreasAudible() {
        Set<Area> linkedAreas = new HashSet<>();
        for (Map.Entry<String, ObjectComponentTemplateLink.ObjectLinkData> linkEntry : getTemplateLink().getLinkData().entrySet()) {
            if (linkEntry.getValue().isVisible()) {
                linkedAreas.add(getLinkedObject(linkEntry.getKey()).getArea());
            }
        }
        return linkedAreas;
    }

    public Set<Area> getLinkedAreasMovable() {
        Set<Area> linkedAreas = new HashSet<>();
        for (Map.Entry<String, ObjectComponentTemplateLink.ObjectLinkData> linkEntry : getTemplateLink().getLinkData().entrySet()) {
            if (linkEntry.getValue().moveAction() != null) {
                linkedAreas.add(getLinkedObject(linkEntry.getKey()).getArea());
            }
        }
        return linkedAreas;
    }

    /*public AreaLink.DistanceCategory getDistanceThrough(String linkID, Area originArea, Area targetArea) {
        WorldObject linkedObject = getLinkedObject(linkID);
        AreaLink.DistanceCategory distanceOriginToLink = getObject().getArea().getLinearDistanceTo(linkedObject.getArea().getID());
        AreaLink.DistanceCategory distanceLinkToTarget = linkedObject.getArea().getLinearDistanceTo(targetArea.getID());
        if (distanceOriginToLink == null) {
            return null;
        }
        if (distanceLinkToTarget == null) {
            return null;
        }
        return AreaLink.combinedDistance(distanceOriginToLink, distanceLinkToTarget);
    }*/

    @Override
    public List<Action> getActions(Actor subject) {
        List<Action> actions = new ArrayList<>();
        for (String linkID : getTemplateLink().getLinkData().keySet()) {
            ObjectComponentTemplateLink.ObjectLinkData linkData = getTemplateLink().getLinkData().get(linkID);
            if (linkData.moveAction() != null) {
                actions.add(new ActionCustom(subject.game(), null, getObject(), null, getLinkedObject(linkID).getArea(), linkData.moveAction(), new MapBuilder<String, Script>().put("dir", Script.constant(getDirection(linkID).toString())).build(), new MenuDataMove(getLinkedObject(linkID).getArea(), getDirection(linkID)), true));
            }
        }
        return actions;
    }

    @Override
    protected String getStatName() {
        return "link";
    }

}
