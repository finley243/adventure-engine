package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

public record ASTTernaryOp(Operator operator, ScriptASTNode left, ScriptASTNode center, ScriptASTNode right, SourceRange range) implements ScriptASTNode {
    public enum Operator {
        BRANCH
    }
}
