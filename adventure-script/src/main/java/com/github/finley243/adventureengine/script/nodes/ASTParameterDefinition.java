package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.HighlightData;
import com.github.finley243.adventureengine.script.HighlightType;
import com.github.finley243.adventureengine.script.SourceRange;

import java.util.ArrayList;
import java.util.List;

public record ASTParameterDefinition(SourceRange nameRange, String name, SourceRange assignmentRange, ASTLiteral defaultValue, SourceRange range) implements ASTNode {
    @Override
    public List<HighlightData> highlightData() {
        List<HighlightData> highlights = new ArrayList<>();
        highlights.add(new HighlightData(HighlightType.PARAMETER_DEFINITION, nameRange));
        if (assignmentRange != null) highlights.add(new HighlightData(HighlightType.PARAMETER_ASSIGNMENT, assignmentRange));
        if (defaultValue != null) highlights.addAll(defaultValue.highlightData());
        return highlights;
    }
}
