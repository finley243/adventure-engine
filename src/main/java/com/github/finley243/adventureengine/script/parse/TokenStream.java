package com.github.finley243.adventureengine.script.parse;

import java.util.ArrayList;
import java.util.List;

public class TokenStream {

    private final List<ScriptToken> tokens;
    private int index;
    private final List<CompileError> errors;

    public TokenStream(List<ScriptToken> tokens) {
        this.tokens = tokens;
        this.errors = new ArrayList<>();
    }

    List<CompileError> getErrors() { return errors; }

    ScriptToken peek() { return tokens.get(index); }
    ScriptToken consume() { return tokens.get(index++); }
    boolean hasNext() { return index < tokens.size(); }

    boolean expect(ScriptTokenType type, String errorMessage) {
        if (peek().type() != type) {
            ScriptToken token = peek();
            errors.add(new CompileError(errorMessage, token.fileName(), token.line(), token.charStart(), token.charEnd()));
            return false;
        }
        consume();
        return true;
    }

    TokenStream consumeBlock(ScriptTokenType open, ScriptTokenType close, String missingOpenErrorMessage) {
        expect(open, missingOpenErrorMessage);
        int start = index;
        int depth = 1;
        while (depth > 0) {
            ScriptTokenType t = consume().type();
            if (t == open) depth++;
            else if (t == close) depth--;
        }
        return new TokenStream(tokens.subList(start, index - 1));
    }

}
