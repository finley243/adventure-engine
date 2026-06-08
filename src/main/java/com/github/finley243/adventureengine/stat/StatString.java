package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.load.LoadUtils;

import java.util.ArrayList;
import java.util.List;

public class StatString extends Stat {

    private final List<StatStringMod> mods;

    public StatString(String name, MutableStatHolder target) {
        super(name, target);
        this.mods = new ArrayList<>();
    }

    public String value(String base, Context context) {
        String topValue = null;
        for (StatStringMod mod : mods) {
            if (mod.shouldApply(context)) {
                topValue = mod.value;
            }
        }
        return topValue == null ? base : topValue;
    }

    public <E extends Enum<E>> E valueEnum(E base, Class<E> enumType, Context context) {
        String topValue = null;
        for (StatStringMod mod : mods) {
            if (mod.shouldApply(context)) {
                topValue = mod.value;
            }
        }
        return topValue == null ? base : LoadUtils.stringToEnum(topValue, enumType);
    }

    public void addMod(Game game, StatStringMod mod) {
        mods.add(mod);
        getTarget().onStatChange(game, getName());
    }

    public void removeMod(Game game, StatStringMod mod) {
        mods.remove(mod);
        getTarget().onStatChange(game, getName());
    }

    public List<StatStringMod> getMods() {
        return mods;
    }

    public record StatStringMod(Condition condition, String value) {
        public boolean shouldApply(Context context) {
            return condition == null || condition.isMet(context);
        }
    }

}
