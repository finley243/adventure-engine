package com.github.finley243.adventureengine.network;

import com.github.finley243.adventureengine.action.network.NetworkAction;
import com.github.finley243.adventureengine.world.Networked;

import java.util.ArrayList;
import java.util.List;

public class Network {

    private final String ID;
    private final String name;
    private final List<SubNetwork> subNets;

    public Network(String ID, String name, List<SubNetwork> subNets) {
        this.ID = ID;
        this.name = name;
        this.subNets = subNets;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
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
            return this.getID().equals(other.getID());
        }
    }

}
