package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.script.ScriptRuntime;

import java.util.function.Function;

public class StringRegistryStat<T> extends StringStat {

    private final Registry<T> registry;
    private final Function<T, String> idFunction;

    public StringRegistryStat(String name, StatHolder target, ScriptRuntime scriptRuntime, Registry<T> registry, Function<T, String> idFunction) {
        super(name, target, scriptRuntime);
        this.registry = registry;
        this.idFunction = idFunction;
    }

    public T valueObject(T base, Context context) {
        String baseID = idFunction.apply(base);
        String valueString = this.value(baseID, context);
        return registry.getFromID(valueString);
    }

}
