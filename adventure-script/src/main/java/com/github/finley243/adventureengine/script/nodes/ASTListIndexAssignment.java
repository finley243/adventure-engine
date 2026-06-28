package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.HighlightData;
import com.github.finley243.adventureengine.script.HighlightType;
import com.github.finley243.adventureengine.script.SourceRange;

import java.util.ArrayList;
import java.util.List;

public record ASTListIndexAssignment(ASTListIndexRef listIndexRef, SourceRange operatorRange, ASTNode value, SourceRange range) implements ASTNode {
    @Override
    public List<HighlightData> highlightData() {
        List<HighlightData> highlights = new ArrayList<>();
        if (listIndexRef != null) highlights.addAll(listIndexRef.highlightData());
        if (operatorRange != null) highlights.add(new HighlightData(HighlightType.OPERATOR, operatorRange));
        if (value != null) highlights.addAll(value.highlightData());
        return highlights;
    }
}
