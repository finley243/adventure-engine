package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

public record ASTMemberAssignment(ASTMemberAccess holder, ASTNode value, SourceRange range) implements ASTNode {
}
