package com.github.finley243.adventureengine.world;

import com.github.finley243.adventureengine.combat.Damage;
import com.github.finley243.adventureengine.world.environment.Area;

public interface AttackTarget {

    boolean canBeAttacked();

    void damage(Damage damage);

    Area getArea();

}
