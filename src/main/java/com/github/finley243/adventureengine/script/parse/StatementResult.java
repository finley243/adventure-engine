package com.github.finley243.adventureengine.script.parse;

public record StatementResult(TokenStream contents, int charStart, int charEnd, String fileName, int line, StatementError error) {
}
