package com.github.finley243.adventureengine.script.parse.nodes;

public record ASTTernaryOp(Operator operator, ScriptASTNode left, ScriptASTNode center, ScriptASTNode right) implements ScriptASTNode {
    public enum Operator {
        BRANCH
    }
}
