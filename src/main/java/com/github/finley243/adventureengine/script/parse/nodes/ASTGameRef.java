package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

public record ASTGameRef(String name, SourceRange range) implements ScriptASTNode {
}
