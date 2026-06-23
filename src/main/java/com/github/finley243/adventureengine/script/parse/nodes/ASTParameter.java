package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

public record ASTParameter(String name, ASTNode value, SourceRange range) implements ASTNode {
}
