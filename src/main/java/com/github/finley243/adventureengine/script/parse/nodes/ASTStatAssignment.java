package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

public record ASTStatAssignment(ASTNode holder, String name, ASTNode value, SourceRange range) implements ASTNode {
}
