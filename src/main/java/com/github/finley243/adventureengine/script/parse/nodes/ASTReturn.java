package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

public record ASTReturn(ASTNode value, SourceRange range) implements ASTNode {
}
