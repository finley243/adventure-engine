package com.github.finley243.adventureengine.stat;

public interface StatHolder {

    // TODO - Add function to get moddable enum stats

    StatInt getStatInt(String name);

    StatFloat getStatFloat(String name);

    StatBoolean getStatBoolean(String name);

    StatEffectList getStatEffects(String name);

    StatString getStatString(String name);

    StatStringSet getStatStringSet(String name);

    void onStatChange();

    void modifyState(String name, int amount);

    void triggerEffect(String name);

}
