package com.github.finley243.adventureengine.script.parse;

import com.github.finley243.adventureengine.script.parse.nodes.ASTNode;

import java.util.List;

public record ASTParseResult(ASTNode node, List<CompileError> errors) {
}
