package com.github.finley243.adventureengine.load;

public class ScriptCompileException extends RuntimeException {

    private final String fileName;
    private final int lineNumber;

    public ScriptCompileException(String message, String fileName, int lineNumber) {
        super(message);
        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }

    public ScriptCompileException(String message, String fileName, int lineNumber, Throwable cause) {
        super(message, cause);
        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }

    public String getFileName() {
        return fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

}

