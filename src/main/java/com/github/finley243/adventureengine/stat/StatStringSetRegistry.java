package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.script.ScriptRuntime;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class StatStringSetRegistry <T> extends StatStringSet {

    private final Registry<T> registry;
    private final Function<T, String> idFunction;

    public StatStringSetRegistry(String name, MutableStatHolder target, Registry<T> registry, Function<T, String> idFunction) {
        super(name, target);
        this.registry = registry;
        this.idFunction = idFunction;
    }

    public Set<T> valueObjects(Set<T> base, ScriptRuntime scriptRuntime, Context context) {
        Set<String> baseIDs = new HashSet<>();
        for (T baseObject : base) {
            String baseObjectID = idFunction.apply(baseObject);
            baseIDs.add(baseObjectID);
        }
        Set<String> valueIDs = this.value(baseIDs, scriptRuntime, context);
        Set<T> valueObjects = new HashSet<>();
        for (String valueID : valueIDs) {
            T valueObject = registry.getFromID(valueID);
            valueObjects.add(valueObject);
        }
        return valueObjects;
    }

}
