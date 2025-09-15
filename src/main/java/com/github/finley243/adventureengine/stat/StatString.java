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

    public String value(String base, Game game, Context context) {
        String topValue = null;
        for (StatStringMod mod : mods) {
            if (mod.shouldApply(game, context)) {
                topValue = mod.value;
            }
        }
        return topValue == null ? base : topValue;
    }

    public <E extends Enum<E>> E valueEnum(E base, Class<E> enumType, Game game, Context context) {
        String topValue = null;
        for (StatStringMod mod : mods) {
            if (mod.shouldApply(game, context)) {
                topValue = mod.value;
            }
        }
        return topValue == null ? base : LoadUtils.stringToEnum(topValue, enumType);
    }

    public void addMod(StatStringMod mod, Game game) {
        mods.add(mod);
        getTarget().onStatChange(getName(), game);
    }

    public void removeMod(StatStringMod mod, Game game) {
        mods.remove(mod);
        getTarget().onStatChange(getName(), game);
    }

    public List<StatStringMod> getMods() {
        return mods;
    }

    public record StatStringMod(Condition condition, String value) {
        public boolean shouldApply(Game game, Context context) {
            return condition == null || condition.isMet(game, context);
        }
    }

}
