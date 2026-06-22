package com.github.finley243.adventureengine.script.parse.nodes;

public record ASTBinaryOp(Operator operator, ScriptASTNode left, ScriptASTNode right) implements ScriptASTNode {
    public enum Operator {
        ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO, POWER, AND, OR,
        EQUAL, NOT_EQUAL, LESS, GREATER, LESS_EQUAL, GREATER_EQUAL
    }
}
