package com.github.finley243.adventureengine.stat;

import java.util.Set;

public interface StatHolder {

    // TODO - Add function to get moddable enum stats

    StatInt getStatInt(String name);

    StatFloat getStatFloat(String name);

    StatBoolean getStatBoolean(String name);

    StatString getStatString(String name);

    StatStringSet getStatStringSet(String name);

    int getStatValueInt(String name);

    float getStatValueFloat(String name);

    boolean getStatValueBoolean(String name);

    String getStatValueString(String name);

    Set<String> getStatValueStringSet(String name);

    void onStatChange();

    void modifyState(String name, int amount);

    void triggerEffect(String name);

}
