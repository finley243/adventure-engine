package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.HighlightData;
import com.github.finley243.adventureengine.script.HighlightType;
import com.github.finley243.adventureengine.script.SourceRange;

import java.util.ArrayList;
import java.util.List;

public record ASTUnaryOp(Operator operator, SourceRange operatorRange, ASTNode operand, SourceRange range) implements ASTNode {
    public enum Operator {
        NOT, NEGATE
    }

    @Override
    public List<HighlightData> highlightData() {
        List<HighlightData> highlights = new ArrayList<>();
        highlights.add(new HighlightData(HighlightType.OPERATOR, operatorRange));
        if (operand != null) highlights.addAll(operand.highlightData());
        return highlights;
    }
}
