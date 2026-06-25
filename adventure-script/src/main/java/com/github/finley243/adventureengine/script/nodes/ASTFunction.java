package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.SourceRange;

import java.util.List;

public record ASTFunction(String name, List<ASTNode> parameters, ASTNode body, SourceRange range) implements ASTNode {
}
