package com.github.finley243.adventureengine.network;

import com.github.finley243.adventureengine.world.Networked;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Network {

    private final String ID;
    private final String name;
    private final List<List<Networked>> connected;

    public Network(String ID, String name, int levels) {
        if(levels < 1) throw new IllegalArgumentException("Network must have at least 1 level");
        this.ID = ID;
        this.name = name;
        connected = new ArrayList<>(levels);
        for(int i = 0; i < levels; i++) {
            connected.add(new ArrayList<Networked>());
        }
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public void connectObject(Networked object, int level) {
        if(level < 1 || level >= connected.size()) throw new IllegalArgumentException("Attempted to add object to invalid network level");
        connected.get(level).add(object);
    }

    public List<Networked> getConnectedAtLevel(int level) {
        if(level < 1 || level >= connected.size()) throw new IllegalArgumentException("Attempted to access objects at invalid network level");
        return connected.get(level);
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Network)) {
            return false;
        } else {
            Network other = (Network) o;
            return this.getID().equals(other.getID()) && this.getName().equals(other.getName());
        }
    }

}
