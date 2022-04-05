package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.ModdableStat;
import com.github.finley243.adventureengine.actor.Actor;

public class EffectStat extends Effect {

    private final String stat;

    public EffectStat(int duration, boolean manualRemoval, int amount, String stat) {
        super(duration, manualRemoval, amount);
        this.stat = stat;
    }

    @Override
    public void start(Actor target) {
        ModdableStat moddableStat = target.getStat(stat);
        if(moddableStat != null) {
            moddableStat.addMod(amount);
        }
    }

    @Override
    public void end(Actor target) {
        ModdableStat moddableStat = target.getStat(stat);
        if(moddableStat != null) {
            moddableStat.addMod(-amount);
        }
    }

    @Override
    public void eachTurn(Actor target) {

    }

}
