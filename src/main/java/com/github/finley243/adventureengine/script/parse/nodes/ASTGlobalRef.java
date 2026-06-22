package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

public record ASTGlobalRef(String name, SourceRange range) implements ScriptASTNode {
}
