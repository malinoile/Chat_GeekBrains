package classes;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class HistoryManager implements AutoCloseable{

    private static final int COUNT_WRITABLE_LINES = 100;

    private Path path;

    HistoryManager(String username) {
        path = initializePath(username);
        if (Files.notExists(path)) {
            createFileAndDirectory();
        }
    }

    public void updateFilename(String newUsername) {
        Path tempPath = initializePath(newUsername);
        try {
            Files.move(path, tempPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        path = tempPath;
    }

    public void writeToFile(String message) {
        try {
            Files.write(path, (message + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> readFromFile() {
        List<String> lastLines = new ArrayList<>();
        try {
            List<String> stringsFromFile = Files.readAllLines(path);
            if(stringsFromFile.size() <= COUNT_WRITABLE_LINES) {
                lastLines = stringsFromFile;
            } else {
                for (int i = stringsFromFile.size() - COUNT_WRITABLE_LINES; i < stringsFromFile.size(); i++) {
                    lastLines.add(stringsFromFile.get(i));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lastLines;
    }

    private void createFileAndDirectory() {
        try {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path initializePath(String username) {
        return Paths.get("histories/history_" + username + ".txt");
    }

    @Override
    public void close() throws Exception {

    }
}
