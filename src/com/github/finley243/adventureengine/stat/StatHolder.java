package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.Set;

public interface StatHolder {

    int getValueInt(String name, Context context);

    float getValueFloat(String name, Context context);

    boolean getValueBoolean(String name, Context context);

    String getValueString(String name, Context context);

    Set<String> getValueStringSet(String name, Context context);

    Expression getStatValue(String name, Context context);

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
