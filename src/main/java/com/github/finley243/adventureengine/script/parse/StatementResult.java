package com.github.finley243.adventureengine.script.parse;

public record StatementResult(TokenStream contents, SourceRange range, StatementError error) {
}
