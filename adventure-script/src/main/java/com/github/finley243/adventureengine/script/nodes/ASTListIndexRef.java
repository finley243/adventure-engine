package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.HighlightData;
import com.github.finley243.adventureengine.script.SourceRange;

import java.util.ArrayList;
import java.util.List;

public record ASTListIndexRef(ASTNode list, ASTNode index, SourceRange range) implements ASTNode {
    @Override
    public List<HighlightData> highlightData() {
        List<HighlightData> highlights = new ArrayList<>();
        if (list != null) highlights.addAll(list.highlightData());
        if (index != null) highlights.addAll(index.highlightData());
        return highlights;
    }
}
