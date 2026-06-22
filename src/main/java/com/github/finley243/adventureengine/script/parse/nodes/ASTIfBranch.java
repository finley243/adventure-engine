package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

public record ASTIfBranch(ScriptASTNode condition, ScriptASTNode body, SourceRange range) implements ScriptASTNode {
}
