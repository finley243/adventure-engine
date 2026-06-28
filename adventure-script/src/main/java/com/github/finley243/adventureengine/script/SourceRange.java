package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.script.nodes.ASTNode;

public record SourceRange(int start, int end, String fileName, int line) {
    public SourceRange(ScriptToken token) {
        this(token.charStart(), token.charEnd(), token.fileName(), token.line());
    }
    public SourceRange(ScriptToken startToken, ScriptToken endToken) {
        this(startToken.charStart(), endToken.charEnd(), startToken.fileName(), startToken.line());
    }
    public SourceRange(ASTNode startNode, ScriptToken endToken) {
        this(startNode.range().start(), endToken.charEnd(), startNode.range().fileName(), startNode.range().line());
    }
}
