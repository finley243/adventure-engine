package com.github.finley243.adventureengine.script.parse;

import com.github.finley243.adventureengine.expression.Expression;

public record ScriptParameter(String name, boolean isRequired, Expression defaultValue) {
}
