package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.HighlightData;
import com.github.finley243.adventureengine.script.SourceRange;

import java.util.ArrayList;
import java.util.List;

public record ASTCompound(List<ASTNode> statements, SourceRange range) implements ASTNode {
    @Override
    public List<HighlightData> highlightData() {
        List<HighlightData> highlights = new ArrayList<>();
        if (statements != null) {
            for (ASTNode statementNode : statements) {
                if (statementNode != null) highlights.addAll(statementNode.highlightData());
            }
        }
        return highlights;
    }
}
