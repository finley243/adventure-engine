package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.HighlightData;
import com.github.finley243.adventureengine.script.HighlightType;
import com.github.finley243.adventureengine.script.SourceRange;

import java.util.ArrayList;
import java.util.List;

public record ASTBinaryOp(Operator operator, SourceRange operatorRange, ASTNode left, ASTNode right, SourceRange range) implements ASTNode {
    public enum Operator {
        ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO, POWER, AND, OR,
        EQUAL, NOT_EQUAL, LESS, GREATER, LESS_EQUAL, GREATER_EQUAL, NULL_COALESCING
    }

    @Override
    public List<HighlightData> highlightData() {
        List<HighlightData> highlights = new ArrayList<>();
        if (left != null) highlights.addAll(left.highlightData());
        if (operatorRange != null) highlights.add(new HighlightData(HighlightType.OPERATOR, operatorRange));
        if (right != null) highlights.addAll(right.highlightData());
        return highlights;
    }
}
