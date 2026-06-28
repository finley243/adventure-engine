package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.HighlightData;
import com.github.finley243.adventureengine.script.HighlightType;
import com.github.finley243.adventureengine.script.SourceRange;

import java.util.ArrayList;
import java.util.List;

public record ASTParameter(SourceRange nameRange, String name, SourceRange assignmentRange, ASTNode value, SourceRange range) implements ASTNode {
    @Override
    public List<HighlightData> highlightData() {
        List<HighlightData> highlights = new ArrayList<>();
        if (nameRange != null) highlights.add(new HighlightData(HighlightType.NAMED_PARAMETER_REFERENCE, nameRange));
        if (assignmentRange != null) highlights.add(new HighlightData(HighlightType.PARAMETER_ASSIGNMENT, assignmentRange));
        if (value != null) highlights.addAll(value.highlightData());
        return highlights;
    }
}
