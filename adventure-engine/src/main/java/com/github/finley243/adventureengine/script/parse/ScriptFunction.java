package com.github.finley243.adventureengine.script.parse;

import com.github.finley243.adventureengine.script.Script;

import java.util.List;

public record ScriptFunction(String name, List<ScriptParameter> parameters, boolean allowExtraParameters,
                             Script script) {
}
