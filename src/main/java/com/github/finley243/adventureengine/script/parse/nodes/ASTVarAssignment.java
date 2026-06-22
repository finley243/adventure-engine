package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

public record ASTVarAssignment(String name, ScriptASTNode value, SourceRange range) implements ScriptASTNode {
}
