package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTasksId = new ArrayList<>();
    private Integer epicId;

    public Epic(String taskName, String description, Status status) {
        super(taskName, description, status);

    }

    public Epic(Integer taskId, String taskName, String description, Status status) {
        super(taskId, taskName, description, status);
    }

    public Epic(Integer taskId, TypeTask type, String taskName, Status status, String description, Integer epicId) {
        super(taskId, type, taskName, status, description, epicId);
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
    public void setType(TypeTask type) {
        this.type = TypeTask.EPIC;
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
