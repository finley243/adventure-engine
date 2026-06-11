package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.world.environment.AreaLink;
import com.github.finley243.adventureengine.world.environment.LinkType;
import com.github.finley243.adventureengine.world.obstruction.ObstructionType;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WorldTypeLoader {

    private static final String NAME_OBSTRUCTION_TYPE = "obstructionType";
    private static final String NAME_LINK_TYPE = "linkType";

    private static final String NAME_OBSTRUCTION_TYPE_ID = "id";

    public WorldTypeLoader() {

    }

    public Map<String, ObstructionType> loadObstructionTypes(Element element) {
        return LoadUtils.loadAll(element, NAME_OBSTRUCTION_TYPE, this::parseObstructionType, ObstructionType::ID);
    }

    public Map<String, LinkType> loadLinkTypes(Element element) {
        return LoadUtils.loadAll(element, NAME_LINK_TYPE, this::parseLinkType, LinkType::getID);
    }

    private ObstructionType parseObstructionType(Element element) {
        String ID = LoadUtils.attribute(element, NAME_OBSTRUCTION_TYPE_ID, null);
        String name = element.getTextContent();
        return new ObstructionType(ID, name);
    }

    private LinkType parseLinkType(Element element) {
        String ID = LoadUtils.attribute(element, "id", null);
        boolean isVisible = LoadUtils.attributeBool(element, "visible", true);
        String actorMoveAction = LoadUtils.attribute(element, "moveAction", null);
        Set<AreaLink.DistanceCategory> actorMoveDistances = LoadUtils.setOfEnumTags(element, "moveDistance", AreaLink.DistanceCategory.class);
        Map<String, String> vehicleMoveActions = new HashMap<>();
        Map<String, Set<AreaLink.DistanceCategory>> vehicleMoveDistances = new HashMap<>();
        for (Element vehicleTypeElement : LoadUtils.directChildrenWithName(element, "vehicleMoveAction")) {
            String vehicleType = LoadUtils.attribute(vehicleTypeElement, "type", null);
            String vehicleAction = LoadUtils.attribute(vehicleTypeElement, "action", null);
            Set<AreaLink.DistanceCategory> vehicleTypeMoveDistances = LoadUtils.setOfEnumTags(element, "moveDistance", AreaLink.DistanceCategory.class);
            vehicleMoveActions.put(vehicleType, vehicleAction);
            vehicleMoveDistances.put(vehicleType, vehicleTypeMoveDistances);
        }
        return new LinkType(ID, isVisible, actorMoveAction, actorMoveDistances, vehicleMoveActions, vehicleMoveDistances);
    }

}
