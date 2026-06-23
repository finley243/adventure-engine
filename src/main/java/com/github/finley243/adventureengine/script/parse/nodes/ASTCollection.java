package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

import java.util.List;

public record ASTCollection(Type type, List<ASTNode> elements, SourceRange range) implements ASTNode {
    public enum Type {
        SET, LIST
    }
}
