package service;

import model.Task;

import java.util.List;

public interface HistoryManager {

    Task add(Task task);

    List<Task> getHistory();

}
