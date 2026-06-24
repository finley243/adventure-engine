package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

public record ASTMemberAccess(ASTNode object, ASTMemberName name, SourceRange range) implements ASTNode {
}
