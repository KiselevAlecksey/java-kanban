package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTasksId = new ArrayList<>();

    public Epic(String taskName, String description, Status status) {
        super(taskName, description, status);

    }

    public Epic(Integer taskId, String taskName, String description, Status status) {
        super(taskId, taskName, description, status);
    }

    public List<Integer> getSubTasksId() {
        List<Integer> list = subTasksId;
        return list;
    }

    public void addSubTaskId(Integer id) {
        subTasksId.add(id);
    }

    public void removeSubTaskById(Integer id) {
        subTasksId.remove(id);
    }

}
