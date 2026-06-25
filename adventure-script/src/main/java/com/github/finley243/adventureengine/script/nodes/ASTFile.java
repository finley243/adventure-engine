package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.SourceRange;

import java.util.List;

public record ASTFile(List<ASTFunction> functions, SourceRange range) implements ASTNode {
}
