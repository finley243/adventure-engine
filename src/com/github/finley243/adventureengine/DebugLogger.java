package com.github.finley243.adventureengine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DebugLogger {

    public static final String TIMESTAMP_FORMAT_PATTERN = "uuuu-MM-dd HH:mm:ss:SS";
    public static final String FILE_TIMESTAMP_FORMAT_PATTERN = "uuuu_MM_dd_HH_mm_ss_SS";

    private final DateTimeFormatter timestampFormatter;
    private final BufferedWriter bufferedWriter;

    public DebugLogger(String logDirectory) throws IOException {
        this.timestampFormatter = DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT_PATTERN);
        String fileName = logDirectory + "/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern(FILE_TIMESTAMP_FORMAT_PATTERN)) + ".txt";
        File file = new File(fileName);
        boolean createdLogFile = file.createNewFile();
        if (!createdLogFile) throw new IOException("Log file could not be created");
        FileWriter fileWriter = new FileWriter(file);
        this.bufferedWriter = new BufferedWriter(fileWriter);
    }

    public void print(String text) {
        String timestamp = LocalDateTime.now().format(timestampFormatter);
        try {
            bufferedWriter.write(timestamp + " - " + text);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
