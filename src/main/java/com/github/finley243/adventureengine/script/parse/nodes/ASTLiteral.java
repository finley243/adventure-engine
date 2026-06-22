package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

public record ASTLiteral(Type type, String value, SourceRange range) implements ScriptASTNode {
    public enum Type {
        STRING, FLOAT, INTEGER, BOOLEAN, NULL
    }
}
