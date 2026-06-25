package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.SourceRange;

public record ASTGlobalRef(ASTNode name, SourceRange range) implements ASTNode {
}
