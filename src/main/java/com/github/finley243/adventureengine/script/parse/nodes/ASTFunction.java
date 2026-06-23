package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

import java.util.List;

public record ASTFunction(String name, String returnType, List<ASTNode> parameters, ASTNode body, SourceRange range) implements ASTNode {
}
