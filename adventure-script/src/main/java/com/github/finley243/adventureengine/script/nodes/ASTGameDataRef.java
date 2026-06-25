package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.SourceRange;

public record ASTGameDataRef(String type, ASTNode id, SourceRange range) implements ASTNode {
}
