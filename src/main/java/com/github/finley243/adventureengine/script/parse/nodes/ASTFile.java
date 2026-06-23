package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

import java.util.List;

public record ASTFile(List<ASTNode> functions, SourceRange range) implements ASTNode {
}
