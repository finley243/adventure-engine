package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.HighlightData;
import com.github.finley243.adventureengine.script.HighlightType;
import com.github.finley243.adventureengine.script.SourceRange;

import java.util.ArrayList;
import java.util.List;

public record ASTFunction(String name, SourceRange nameRange, List<ASTNode> parameters, ASTNode body, SourceRange range) implements ASTNode {
    @Override
    public List<HighlightData> highlightData() {
        List<HighlightData> highlights = new ArrayList<>();
        highlights.add(new HighlightData(HighlightType.FUNCTION_DEFINITION_NAME, nameRange));
        if (parameters != null) {
            for (ASTNode parameterNode : parameters) {
                if (parameterNode != null) highlights.addAll(parameterNode.highlightData());
            }
        }
        if (body != null) highlights.addAll(body.highlightData());
        return highlights;
    }
}
