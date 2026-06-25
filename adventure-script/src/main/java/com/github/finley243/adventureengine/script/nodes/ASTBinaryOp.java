package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.SourceRange;

public record ASTBinaryOp(Operator operator, ASTNode left, ASTNode right, SourceRange range) implements ASTNode {
    public enum Operator {
        ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO, POWER, AND, OR,
        EQUAL, NOT_EQUAL, LESS, GREATER, LESS_EQUAL, GREATER_EQUAL, NULL_COALESCING
    }
}
