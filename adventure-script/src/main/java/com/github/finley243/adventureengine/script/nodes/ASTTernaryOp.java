package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.HighlightData;
import com.github.finley243.adventureengine.script.HighlightType;
import com.github.finley243.adventureengine.script.SourceRange;

import java.util.ArrayList;
import java.util.List;

public record ASTTernaryOp(ASTNode left, SourceRange firstOpRange, ASTNode center, SourceRange secondOpRange, ASTNode right, SourceRange range) implements ASTNode {
    @Override
    public List<HighlightData> highlightData() {
        List<HighlightData> highlights = new ArrayList<>();
        if (left != null) highlights.addAll(left.highlightData());
        if (firstOpRange != null) highlights.add(new HighlightData(HighlightType.OPERATOR, firstOpRange));
        if (center != null) highlights.addAll(center.highlightData());
        if (secondOpRange != null) highlights.add(new HighlightData(HighlightType.OPERATOR, secondOpRange));
        if (right != null) highlights.addAll(right.highlightData());
        return highlights;
    }
}
