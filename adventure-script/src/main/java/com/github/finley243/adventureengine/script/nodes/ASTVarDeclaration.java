package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.SourceRange;

public record ASTVarDeclaration(String name, ASTNode value, SourceRange range) implements ASTNode {
}
