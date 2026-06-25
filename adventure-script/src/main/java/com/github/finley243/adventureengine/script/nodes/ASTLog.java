package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.SourceRange;

public record ASTLog(ASTNode message, SourceRange range) implements ASTNode {
}
