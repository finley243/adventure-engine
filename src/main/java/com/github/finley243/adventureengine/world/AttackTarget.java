package com.github.finley243.adventureengine.world;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.combat.Damage;
import com.github.finley243.adventureengine.world.environment.Area;

public interface AttackTarget {

    boolean canBeAttacked();

    ComputedDamage applyEffectsAndComputeDamage(Game game, Damage damage, Context context);

    void applyDamage(Game game, ComputedDamage computedDamage, Context context);

    Area getArea();

    boolean isVisible(Actor subject);

    public record ComputedDamage(int amount, Limb limb, boolean isKillingBlow) {}

}
