package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private final List<Task> history = new ArrayList<>(10);

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
        List<Task> list = history;
        return list;
    }

}
