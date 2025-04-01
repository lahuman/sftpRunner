package io.github.lahuman.manager;


import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class DownloadHistoryManager {
    private static final String DOWNLOADED_LOG_FILE = "downloaded.log";
    private final Set<String> downloadedFiles = new HashSet<>();

    public DownloadHistoryManager() {
        loadDownloadedFiles();
    }

    public boolean isDownloaded(String fileName) {
        return downloadedFiles.contains(fileName);
    }

    public void saveDownloadedFile(String fileName) {
        downloadedFiles.add(fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DOWNLOADED_LOG_FILE, true))) {
            writer.write(fileName);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error saving download log: " + e.getMessage());
        }
    }

    private void loadDownloadedFiles() {
        File logFile = new File(DOWNLOADED_LOG_FILE);
        if (logFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    downloadedFiles.add(line.trim());
                }
            } catch (IOException e) {
                System.err.println("Error loading download history: " + e.getMessage());
            }
        }
    }
}

