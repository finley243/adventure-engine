package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.SourceRange;

import java.util.List;

public record ASTCompound(List<ASTNode> statements, SourceRange range) implements ASTNode {
}
