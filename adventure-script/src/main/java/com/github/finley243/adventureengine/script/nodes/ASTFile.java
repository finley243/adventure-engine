package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.HighlightData;
import com.github.finley243.adventureengine.script.SourceRange;

import java.util.ArrayList;
import java.util.List;

public record ASTFile(List<ASTFunction> functions, SourceRange range) implements ASTNode {
    @Override
    public List<HighlightData> highlightData() {
        List<HighlightData> highlights = new ArrayList<>();
        if (functions != null) {
            for (ASTNode functionNode : functions) {
                if (functionNode != null) highlights.addAll(functionNode.highlightData());
            }
        }
        return highlights;
    }
}
