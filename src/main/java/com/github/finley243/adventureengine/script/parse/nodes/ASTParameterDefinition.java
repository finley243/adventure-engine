package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

public record ASTParameterDefinition(String name, ASTNode defaultValue, SourceRange range) implements ASTNode {
}
