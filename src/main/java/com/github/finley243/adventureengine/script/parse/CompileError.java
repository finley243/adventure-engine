package com.github.finley243.adventureengine.script.parse;

public record CompileError(String message, SourceRange range) {}
