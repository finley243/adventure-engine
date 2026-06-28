package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.HighlightData;
import com.github.finley243.adventureengine.script.HighlightType;
import com.github.finley243.adventureengine.script.SourceRange;

import java.util.ArrayList;
import java.util.List;

public record ASTIf(List<ASTIfBranch> branches, SourceRange elseKeywordRange, ASTCompound elseBranch, SourceRange range) implements ASTNode {
    @Override
    public List<HighlightData> highlightData() {
        List<HighlightData> highlights = new ArrayList<>();
        if (branches != null) {
            for (ASTNode branchNode : branches) {
                if (branchNode != null) highlights.addAll(branchNode.highlightData());
            }
        }
        if (elseKeywordRange != null) highlights.add(new HighlightData(HighlightType.KEYWORD, elseKeywordRange));
        if (elseBranch != null) highlights.addAll(elseBranch.highlightData());
        return highlights;
    }
}
