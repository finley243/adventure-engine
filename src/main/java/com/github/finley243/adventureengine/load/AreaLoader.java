package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.Room;
import org.w3c.dom.Element;

import java.util.Map;

public class AreaLoader {

    private final Registry<Room> roomRegistry;

    public AreaLoader(Registry<Room> roomRegistry) {
        this.roomRegistry = roomRegistry;
    }

    public Map<String, Area> load(Element element) {

    }

}
