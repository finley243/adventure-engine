package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.script.Script;

import java.util.ArrayList;
import java.util.List;

public class StatInt extends Stat {

    private final List<StatIntMod> mods;

    public StatInt(String name, MutableStatHolder target) {
        super(name, target);
        this.mods = new ArrayList<>();
    }

    public int value(int base, int min, int max, Context context) {
        int add = 0;
        float mult = 0.0f;
        for (StatIntMod mod : mods) {
            if (mod.shouldApply(context)) {
                add += mod.add;
                mult += mod.mult;
            }
        }
        int computedValue = Math.round(base * (mult + 1.0f)) + add;
        return MathUtils.bound(computedValue, min, max);
    }

    public void addMod(StatIntMod mod) {
        mods.add(mod);
        getTarget().onStatChange(getName());
    }

    public void removeMod(StatIntMod mod) {
        mods.remove(mod);
        getTarget().onStatChange(getName());
    }

    public List<StatIntMod> getMods() {
        return mods;
    }

    public record StatIntMod(Script condition, int add, float mult) {
        public boolean shouldApply(Context context) {
            if (condition == null) return true;
            Expression conditionResult = condition.execute().value();
            if (conditionResult.getDataType(context) != Expression.DataType.BOOLEAN) throw new IllegalArgumentException("Condition provided non-boolean value");
            return conditionResult.getValueBoolean(context);
        }
    }

}
