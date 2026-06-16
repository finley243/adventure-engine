package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Faction;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.gamedata.*;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemFactory;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;
import com.github.finley243.adventureengine.world.environment.LinkType;
import com.github.finley243.adventureengine.world.environment.Room;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.obstruction.ObstructionType;
import org.w3c.dom.Element;

import java.util.*;

public class AreaLoader {

    private static final String NAME_AREA = "area";

    private final ConfigHandler configHandler;
    private final ScriptRuntime scriptRuntime;
    private final ScriptParser scriptParser;
    private final SceneLoader sceneLoader;
    private final ActorLoader actorLoader;
    private final ObjectLoader objectLoader;
    private final ItemLoader itemLoader;
    private final Registry<Faction> factionRegistry;
    private final Registry<Room> roomRegistry;
    private final Registry<ObstructionType> obstructionTypeRegistry;
    private final Registry<LinkType> linkTypeRegistry;
    private final Registry<Effect> effectRegistry;
    private final ItemFactory itemFactory;

    public AreaLoader(ConfigHandler configHandler, ScriptRuntime scriptRuntime, ScriptParser scriptParser, SceneLoader sceneLoader, ActorLoader actorLoader, ObjectLoader objectLoader, ItemLoader itemLoader, Registry<Faction> factionRegistry, Registry<Room> roomRegistry, Registry<ObstructionType> obstructionTypeRegistry, Registry<LinkType> linkTypeRegistry, Registry<Effect> effectRegistry, ItemFactory itemFactory) {
        this.configHandler = configHandler;
        this.scriptRuntime = scriptRuntime;
        this.scriptParser = scriptParser;
        this.sceneLoader = sceneLoader;
        this.actorLoader = actorLoader;
        this.objectLoader = objectLoader;
        this.itemLoader = itemLoader;
        this.factionRegistry = factionRegistry;
        this.roomRegistry = roomRegistry;
        this.obstructionTypeRegistry = obstructionTypeRegistry;
        this.linkTypeRegistry = linkTypeRegistry;
        this.effectRegistry = effectRegistry;
        this.itemFactory = itemFactory;
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
        String areaID = LoadUtils.attribute(element, "id", null);
        String roomID = LoadUtils.attribute(element, "room", null);
        Room room = roomID != null ? roomRegistry.getFromID(roomID) : null;
        String landmarkID = LoadUtils.attribute(element, "landmark", null);
        Element nameElement = LoadUtils.singleChildWithName(element, "name");
        String name = (nameElement == null ? null : nameElement.getTextContent());
        Area.AreaNameType nameType;
        try {
            nameType = LoadUtils.attributeEnum(nameElement, "type", Area.AreaNameType.class, (landmarkID != null ? Area.AreaNameType.NEAR : Area.AreaNameType.IN));
        } catch (IllegalArgumentException e) {
            throw new GameDataException("Area has invalid name type");
        }
        boolean nameIsPlural = LoadUtils.attributeBool(nameElement, "plural", false);
        Scene description = sceneLoader.parseScene(LoadUtils.singleChildWithName(element, "description"));
        String areaOwnerFactionID = LoadUtils.attribute(element, "faction", null);
        Faction areaOwnerFaction = factionRegistry.getFromID(areaOwnerFactionID);
        if (areaOwnerFaction == null && areaOwnerFactionID != null) throw new GameDataException("Area has invalid owner faction");
        Area.RestrictionType restrictionType;
        try {
            restrictionType = LoadUtils.attributeEnum(element, "restriction", Area.RestrictionType.class, Area.RestrictionType.PUBLIC);
        } catch (IllegalArgumentException e) {
            throw new GameDataException("Area has invalid restriction type");
        }
        boolean allowAllies = LoadUtils.attributeBool(element, "allowAllies", false);

        List<Element> linkElements = LoadUtils.directChildrenWithName(element, "link");
        Map<String, AreaLink> linkSet = new HashMap<>();
        String linkTypeDefault = configHandler.get(ConfigOption.DEFAULT_LINK_TYPE);
        for (Element linkElement : linkElements) {
            String linkAreaID = LoadUtils.attribute(linkElement, "area", null);
            String linkTypeID = LoadUtils.attribute(linkElement, "type", linkTypeDefault);
            LinkType linkType = linkTypeRegistry.getFromID(linkTypeID);
            if (linkType == null) throw new GameDataException("AreaLink has invalid link type");
            AreaLink.CompassDirection linkDirection;
            try {
                linkDirection = LoadUtils.attributeEnum(linkElement, "dir", AreaLink.CompassDirection.class, AreaLink.CompassDirection.N);
            } catch (IllegalArgumentException e) {
                throw new GameDataException("Area has invalid link direction");
            }
            AreaLink.DistanceCategory linkDistance;
            try {
                linkDistance = LoadUtils.attributeEnum(linkElement, "dist", AreaLink.DistanceCategory.class, AreaLink.DistanceCategory.CLOSE);
            } catch (IllegalArgumentException e) {
                throw new GameDataException("Area has invalid link distance category");
            }
            AreaLink link = new AreaLink(linkType, linkDirection, linkDistance);
            linkSet.put(linkAreaID, link);
        }

        Set<String> defaultObstructionTypeIDs = LoadUtils.setOfTags(element, "obstructionType");
        Set<ObstructionType> defaultObstructionTypes = new HashSet<>();
        for (String obstructionTypeID : defaultObstructionTypeIDs) {
            ObstructionType obstructionType = obstructionTypeRegistry.getFromID(obstructionTypeID);
            if (obstructionType == null) throw new GameDataException("Area has invalid obstruction type");
            defaultObstructionTypes.add(obstructionType);
        }

        Map<String, List<Script>> areaScripts = LoadUtils.loadScriptsWithTriggers(element, scriptParser, "Area(" + areaID + ")");

        Map<String, WorldObject> objectMap = new HashMap<>();
        for (Element objectElement : LoadUtils.directChildrenWithName(element, "object")) {
            WorldObject object = objectLoader.parseObject(objectElement);
            objectMap.put(object.getID(), object);
        }

        WorldObject landmarkObject = null;
        if (landmarkID != null) {
            landmarkObject = objectMap.get(landmarkID);
            if (landmarkObject == null) throw new GameDataException("Area landmark object not found");
        }

        Area area = new Area(scriptRuntime, obstructionTypeRegistry, effectRegistry, itemFactory, areaID, landmarkObject, name, nameType, nameIsPlural, description, room, areaOwnerFaction, restrictionType, allowAllies, linkSet, defaultObstructionTypes, areaScripts);

        for (Element itemElement : LoadUtils.directChildrenWithName(element, "item")) {
            Item item = itemLoader.parseItem(itemElement);
            area.getInventory().addItem(item);
        }

        Map<String, Actor> actorMap = new HashMap<>();
        for (Element actorElement : LoadUtils.directChildrenWithName(element, "actor")) {
            Actor actor = actorLoader.parseActor(actorElement, area);
            actorMap.put(actor.getID(), actor);
        }

        return new AreaParseResult(area, actorMap, objectMap);
    }

    public record AreaLoaderResult(Map<String, Area> areas, Map<String, Actor> actors, Map<String, WorldObject> objects) {}

    private record AreaParseResult(Area area, Map<String, Actor> actors, Map<String, WorldObject> objects) {}

}
