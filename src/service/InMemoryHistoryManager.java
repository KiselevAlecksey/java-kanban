package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private final List<Task> history = new ArrayList<>(10);

    @Override
    public Task add(Task task) {
        int sizeHistory = history.size();

                if (sizeHistory < 10) {
                    history.add(task);
                } else {
                    removeTaskFromHistory(sizeHistory);
                    history.add(task);
                }
        return task;
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

    private void removeTaskFromHistory(int sizeHistory) {
        if (sizeHistory > 0) {
            history.removeFirst();
        }
    }

}
