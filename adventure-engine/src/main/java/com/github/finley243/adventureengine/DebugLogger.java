package com.github.finley243.adventureengine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DebugLogger {

    private static final String TIMESTAMP_FORMAT_PATTERN = "uuuu-MM-dd HH:mm:ss:SS";
    private static final String FILE_TIMESTAMP_FORMAT_PATTERN = "uuuu_MM_dd_HH_mm_ss_SS";

    private static DebugLogger instance;

    private final DateTimeFormatter timestampFormatter;
    private final BufferedWriter bufferedWriter;

    private DebugLogger(String logDirectory) throws IOException {
        this.timestampFormatter = DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT_PATTERN);
        String fileName = logDirectory + "/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern(FILE_TIMESTAMP_FORMAT_PATTERN)) + ".txt";
        File file = new File(fileName);
        Files.createDirectories(Paths.get(logDirectory));
        boolean createdLogFile = file.createNewFile();
        if (!createdLogFile) throw new IOException("Log file could not be created");
        FileWriter fileWriter = new FileWriter(file);
        this.bufferedWriter = new BufferedWriter(fileWriter);
    }

    private void printInstance(String text) {
        System.out.println(text);
        String timestamp = LocalDateTime.now().format(timestampFormatter);
        try {
            bufferedWriter.write(timestamp + " - " + text);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void init(String logDirectory) {
        if (instance == null) {
            try {
                instance = new DebugLogger(logDirectory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void print(String text) {
        if (instance != null) {
            instance.printInstance(text);
        }
    }

}
