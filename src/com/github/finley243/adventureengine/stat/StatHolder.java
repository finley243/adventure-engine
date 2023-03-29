package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.actor.Inventory;

import java.util.Set;

public interface StatHolder {

    int getValueInt(String name);

    float getValueFloat(String name);

    boolean getValueBoolean(String name);

    String getValueString(String name);

    Set<String> getValueStringSet(String name);

    void setStateBoolean(String name, boolean value);

    void setStateInteger(String name, int value);

    void setStateFloat(String name, float value);

    void setStateString(String name, String value);

    void setStateStringSet(String name, Set<String> value);

    void modStateInteger(String name, int amount);

    void modStateFloat(String name, float amount);

    Inventory getInventory();

    StatHolder getSubHolder(String name, String ID);

}
