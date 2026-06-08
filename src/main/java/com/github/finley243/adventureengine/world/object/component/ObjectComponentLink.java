package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.DebugLogger;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.action.ActionTemplate;
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

    public ObjectComponentLink(Game game, WorldObject object, ObjectComponentTemplate template) {
        super(game, object, template);
    }

    private ObjectComponentTemplateLink getTemplateLink() {
        return (ObjectComponentTemplateLink) getTemplate();
    }

    public WorldObject getLinkedObject(Game game, String linkID) {
        Expression linkedObjectExpression = getObject().getLocalVariable(linkID + "_object");
        if (linkedObjectExpression == null) {
            DebugLogger.print("ObjectComponentLink " + getObject() + " - linked object local variable is missing");
            return null;
        }
        if (linkedObjectExpression.getDataType() != Expression.DataType.STRING) {
            DebugLogger.print("ObjectComponentLink " + getObject() + " - linked object local variable is not a string");
            return null;
        }
        String linkedObjectID = linkedObjectExpression.getValueString();
        return game.data().getObject(linkedObjectID);
    }

    public AreaLink.CompassDirection getDirection(String linkID) {
        Expression directionExpression = getObject().getLocalVariable(linkID + "_dir");
        if (directionExpression == null) {
            DebugLogger.print("ObjectComponentLink " + getObject() + " - direction local variable is missing");
            return null;
        }
        if (directionExpression.getDataType() != Expression.DataType.STRING) {
            DebugLogger.print("ObjectComponentLink " + getObject() + " - direction local variable is not a string");
            return null;
        }
        String directionString = directionExpression.getValueString();
        return LoadUtils.stringToEnum(directionString, AreaLink.CompassDirection.class);
    }

    public boolean isLinkedAreaVisible(String linkID, Game game, Actor actor) {
        ObjectComponentTemplateLink.ObjectLinkData linkData = getTemplateLink().getLinkData().get(linkID);
        Context context = Context.builder(game).subject(actor).target(actor).parentObject(getObject()).build();
        return linkData.isVisible() && (linkData.conditionVisible() == null || linkData.conditionVisible().isMet(context));
    }

    public Set<Area> getLinkedLineOfSightAreas(Game game) {
        Set<Area> linkedAreas = new HashSet<>();
        for (Map.Entry<String, ObjectComponentTemplateLink.ObjectLinkData> linkEntry : getTemplateLink().getLinkData().entrySet()) {
            if (linkEntry.getValue().isVisible()) {
                linkedAreas.add(getLinkedObject(game, linkEntry.getKey()).getArea());
            }
        }
        return linkedAreas;
    }

    public Map<Area, AreaLink.CompassDirection> getLinkedLineOfSightAreasWithDirections(Game game) {
        Map<Area, AreaLink.CompassDirection> linkedAreas = new HashMap<>();
        Context context = Context.builder(game).subject(game.data().getPlayer()).target(game.data().getPlayer()).parentObject(getObject()).build();
        for (Map.Entry<String, ObjectComponentTemplateLink.ObjectLinkData> linkEntry : getTemplateLink().getLinkData().entrySet()) {
            if (linkEntry.getValue().isVisible() && (linkEntry.getValue().conditionVisible() == null || linkEntry.getValue().conditionVisible().isMet(context))) {
                linkedAreas.put(getLinkedObject(game, linkEntry.getKey()).getArea(), getDirection(linkEntry.getKey()));
            }
        }
        return linkedAreas;
    }

    public Set<Area> getLinkedAreasAudible(Game game) {
        Set<Area> linkedAreas = new HashSet<>();
        for (Map.Entry<String, ObjectComponentTemplateLink.ObjectLinkData> linkEntry : getTemplateLink().getLinkData().entrySet()) {
            if (linkEntry.getValue().isVisible()) {
                linkedAreas.add(getLinkedObject(game, linkEntry.getKey()).getArea());
            }
        }
        return linkedAreas;
    }

    public Set<Area> getLinkedAreasMovable(Game game) {
        Set<Area> linkedAreas = new HashSet<>();
        for (Map.Entry<String, ObjectComponentTemplateLink.ObjectLinkData> linkEntry : getTemplateLink().getLinkData().entrySet()) {
            if (linkEntry.getValue().moveAction() != null) {
                linkedAreas.add(getLinkedObject(game, linkEntry.getKey()).getArea());
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
    protected List<Action> getPossibleActions(Game game, Actor subject) {
        List<Action> actions = new ArrayList<>();
        for (String linkID : getTemplateLink().getLinkData().keySet()) {
            ObjectComponentTemplateLink.ObjectLinkData linkData = getTemplateLink().getLinkData().get(linkID);
            if (linkData.moveAction() != null) {
                ActionTemplate linkMoveActionTemplate = game.data().getActionTemplate(linkData.moveAction());
                actions.add(new ActionCustom(null, getObject(), null, getLinkedObject(game, linkID).getArea(), linkMoveActionTemplate, new MapBuilder<String, Script>().put("dir", Script.constant(getDirection(linkID).toString())).build(), new MenuDataMove(getLinkedObject(game, linkID).getArea(), getDirection(linkID)), true));
            }
        }
        return actions;
    }

    @Override
    protected String getStatName() {
        return "link";
    }

}
