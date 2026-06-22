package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

public record ASTLog(String message, SourceRange range) implements ScriptASTNode {
}
