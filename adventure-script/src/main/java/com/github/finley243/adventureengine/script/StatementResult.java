package com.github.finley243.adventureengine.script;

public record StatementResult(TokenStream contents, SourceRange range, StatementError error) {
}
