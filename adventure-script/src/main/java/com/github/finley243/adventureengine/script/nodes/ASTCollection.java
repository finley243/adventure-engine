package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.HighlightData;
import com.github.finley243.adventureengine.script.HighlightType;
import com.github.finley243.adventureengine.script.SourceRange;

import java.util.ArrayList;
import java.util.List;

public record ASTCollection(Type type, SourceRange keywordRange, List<ASTNode> elements, SourceRange range) implements ASTNode {
    public enum Type {
        SET, LIST
    }

    @Override
    public List<HighlightData> highlightData() {
        List<HighlightData> highlights = new ArrayList<>();
        highlights.add(new HighlightData(HighlightType.KEYWORD, keywordRange));
        if (elements != null) {
            for (ASTNode elementNode : elements) {
                if (elementNode != null) highlights.addAll(elementNode.highlightData());
            }
        }
        return highlights;
    }
}
