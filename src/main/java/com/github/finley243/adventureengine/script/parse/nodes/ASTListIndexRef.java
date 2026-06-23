package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

public record ASTListIndexRef(ASTNode list, int index, SourceRange range) implements ASTNode {
}
