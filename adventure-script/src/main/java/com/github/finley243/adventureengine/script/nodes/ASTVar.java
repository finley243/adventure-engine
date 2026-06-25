package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.SourceRange;

public record ASTVar(String name, SourceRange range) implements ASTNode {
}
