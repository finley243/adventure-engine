package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.condition.Condition;

import java.util.ArrayList;
import java.util.List;

public class StatFloat extends Stat {

    private final List<StatFloatMod> mods;

    public StatFloat(String name, MutableStatHolder target) {
        super(name, target);
        this.mods = new ArrayList<>();
    }

    public float value(float base, float min, float max, Game game, Context context) {
        float add = 0.0f;
        float mult = 0.0f;
        for (StatFloatMod mod : mods) {
            if (mod.shouldApply(game, context)) {
                add += mod.add;
                mult += mod.mult;
            }
        }
        float computedValue = (base * (mult + 1.0f)) + add;
        return Math.min(Math.max(computedValue, min), max);
    }

    public void addMod(StatFloatMod mod, Game game) {
        mods.add(mod);
        getTarget().onStatChange(getName(), game);
    }

    public void removeMod(StatFloatMod mod, Game game) {
        mods.remove(mod);
        getTarget().onStatChange(getName(), game);
    }

    public List<StatFloatMod> getMods() {
        return mods;
    }

    public record StatFloatMod(Condition condition, float add, float mult) {
        public boolean shouldApply(Game game, Context context) {
            return condition == null || condition.isMet(game, context);
        }
    }

}
