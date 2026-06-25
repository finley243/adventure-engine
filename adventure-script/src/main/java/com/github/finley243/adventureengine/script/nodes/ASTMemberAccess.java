package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.SourceRange;

public record ASTMemberAccess(ASTNode object, ASTMemberName name, SourceRange range) implements ASTNode {
}
