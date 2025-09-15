package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.condition.Condition;

import java.util.ArrayList;
import java.util.List;

public class StatInt extends Stat {

    private final List<StatIntMod> mods;

    public StatInt(String name, MutableStatHolder target) {
        super(name, target);
        this.mods = new ArrayList<>();
    }

    public int value(int base, int min, int max, Game game, Context context) {
        int add = 0;
        float mult = 0.0f;
        for (StatIntMod mod : mods) {
            if (mod.shouldApply(game, context)) {
                add += mod.add;
                mult += mod.mult;
            }
        }
        int computedValue = Math.round(base * (mult + 1.0f)) + add;
        return MathUtils.bound(computedValue, min, max);
    }

    public void addMod(StatIntMod mod, Game game) {
        mods.add(mod);
        getTarget().onStatChange(getName(), game);
    }

    public void removeMod(StatIntMod mod, Game game) {
        mods.remove(mod);
        getTarget().onStatChange(getName(), game);
    }

    public List<StatIntMod> getMods() {
        return mods;
    }

    public record StatIntMod(Condition condition, int add, float mult) {
        public boolean shouldApply(Game game, Context context) {
            return condition == null || condition.isMet(game, context);
        }
    }

}
