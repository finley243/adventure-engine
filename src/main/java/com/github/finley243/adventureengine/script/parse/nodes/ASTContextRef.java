package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

public record ASTContextRef(String name, SourceRange range) implements ASTNode {
}
