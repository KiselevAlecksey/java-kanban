package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private static final int HISTORY_SIZE = 10;
    private final List<Task> history = new ArrayList<>(HISTORY_SIZE);

    @Override
    public Task add(Task task) {
        int sizeHistory = history.size();

        if (history.contains(task)) {
            history.remove(task);
        }

        if (sizeHistory > 9) {
            history.removeFirst();
        }

        history.add(task);
        return task;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> list = new ArrayList<>(history);
        return list;
    }

}
