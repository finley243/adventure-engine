package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

public record ASTFor(String iteratorName, ASTNode collection, ASTNode body, SourceRange range) implements ASTNode {
}
