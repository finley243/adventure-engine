package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

public record ASTGlobalAssignment(ASTGlobalRef name, ASTNode value, SourceRange range) implements ASTNode {
}
