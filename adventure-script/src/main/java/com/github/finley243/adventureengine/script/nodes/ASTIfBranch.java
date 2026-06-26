package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.HighlightData;
import com.github.finley243.adventureengine.script.HighlightType;
import com.github.finley243.adventureengine.script.SourceRange;

import java.util.ArrayList;
import java.util.List;

public record ASTIfBranch(SourceRange keywordRange, ASTNode condition, ASTNode body, SourceRange range) implements ASTNode {
    @Override
    public List<HighlightData> highlightData() {
        List<HighlightData> highlights = new ArrayList<>();
        highlights.add(new HighlightData(HighlightType.KEYWORD, keywordRange));
        if (condition != null) highlights.addAll(condition.highlightData());
        if (body != null) highlights.addAll(body.highlightData());
        return highlights;
    }
}
