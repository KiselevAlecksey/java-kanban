package model;

public class SubTask extends Task {
    private Integer epicId;

    public SubTask(String taskName, String description, Status status, Integer epicId) {
        super(taskName, description, status);
        this.epicId = epicId;
    }

    public SubTask(Integer taskId, String taskName, String description, Status status, Integer epicId) {
        super(taskId, taskName, description, status);
        this.epicId = epicId;
    }

    public SubTask(Integer taskId, TypeTask type, String taskName, Status status, String description, Integer epicId) {
        super(taskId, type, taskName, status, description, epicId);
    }

    @Override
    public Integer getEpicId() {
        return epicId;
    }

    public void setEpic(Integer epic) {
        this.epicId = epic;
    }

    @Override
    public void setType(TypeTask type) {
        this.type = TypeTask.SUBTASK;
    }

    @Override
    public TypeTask getType() {
        return TypeTask.SUBTASK;
    }
}
