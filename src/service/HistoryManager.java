package service;

import model.Task;

import java.util.List;

public interface HistoryManager {

    Task add(Task task);

    int remove(int id);

    List<Task> getHistory();

}
