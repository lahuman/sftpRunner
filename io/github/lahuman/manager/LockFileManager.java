package io.github.lahuman.manager;


import java.io.File;
import java.io.IOException;

public class LockFileManager {
    private static final String LOCK_FILE = "run.lock";

    public boolean createLock() {
        File lockFile = new File(LOCK_FILE);
        try {
            return lockFile.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }

    public boolean releaseLock() {
        return new File(LOCK_FILE).delete();
    }
}
