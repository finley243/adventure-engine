package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.SourceRange;

public record ASTUnaryOp(Operator operator, ASTNode operand, SourceRange range) implements ASTNode {
    public enum Operator {
        NOT, NEGATE
    }
}
