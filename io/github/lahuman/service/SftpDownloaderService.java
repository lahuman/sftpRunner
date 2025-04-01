package io.github.lahuman.service;

import com.jcraft.jsch.*;
import io.github.lahuman.config.SftpConfig;
import io.github.lahuman.manager.DownloadHistoryManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Vector;
import java.util.regex.Pattern;

public class SftpDownloaderService implements SftpService {
    private final SftpConfig config;
    private final DownloadHistoryManager historyManager;

    public SftpDownloaderService(SftpConfig config, DownloadHistoryManager historyManager) {
        this.config = config;
        this.historyManager = historyManager;
    }

    @Override
    public void downloadFiles() {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(config.getUsername(), config.getHost(), config.getPort());
            session.setPassword(config.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            channel.cd(config.getRemoteDir());

            Pattern pattern = Pattern.compile(config.getFilePattern());
            for (ChannelSftp.LsEntry entry : (Vector<ChannelSftp.LsEntry>) channel.ls(".")) {
                if (!entry.getAttrs().isDir() && pattern.matcher(entry.getFilename()).matches()) {
                    String remoteFile = entry.getFilename();
                    if (!historyManager.isDownloaded(remoteFile)) {
                        downloadFile(channel, remoteFile);
                    }
                }
            }

            channel.disconnect();
            session.disconnect();
        } catch (JSchException | SftpException e) {
            System.err.println("SFTP error: " + e.getMessage());
        }
    }

    private void downloadFile(ChannelSftp channel, String remoteFile) {
        Path localPath = Paths.get(config.getLocalDir(), remoteFile);
        try (InputStream inputStream = channel.get(remoteFile)) {
            Files.createDirectories(localPath.getParent());
            Files.copy(inputStream, localPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Downloaded: " + remoteFile);
            historyManager.saveDownloadedFile(remoteFile);
        } catch (IOException | SftpException e) {
            System.err.println("Error downloading " + remoteFile + ": " + e.getMessage());
        }
    }
}