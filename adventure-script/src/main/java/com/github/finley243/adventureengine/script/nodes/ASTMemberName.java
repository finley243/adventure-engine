package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.HighlightData;

import java.util.List;

public sealed interface ASTMemberName permits ASTMemberNameStatic, ASTMemberNameDynamic {
    List<HighlightData> highlightData();
}
