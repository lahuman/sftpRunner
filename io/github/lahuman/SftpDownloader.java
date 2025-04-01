package io.github.lahuman;

import io.github.lahuman.config.SftpConfig;
import io.github.lahuman.manager.DownloadHistoryManager;
import io.github.lahuman.manager.LockFileManager;
import io.github.lahuman.service.SftpDownloaderService;
import io.github.lahuman.service.SftpService;

public class SftpDownloader {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java SftpDownloader <configFilePath>");
            return;
        }

        LockFileManager lockManager = new LockFileManager();
        if (!lockManager.createLock()) {
            System.out.println("Another instance is running.");
            return;
        }

        try {
            SftpConfig config = new SftpConfig(args[0]);
            DownloadHistoryManager historyManager = new DownloadHistoryManager();
            SftpService downloader = new SftpDownloaderService(config, historyManager);
            downloader.downloadFiles();
        } finally {
            if(!lockManager.releaseLock()) {
                System.err.println("Failed to release lock.");
            }
        }
    }
}
