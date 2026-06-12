package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.GameDataException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ScriptLoader {

    private static final String SCRIPT_FILE_EXTENSION = "ascr";

    private final ScriptParser scriptParser;

    public ScriptLoader(ScriptParser scriptParser) {
        this.scriptParser = scriptParser;
    }

    public Map<String, ScriptParser.ScriptData> loadFromDir(File dir) {
        if (!dir.exists()) throw new IllegalArgumentException("Directory does not exist: " + dir.getAbsolutePath());
        if (!dir.isDirectory()) throw new IllegalArgumentException("Script path must be a directory: " + dir.getAbsolutePath());
        File[] files = dir.listFiles();
        Objects.requireNonNull(files);
        Map<String, ScriptParser.ScriptData> scriptDataMap = new HashMap<>();
        for (File file : files) {
            String fileExtension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            if (fileExtension.equalsIgnoreCase(SCRIPT_FILE_EXTENSION)) {
                String fileContents;
                try {
                    fileContents = Files.readString(file.toPath());
                } catch (IOException e) {
                    throw new GameDataException("Failed to read script file: " + file.getAbsolutePath());
                }
                List<ScriptParser.ScriptData> functions;
                try {
                    functions = scriptParser.parseFunctions(fileContents, file.getName());
                } catch (ScriptCompileException e) {
                    throw new GameDataException("Script parsing failure:\n" + e.getFileName() + ":" + e.getLineNumber() + " - " + e.getMessage());
                }
                for (ScriptParser.ScriptData function : functions) {
                    scriptDataMap.put(function.name(), function);
                }
            }
        }
        return scriptDataMap;
    }

}
