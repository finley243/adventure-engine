package com.github.finley243.adventureengine.world.object;

import com.github.finley243.adventureengine.actor.Actor;

public class ObjectChair extends WorldObject {

    private Actor occupant;

    public ObjectChair(String ID, String areaID, String name) {
        super(ID, areaID, name);
    }
    
}
