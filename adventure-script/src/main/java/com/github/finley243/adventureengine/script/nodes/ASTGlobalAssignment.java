package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.SourceRange;

public record ASTGlobalAssignment(ASTGlobalRef name, ASTNode value, SourceRange range) implements ASTNode {
}
