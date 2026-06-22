package com.github.finley243.adventureengine.script.parse.nodes;

public record ASTUnaryOp(Operator operator, ScriptASTNode operand) implements ScriptASTNode {
    public enum Operator {
        NOT
    }
}
