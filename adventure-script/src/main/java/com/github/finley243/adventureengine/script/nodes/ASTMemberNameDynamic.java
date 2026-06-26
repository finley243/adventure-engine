package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.HighlightData;

import java.util.ArrayList;
import java.util.List;

public record ASTMemberNameDynamic(ASTNode expression) implements ASTMemberName {
    @Override
    public List<HighlightData> highlightData() {
        List<HighlightData> highlights = new ArrayList<>();
        if (expression != null) highlights.addAll(expression.highlightData());
        return highlights;
    }
}
