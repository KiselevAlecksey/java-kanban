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

    public int getEpicId() {
            return epicId;
    }

    public void setEpic(Integer epic) {
        this.epicId = epic;
    }

}
