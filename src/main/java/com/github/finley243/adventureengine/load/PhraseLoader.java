package com.github.finley243.adventureengine.load;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PhraseLoader {

    public Map<String, String> loadPhrases(File file) throws GameDataException {
        Scanner scanner;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new GameDataException("Could not open phrase file: " + file.getAbsolutePath());
        }
        Map<String, String> phrases = new HashMap<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] split = line.split(":");
            if (split.length != 2) throw new UnsupportedOperationException("Invalid phrase file format - line: " + line);
            phrases.put(split[0].trim(), split[1].trim());
        }
        scanner.close();
        return phrases;
    }

}
