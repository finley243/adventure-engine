package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.GameDataException;
import com.github.finley243.adventureengine.actor.Faction;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.Room;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;

public class RoomLoader {

    private static final String NAME_ROOM = "room";

    private static final String NAME_ID = "id";
    private static final String NAME_NAME = "name";
    private static final String NAME_PROPER = "proper";
    private static final String NAME_TYPE = "type";
    private static final String NAME_DESCRIPTION = "description";
    private static final String NAME_FACTION = "faction";
    private static final String NAME_RESTRICTION = "restriction";
    private static final String NAME_ALLOW_ALLIES = "allowAllies";

    private static final boolean DEFAULT_PROPER = false;
    private static final Area.AreaNameType DEFAULT_TYPE = Area.AreaNameType.IN;
    private static final Area.RestrictionType DEFAULT_RESTRICTION = Area.RestrictionType.PUBLIC;
    private static final boolean DEFAULT_ALLOW_ALLIES = false;

    private final SceneLoader sceneLoader;
    private final ScriptParser scriptParser;
    private final Registry<Faction> factionRegistry;

    public RoomLoader(SceneLoader sceneLoader, ScriptParser scriptParser, Registry<Faction> factionRegistry) {
        this.sceneLoader = sceneLoader;
        this.scriptParser = scriptParser;
        this.factionRegistry = factionRegistry;
    }

    public Map<String, Room> load(Element element) {
        return LoadUtils.loadAll(element, NAME_ROOM, this::parseRoom, Room::getID);
    }

    private Room parseRoom(Element element) {
        if (element == null) return null;
        String ID = LoadUtils.attribute(element, NAME_ID, null);
        Element nameElement = LoadUtils.singleChildWithName(element, NAME_NAME);
        String name = nameElement.getTextContent();
        boolean nameIsProper = LoadUtils.attributeBool(nameElement, NAME_PROPER, DEFAULT_PROPER);
        Area.AreaNameType nameType;
        try {
            nameType = LoadUtils.attributeEnum(nameElement, NAME_TYPE, Area.AreaNameType.class, DEFAULT_TYPE);
        } catch (IllegalArgumentException e) {
            throw new GameDataException("Room has invalid name type");
        }
        Scene description = sceneLoader.parseScene(LoadUtils.singleChildWithName(element, NAME_DESCRIPTION));
        String ownerFactionID = LoadUtils.attribute(element, NAME_FACTION, null);
        Faction ownerFaction = factionRegistry.getFromID(ownerFactionID);
        Area.RestrictionType restrictionType;
        try {
            restrictionType = LoadUtils.attributeEnum(element, NAME_RESTRICTION, Area.RestrictionType.class, DEFAULT_RESTRICTION);
        } catch (IllegalArgumentException e) {
            throw new GameDataException("Room has invalid restriction type");
        }
        boolean allowAllies = LoadUtils.attributeBool(element, NAME_ALLOW_ALLIES, DEFAULT_ALLOW_ALLIES);
        Map<String, List<Script>> roomScripts = LoadUtils.loadScriptsWithTriggers(element, scriptParser, "Room(" + ID + ")");
        return new Room(ID, name, nameType, nameIsProper, description, ownerFaction, restrictionType, allowAllies, roomScripts);
    }

}
