package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.gamedata.ConfigHandler;
import com.github.finley243.adventureengine.gamedata.ConfigOption;
import com.github.finley243.adventureengine.gamedata.MutableRegistry;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;
import com.github.finley243.adventureengine.world.environment.Room;
import com.github.finley243.adventureengine.world.object.WorldObject;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AreaLoader {

    private static final String NAME_AREA = "area";

    private final ConfigHandler configHandler;
    private final ScriptParser scriptParser;
    private final SceneLoader sceneLoader;
    private final ActorLoader actorLoader;
    private final ObjectLoader objectLoader;
    private final ItemLoader itemLoader;
    private final MutableRegistry<Item> itemMutableRegistry;
    private final Registry<Room> roomRegistry;

    public AreaLoader(ConfigHandler configHandler, ScriptParser scriptParser, SceneLoader sceneLoader, ActorLoader actorLoader, ObjectLoader objectLoader, ItemLoader itemLoader, MutableRegistry<Item> itemMutableRegistry, Registry<Room> roomRegistry) {
        this.configHandler = configHandler;
        this.scriptParser = scriptParser;
        this.sceneLoader = sceneLoader;
        this.actorLoader = actorLoader;
        this.objectLoader = objectLoader;
        this.itemLoader = itemLoader;
        this.itemMutableRegistry = itemMutableRegistry;
        this.roomRegistry = roomRegistry;
    }

    public AreaLoaderResult load(Element element) {
        Map<String, Area> areaMap = new HashMap<>();
        Map<String, Actor> actorMap = new HashMap<>();
        Map<String, WorldObject> objectMap = new HashMap<>();
        for (Element child : LoadUtils.directChildrenWithName(element, NAME_AREA)) {
            AreaParseResult result = parseArea(child);
            areaMap.put(result.area().getID(), result.area());
            actorMap.putAll(result.actors());
            objectMap.putAll(result.objects());
        }
        return new AreaLoaderResult(areaMap, actorMap, objectMap);
    }

    private AreaParseResult parseArea(Element element) {
        if (element == null) return null;
        String areaID = LoadUtils.attribute(element, "id", null);
        String roomID = LoadUtils.attribute(element, "room", null);
        Room room = roomID != null ? roomRegistry.getFromID(roomID) : null;
        String landmarkID = LoadUtils.attribute(element, "landmark", null);
        Element nameElement = LoadUtils.singleChildWithName(element, "name");
        String name = (nameElement == null ? null : nameElement.getTextContent());
        Area.AreaNameType nameType = LoadUtils.attributeEnum(nameElement, "type", Area.AreaNameType.class, (landmarkID != null ? Area.AreaNameType.NEAR : Area.AreaNameType.IN));
        boolean nameIsPlural = LoadUtils.attributeBool(nameElement, "plural", false);
        Scene description = sceneLoader.parseScene(LoadUtils.singleChildWithName(element, "description"));
        String areaOwnerFaction = LoadUtils.attribute(element, "faction", null);
        Area.RestrictionType restrictionType = LoadUtils.attributeEnum(element, "restriction", Area.RestrictionType.class, Area.RestrictionType.PUBLIC);
        boolean allowAllies = LoadUtils.attributeBool(element, "allowAllies", false);

        List<Element> linkElements = LoadUtils.directChildrenWithName(element, "link");
        Map<String, AreaLink> linkSet = new HashMap<>();
        String linkTypeDefault = configHandler.get(ConfigOption.DEFAULT_LINK_TYPE);
        for (Element linkElement : linkElements) {
            String linkAreaID = LoadUtils.attribute(linkElement, "area", null);
            String linkType = LoadUtils.attribute(linkElement, "type", linkTypeDefault);
            AreaLink.CompassDirection linkDirection = LoadUtils.attributeEnum(linkElement, "dir", AreaLink.CompassDirection.class, AreaLink.CompassDirection.N);
            AreaLink.DistanceCategory linkDistance = LoadUtils.attributeEnum(linkElement, "dist", AreaLink.DistanceCategory.class, AreaLink.DistanceCategory.CLOSE);
            AreaLink link = new AreaLink(linkAreaID, linkType, linkDirection, linkDistance);
            linkSet.put(linkAreaID, link);
        }

        Set<String> defaultObstructionTypes = LoadUtils.setOfTags(element, "obstructionType");

        Map<String, List<Script>> areaScripts = LoadUtils.loadScriptsWithTriggers(element, scriptParser, "Area(" + areaID + ")");

        Area area = new Area(areaID, landmarkID, name, nameType, nameIsPlural, description, room, areaOwnerFaction, restrictionType, allowAllies, linkSet, defaultObstructionTypes, areaScripts);

        Map<String, WorldObject> objectMap = new HashMap<>();
        List<Element> objectElements = LoadUtils.directChildrenWithName(element, "object");
        for (Element objectElement : objectElements) {
            WorldObject object = objectLoader.parseObject(objectElement, area);
            objectMap.put(object.getID(), object);
        }

        List<Element> itemElements = LoadUtils.directChildrenWithName(element, "item");
        for (Element itemElement : itemElements) {
            Item item = itemLoader.parseItem(itemElement);
            area.getInventory().addItem(item, itemMutableRegistry);
        }

        Map<String, Actor> actorMap = new HashMap<>();
        List<Element> actorElements = LoadUtils.directChildrenWithName(element, "actor");
        for (Element actorElement : actorElements) {
            Actor actor = actorLoader.parseActor(actorElement, area);
            actorMap.put(actor.getID(), actor);
        }

        return new AreaParseResult(area, actorMap, objectMap);
    }

    public record AreaLoaderResult(Map<String, Area> areas, Map<String, Actor> actors, Map<String, WorldObject> objects) {}

    private record AreaParseResult(Area area, Map<String, Actor> actors, Map<String, WorldObject> objects) {}

}
