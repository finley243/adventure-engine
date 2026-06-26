package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.HighlightData;
import com.github.finley243.adventureengine.script.HighlightType;
import com.github.finley243.adventureengine.script.SourceRange;

import java.util.List;

public record ASTContinue(SourceRange range) implements ASTNode {
    @Override
    public List<HighlightData> highlightData() {
        return List.of(new HighlightData(HighlightType.KEYWORD, range));
    }
}
