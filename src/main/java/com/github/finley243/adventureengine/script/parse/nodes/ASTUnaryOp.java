package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

public record ASTUnaryOp(Operator operator, ScriptASTNode operand, SourceRange range) implements ScriptASTNode {
    public enum Operator {
        NOT
    }
}
