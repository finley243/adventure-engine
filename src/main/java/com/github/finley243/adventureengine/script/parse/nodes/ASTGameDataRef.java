package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

public record ASTGameDataRef(String type, ASTNode id, SourceRange range) implements ASTNode {
}
