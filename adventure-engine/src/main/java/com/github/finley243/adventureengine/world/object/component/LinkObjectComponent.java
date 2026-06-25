package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.action.ActionDependencies;
import com.github.finley243.adventureengine.action.ActionTemplate;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.action.MenuDataMove;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.LinkObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;

import java.util.*;

public class LinkObjectComponent extends ObjectComponent {

    private final Map<String, LinkData> linkData;

    LinkObjectComponent(WorldObject object, ObjectComponentTemplate template) {
        super(object, template);
        this.linkData = new HashMap<>();
    }

    public void resolveLinkedObjects(Map<String, LinkData> linkedObjects) {
        if (!linkData.isEmpty()) throw new IllegalStateException("Link data has already been set");
        linkData.putAll(linkedObjects);
    }

    private LinkObjectComponentTemplate getTemplateLink() {
        return (LinkObjectComponentTemplate) getTemplate();
    }

    public WorldObject getLinkedObject(String linkID) {
        return linkData.get(linkID).object();
    }

    public AreaLink.CompassDirection getDirection(String linkID) {
        return linkData.get(linkID).direction();
    }

    public boolean isLinkedAreaVisible(ScriptRuntime scriptRuntime, String linkID, Actor actor) {
        LinkObjectComponentTemplate.ObjectLinkData linkData = getTemplateLink().getLinkData().get(linkID);
        Context context = Context.builder().subject(actor).target(actor).parentObject(getObject()).build();
        return linkData.isVisible() && (linkData.conditionVisible() == null || linkData.conditionVisible().isMet(context));
    }

    public Set<Area> getLinkedLineOfSightAreas() {
        Set<Area> linkedAreas = new HashSet<>();
        for (Map.Entry<String, LinkObjectComponentTemplate.ObjectLinkData> linkEntry : getTemplateLink().getLinkData().entrySet()) {
            if (linkEntry.getValue().isVisible()) {
                linkedAreas.add(getLinkedObject(linkEntry.getKey()).getArea());
            }
        }
        return linkedAreas;
    }

    public Map<Area, AreaLink.CompassDirection> getLinkedLineOfSightAreasWithDirections(ScriptRuntime scriptRuntime) {
        Map<Area, AreaLink.CompassDirection> linkedAreas = new HashMap<>();
        Context context = Context.builder().parentObject(getObject()).build();
        for (Map.Entry<String, LinkObjectComponentTemplate.ObjectLinkData> linkEntry : getTemplateLink().getLinkData().entrySet()) {
            if (linkEntry.getValue().isVisible() && (linkEntry.getValue().conditionVisible() == null || linkEntry.getValue().conditionVisible().isMet(context))) {
                linkedAreas.put(getLinkedObject(linkEntry.getKey()).getArea(), getDirection(linkEntry.getKey()));
            }
        }
        return linkedAreas;
    }

    public Set<Area> getLinkedAreasAudible() {
        Set<Area> linkedAreas = new HashSet<>();
        for (Map.Entry<String, LinkObjectComponentTemplate.ObjectLinkData> linkEntry : getTemplateLink().getLinkData().entrySet()) {
            if (linkEntry.getValue().isVisible()) {
                linkedAreas.add(getLinkedObject(linkEntry.getKey()).getArea());
            }
        }
        return linkedAreas;
    }

    public Set<Area> getLinkedAreasMovable() {
        Set<Area> linkedAreas = new HashSet<>();
        for (Map.Entry<String, LinkObjectComponentTemplate.ObjectLinkData> linkEntry : getTemplateLink().getLinkData().entrySet()) {
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
    protected List<Action> getPossibleActions(Actor subject, ActionDependencies dependencies) {
        List<Action> actions = new ArrayList<>();
        for (String linkID : getTemplateLink().getLinkData().keySet()) {
            LinkObjectComponentTemplate.ObjectLinkData linkData = getTemplateLink().getLinkData().get(linkID);
            if (linkData.moveAction() != null) {
                ActionTemplate linkMoveActionTemplate = linkData.moveAction();
                actions.add(new ActionCustom(subject, dependencies, null, getObject(), null, getLinkedObject(linkID).getArea(), linkMoveActionTemplate, new MapBuilder<String, Script>().put("dir", Script.constant(getDirection(linkID).toString())).build(), new MenuDataMove(getLinkedObject(linkID).getArea(), getDirection(linkID)), true));
            }
        }
        return actions;
    }

    @Override
    protected String getStatName() {
        return "link";
    }

    public record LinkDataIntermediate(String linkID, String objectID, AreaLink.CompassDirection direction) {}

    public record LinkData(String linkID, WorldObject object, AreaLink.CompassDirection direction) {}

}
