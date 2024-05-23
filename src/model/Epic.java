package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTasksId = new ArrayList<>();

    public Epic(String taskName, String description, Status status) {
        super(taskName, description, status);

    }

    public Epic(Integer taskId, String taskName, Status status, String description) {
        super(taskId, taskName, status, description);
    }

    public List<Integer> getSubTasksId() {
        List<Integer> list = new ArrayList<>(subTasksId);
        return list;
    }

    @Override
    public Integer getEpicId() {
        return null;
    }

    @Override
    public TypeTask getType() {
        return TypeTask.EPIC;
    }

    public void addSubTaskId(Integer id) {
        subTasksId.add(id);
    }

    public void removeIdSubTask(Integer id) {
        subTasksId.remove(id);
    }

}
