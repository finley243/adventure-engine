package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

public record ASTListIndexAssignment(ASTNode list, int index, ASTNode value, SourceRange range) implements ASTNode {
}
