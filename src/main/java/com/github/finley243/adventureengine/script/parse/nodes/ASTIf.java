package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

import java.util.List;

public record ASTIf(List<ScriptASTNode> branches, ScriptASTNode elseBranch, SourceRange range) implements ScriptASTNode {
}
