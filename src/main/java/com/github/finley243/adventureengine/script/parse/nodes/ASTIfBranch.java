package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

public record ASTIfBranch(ASTNode condition, ASTNode body, SourceRange range) implements ASTNode {
}
