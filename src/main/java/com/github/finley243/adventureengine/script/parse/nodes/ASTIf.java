package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

import java.util.List;

public record ASTIf(List<ASTNode> branches, ASTNode elseBranch, SourceRange range) implements ASTNode {
}
