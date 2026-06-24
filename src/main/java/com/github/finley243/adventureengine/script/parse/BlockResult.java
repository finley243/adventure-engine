package com.github.finley243.adventureengine.script.parse;

public record BlockResult(TokenStream contents, SourceRange range, BlockError error) {
}
