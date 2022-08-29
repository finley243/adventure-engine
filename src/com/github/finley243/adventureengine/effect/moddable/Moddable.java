package com.github.finley243.adventureengine.effect.moddable;

public interface Moddable {

    // TODO - Add function to get moddable enum stats

    ModdableStatInt getStatInt(String name);

    ModdableStatFloat getStatFloat(String name);

    ModdableStatBoolean getStatBoolean(String name);

    ModdableEffectList getStatEffects(String name);

    ModdableStatString getStatString(String name);

    ModdableStringSet getStatStrings(String name);

    void onStatChange();

    void modifyState(String name, int amount);

    void triggerEffect(String name);

}
