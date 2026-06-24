package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

public record ASTGlobalRef(ASTNode name, SourceRange range) implements ASTNode {
}
