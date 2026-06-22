package com.github.finley243.adventureengine.script.parse;

import org.jspecify.annotations.NonNull;

public record ScriptToken(ScriptTokenType type, String value, int line, String fileName, int charStart, int charEnd) {

    @Override
    @NonNull
    public String toString() {
        return type.toString() + (value != null ? ":" + value : "");
    }

}
