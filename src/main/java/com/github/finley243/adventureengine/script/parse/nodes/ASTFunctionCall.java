package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

import java.util.List;

public record ASTFunctionCall(String name, List<ASTNode> parameters, SourceRange range) implements ASTNode {
}
