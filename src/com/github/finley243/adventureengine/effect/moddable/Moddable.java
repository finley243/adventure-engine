package com.github.finley243.adventureengine.effect.moddable;

public interface Moddable {

    ModdableStatInt getStatInt(String name);

    ModdableStatFloat getStatFloat(String name);

    ModdableStatBoolean getStatBoolean(String name);

    ModdableEffectList getStatEffects(String name);

    void onStatChange();

    void modifyState(String name, int amount);

    void triggerEffect(String name);

}
