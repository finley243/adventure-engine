package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.HighlightData;
import com.github.finley243.adventureengine.script.SourceRange;

import java.util.ArrayList;
import java.util.List;

public record ASTMemberAccess(ASTNode object, ASTMemberName name, SourceRange range) implements ASTNode {
    @Override
    public List<HighlightData> highlightData() {
        List<HighlightData> highlights = new ArrayList<>();
        if (object != null) highlights.addAll(object.highlightData());
        if (name != null) highlights.addAll(name.highlightData());
        return highlights;
    }

}
