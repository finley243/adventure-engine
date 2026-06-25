package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.SourceRange;

public record ASTParameterDefinition(String name, ASTLiteral defaultValue, SourceRange range) implements ASTNode {
}
