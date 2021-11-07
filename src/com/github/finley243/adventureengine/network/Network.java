package com.github.finley243.adventureengine.network;

import com.github.finley243.adventureengine.world.Networked;

import java.util.ArrayList;
import java.util.List;

public class Network {

    private final String ID;
    private final String name;
    private final List<SubNetwork> network;

    public Network(String ID, String name, int[] securityLevels) {
        if(securityLevels.length < 1) throw new IllegalArgumentException("Network must have at least 1 level");
        this.ID = ID;
        this.name = name;
        network = new ArrayList<>(securityLevels.length);
        for (int securityLevel : securityLevels) {
            network.add(new SubNetwork(securityLevel));
        }
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public void connectObject(Networked object, int level) {
        if(level < 1 || level >= network.size()) throw new IllegalArgumentException("Attempted to add object to invalid network level");
        network.get(level).connected.add(object);
    }

    public List<Networked> getConnectedAtLevel(int level) {
        if(level < 1 || level >= network.size()) throw new IllegalArgumentException("Attempted to access objects at invalid network level");
        return network.get(level).connected;
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

    private static class SubNetwork {

        final List<Networked> connected;
        final int securityLevel;

        public SubNetwork(int securityLevel) {
            this.connected = new ArrayList<>();
            this.securityLevel = securityLevel;
        }

    }

}
