package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.script.nodes.ASTNode;

import java.util.List;

public record ASTParseResult(ASTNode node, List<CompileError> errors) {
}
