package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.SourceRange;

public record ASTIfBranch(ASTNode condition, ASTNode body, SourceRange range) implements ASTNode {
}
