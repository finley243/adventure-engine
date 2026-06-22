package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

import java.util.List;

public record ASTCompound(List<ScriptASTNode> statements, SourceRange range) implements ScriptASTNode {
}
