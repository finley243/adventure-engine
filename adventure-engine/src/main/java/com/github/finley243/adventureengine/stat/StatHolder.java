package com.github.finley243.adventureengine.stat;

public interface StatHolder {

    Stat getStat(String name);

    void onStatChange(String name);

}
