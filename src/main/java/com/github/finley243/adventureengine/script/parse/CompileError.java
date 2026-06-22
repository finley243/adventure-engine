package com.github.finley243.adventureengine.script.parse;

public record CompileError(String message, String fileName, int fileLine, int charStart, int charEnd) {}
