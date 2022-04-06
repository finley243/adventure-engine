package com.github.finley243.adventureengine;

public interface Moddable {

    ModdableStat getStat(String name);

    void onStatChange();

    void modifyState(String name, int amount);

    void triggerSpecial(String name);

}
