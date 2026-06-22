package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

import java.util.List;

public record ASTCollection(Type type, List<ScriptASTNode> elements, SourceRange range) implements ScriptASTNode {
    public enum Type {
        SET, LIST
    }
}
