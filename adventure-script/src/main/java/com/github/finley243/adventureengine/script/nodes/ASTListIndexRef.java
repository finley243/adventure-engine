package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.SourceRange;

public record ASTListIndexRef(ASTNode list, ASTNode index, SourceRange range) implements ASTNode {
}
