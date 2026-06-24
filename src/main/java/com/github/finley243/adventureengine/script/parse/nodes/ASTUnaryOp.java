package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

public record ASTUnaryOp(Operator operator, ASTNode operand, SourceRange range) implements ASTNode {
    public enum Operator {
        NOT, NEGATE
    }
}
