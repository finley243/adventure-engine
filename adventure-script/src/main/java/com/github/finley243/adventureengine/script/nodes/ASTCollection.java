package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.SourceRange;

import java.util.List;

public record ASTCollection(Type type, List<ASTNode> elements, SourceRange range) implements ASTNode {
    public enum Type {
        SET, LIST
    }
}
