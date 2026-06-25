package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.SourceRange;

import java.util.List;

public record ASTFunctionCall(String name, List<ASTNode> parameters, SourceRange range) implements ASTNode {
}
