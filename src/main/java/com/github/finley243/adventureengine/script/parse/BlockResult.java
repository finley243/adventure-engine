package com.github.finley243.adventureengine.script.parse;

public record BlockResult(TokenStream contents, int charStart, int charEnd, String fileName, int line, BlockError error) {
}
