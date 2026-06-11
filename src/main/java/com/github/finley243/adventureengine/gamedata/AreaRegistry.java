package com.github.finley243.adventureengine.gamedata;

import com.github.finley243.adventureengine.world.environment.Area;

import java.util.*;

public class AreaRegistry extends Registry<Area> {

    private final Map<String, Set<Area>> areasByRoom;

    public AreaRegistry(Map<String, Area> entries) {
        super(entries);
        this.areasByRoom = new HashMap<>();
        for (Area area : entries.values()) {
            if (area.getRoom() != null) {
                areasByRoom.computeIfAbsent(area.getRoom().getID(), _ -> new HashSet<>()).add(area);
            }
        }
    }

    public Collection<Area> getAllInRoomID(String roomID) {
        return areasByRoom.get(roomID);
    }

}
