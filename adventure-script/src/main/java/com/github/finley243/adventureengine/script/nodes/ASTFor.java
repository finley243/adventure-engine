package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.HighlightData;
import com.github.finley243.adventureengine.script.HighlightType;
import com.github.finley243.adventureengine.script.SourceRange;

import java.util.ArrayList;
import java.util.List;

public record ASTFor(SourceRange keywordRange, SourceRange iteratorRange, String iteratorName, SourceRange separatorRange, ASTNode collection, ASTNode body, SourceRange range) implements ASTNode {
    @Override
    public List<HighlightData> highlightData() {
        List<HighlightData> highlights = new ArrayList<>();
        highlights.add(new HighlightData(HighlightType.KEYWORD, keywordRange));
        if (iteratorRange != null) highlights.add(new HighlightData(HighlightType.VARIABLE, iteratorRange));
        if (separatorRange != null) highlights.add(new HighlightData(HighlightType.OPERATOR, separatorRange));
        if (collection != null) highlights.addAll(collection.highlightData());
        if (body != null) highlights.addAll(body.highlightData());
        return highlights;
    }
}
