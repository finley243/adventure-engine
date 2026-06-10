package com.github.finley243.adventureengine.gamedata;

import com.github.finley243.adventureengine.world.environment.Area;

import java.util.*;

public class AreaRegistry extends Registry<Area> {

    private final Map<String, Set<Area>> areasByRoom;

    public AreaRegistry(Map<String, Area> entries) {
        super(entries);
        this.areasByRoom = new HashMap<>();
        for (Area area : entries.values()) {
            String roomID = area.getRoom().getID();
            if (!areasByRoom.containsKey(roomID)) {
                areasByRoom.put(roomID, new HashSet<>());
            }
            this.areasByRoom.get(area.getRoom().getID()).add(area);
        }
    }

    public Collection<Area> getAllInRoomID(String roomID) {
        return areasByRoom.get(roomID);
    }

}
