package service;

import service.infilemanager.FileBackedTaskManager;
import service.inmemorymanager.InMemoryHistoryManager;

import java.nio.file.Paths;

public class Managers {

    public static TaskManager getDefault() {
        return new FileBackedTaskManager(getDefaultHistory(), Paths.get("src\\resources\\tasks.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
