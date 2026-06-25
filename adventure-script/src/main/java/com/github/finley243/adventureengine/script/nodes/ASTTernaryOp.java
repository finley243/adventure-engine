package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.SourceRange;

public record ASTTernaryOp(ASTNode left, ASTNode center, ASTNode right, SourceRange range) implements ASTNode {
}
