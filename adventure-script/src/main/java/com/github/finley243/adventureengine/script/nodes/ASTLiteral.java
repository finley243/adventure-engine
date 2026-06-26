package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.HighlightData;
import com.github.finley243.adventureengine.script.HighlightType;
import com.github.finley243.adventureengine.script.SourceRange;

import java.util.ArrayList;
import java.util.List;

public record ASTLiteral(Type type, String value, SourceRange range) implements ASTNode {
    public enum Type {
        STRING, FLOAT, INTEGER, BOOLEAN, NULL
    }

    @Override
    public List<HighlightData> highlightData() {
        List<HighlightData> highlights = new ArrayList<>();
        HighlightType highlightType = switch (type) {
            case STRING -> HighlightType.STRING;
            case FLOAT, INTEGER -> HighlightType.NUMBER;
            case BOOLEAN -> HighlightType.BOOLEAN;
            case NULL -> HighlightType.NULL;
        };
        highlights.add(new HighlightData(highlightType, range));
        return highlights;
    }
}
