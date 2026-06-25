package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.SourceRange;

public record ASTLiteral(Type type, String value, SourceRange range) implements ASTNode {
    public enum Type {
        STRING, FLOAT, INTEGER, BOOLEAN, NULL
    }
}
