package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.load.LoadUtils;
import com.github.finley243.adventureengine.script.ScriptRuntime;

import java.util.ArrayList;
import java.util.List;

public class StringStat extends Stat {

    private final List<StatStringMod> mods;

    public StringStat(String name, StatHolder target, ScriptRuntime scriptRuntime) {
        super(name, target, scriptRuntime);
        this.mods = new ArrayList<>();
    }

    public String value(String base, Context context) {
        String topValue = null;
        for (StatStringMod mod : mods) {
            if (shouldApplyMod(mod.condition(), context)) {
                topValue = mod.value;
            }
        }
        return topValue == null ? base : topValue;
    }

    public <E extends Enum<E>> E valueEnum(E base, Class<E> enumType, Context context) {
        String topValue = null;
        for (StatStringMod mod : mods) {
            if (shouldApplyMod(mod.condition(), context)) {
                topValue = mod.value;
            }
        }
        return topValue == null ? base : LoadUtils.stringToEnum(topValue, enumType);
    }

    public void addMod(StatStringMod mod) {
        mods.add(mod);
        getTarget().onStatChange(getName());
    }

    public void removeMod(StatStringMod mod) {
        mods.remove(mod);
        getTarget().onStatChange(getName());
    }

    public List<StatStringMod> getMods() {
        return mods;
    }

    public record StatStringMod(Condition condition, String value) {}

}
