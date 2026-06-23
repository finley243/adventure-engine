package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

public record ASTError(ASTNode message, SourceRange range) implements ASTNode {
}
