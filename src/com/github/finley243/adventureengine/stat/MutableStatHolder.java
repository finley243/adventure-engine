package com.github.finley243.adventureengine.stat;

public interface MutableStatHolder extends StatHolder {

    MutableStatController getMutableStatController();

    void onStatChange(String name);

}
