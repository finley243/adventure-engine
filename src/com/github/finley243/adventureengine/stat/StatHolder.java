package com.github.finley243.adventureengine.stat;

import java.util.Set;

public interface StatHolder {

    StatInt getStatInt(String name);

    StatFloat getStatFloat(String name);

    StatBoolean getStatBoolean(String name);

    StatString getStatString(String name);

    StatStringSet getStatStringSet(String name);

    void onStatChange();

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

    void triggerEffect(String name);

}
