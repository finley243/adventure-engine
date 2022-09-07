package com.github.finley243.adventureengine.world;

import com.github.finley243.adventureengine.Damage;
import com.github.finley243.adventureengine.world.environment.Area;

public interface AttackTarget {

    void damage(Damage damage);

    Area getArea();

}
