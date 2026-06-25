package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.SourceRange;

import java.util.List;

public record ASTIf(List<ASTIfBranch> branches, ASTCompound elseBranch, SourceRange range) implements ASTNode {
}
