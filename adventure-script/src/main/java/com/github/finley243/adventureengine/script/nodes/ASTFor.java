package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.SourceRange;

public record ASTFor(String iteratorName, ASTNode collection, ASTNode body, SourceRange range) implements ASTNode {
}
