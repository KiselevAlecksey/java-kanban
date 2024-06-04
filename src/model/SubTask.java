package model;

public class SubTask extends Task {
    private Integer epicId;

    public SubTask(String taskName, String description, Status status, Integer epicId) {
        super(taskName, description, status);
        this.epicId = epicId;
    }

    public SubTask(Integer taskId, String taskName, Status status, String description, Integer epicId) {
        super(taskId, taskName, status, description);
        this.epicId = epicId;
    }

    @Override
    public Integer getEpicId() {
        return epicId;
    }

    public void setEpic(Integer epic) {
        this.epicId = epic;
    }

    @Override
    public TypeTask getType() {
        return TypeTask.SUBTASK;
    }
}
