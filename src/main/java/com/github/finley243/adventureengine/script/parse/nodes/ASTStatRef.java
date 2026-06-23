package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

public record ASTStatRef(ASTNode holder, String name, SourceRange range) implements ASTNode {
}
