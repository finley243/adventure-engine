package com.github.finley243.adventureengine;

public interface Moddable {

    ModdableStatInt getStatInt(String name);

    ModdableStatFloat getStatFloat(String name);

    void onStatChange();

    void modifyState(String name, int amount);

    void triggerSpecial(String name);

}
