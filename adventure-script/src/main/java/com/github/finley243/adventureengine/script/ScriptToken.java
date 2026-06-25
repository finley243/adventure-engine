package com.github.finley243.adventureengine.script;

public record ScriptToken(ScriptTokenType type, String value, int line, String fileName, int charStart, int charEnd) {

    @Override
    public String toString() {
        return type.toString() + (value != null ? ":" + value : "");
    }

}
