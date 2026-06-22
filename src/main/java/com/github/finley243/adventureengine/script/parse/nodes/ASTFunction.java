package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

import java.util.List;

public record ASTFunction(String name, String returnType, List<ScriptASTNode> parameters, ScriptASTNode body, SourceRange range) implements ScriptASTNode {
}
