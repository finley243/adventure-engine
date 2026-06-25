package com.github.finley243.adventureengine.script;

import java.util.List;

public class ScriptCompileException extends RuntimeException {

    private final List<CompileError> errors;

    public ScriptCompileException(List<CompileError> errors) {
        StringBuilder sb = new StringBuilder("Script validation failed:\n");
        for (CompileError error : errors) {
            sb.append(" [").append(error.range().fileName()).append(":").append(error.range().line()).append("] ").append(error.message()).append("\n");
        }
        super(sb.toString());
        this.errors = errors;
    }

    public List<CompileError> getErrors() {
        return errors;
    }

}

