package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.actor.Inventory;

public interface StatHolder {

    StatController getStatController();

    Inventory getInventory();

    StatHolder getSubHolder(String name, String ID);

}
