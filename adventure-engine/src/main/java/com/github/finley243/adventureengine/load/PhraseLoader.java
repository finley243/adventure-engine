package com.github.finley243.adventureengine.load;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhraseLoader {

    private static final Pattern PHRASE_REFERENCE_PATTERN = Pattern.compile("@([a-zA-Z0-9_]+)");

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
            if (split.length != 2) throw new GameDataException("Invalid phrase file format - line: " + line);
            phrases.put(split[0].trim(), split[1].trim());
        }
        scanner.close();
        confirmNoReferenceCycles(phrases);
        return phrases;
    }

    private void confirmNoReferenceCycles(Map<String, String> phrases) {
        Set<String> fullyExplored = new HashSet<>();
        Set<String> exploring = new HashSet<>();
        for (String phraseID : phrases.keySet()) {
            if (!fullyExplored.contains(phraseID)) {
                detectCycle(phraseID, phrases, exploring, fullyExplored, new ArrayDeque<>());
            }
        }
    }

    private void detectCycle(String phraseID, Map<String, String> phrases, Set<String> exploring, Set<String> fullyExplored, Deque<String> path) {
        exploring.add(phraseID);
        path.addLast(phraseID);
        String value = phrases.get(phraseID);
        if (value != null) {
            Matcher matcher = PHRASE_REFERENCE_PATTERN.matcher(value);
            while (matcher.find()) {
                String ref = matcher.group(1);
                if (exploring.contains(ref)) {
                    List<String> cycle = new ArrayList<>(path);
                    cycle.add(ref);
                    throw new GameDataException("Cycle in phrase references: " + String.join(" -> ", cycle));
                }
                if (!fullyExplored.contains(ref) && phrases.containsKey(ref)) {
                    detectCycle(ref, phrases, exploring, fullyExplored, path);
                }
            }
        }
        path.removeLast();
        exploring.remove(phraseID);
        fullyExplored.add(phraseID);
    }

}
