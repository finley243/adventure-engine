package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.SourceRange;

public record ASTError(ASTNode message, SourceRange range) implements ASTNode {
}
