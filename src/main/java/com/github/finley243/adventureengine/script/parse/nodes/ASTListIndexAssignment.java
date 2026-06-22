package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

public record ASTListIndexAssignment(ScriptASTNode list, int index, ScriptASTNode value, SourceRange range) implements ScriptASTNode {
}
