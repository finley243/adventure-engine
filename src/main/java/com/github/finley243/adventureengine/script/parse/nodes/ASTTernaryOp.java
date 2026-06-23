package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

public record ASTTernaryOp(Operator operator, ASTNode left, ASTNode center, ASTNode right, SourceRange range) implements ASTNode {
    public enum Operator {
        BRANCH
    }
}
