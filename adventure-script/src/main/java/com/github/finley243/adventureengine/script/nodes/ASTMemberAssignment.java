package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.SourceRange;

public record ASTMemberAssignment(ASTMemberAccess holder, ASTNode value, SourceRange range) implements ASTNode {
}
