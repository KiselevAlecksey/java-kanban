package model;

public class Task {
    private String name;
    private Integer taskId;
    private String description;
    protected Status status;

    public Task(String taskName, String description, Status status) {
        this.name = taskName;
        this.description = description;
        this.status = status;
    }

    public Task(Integer taskId, String taskName, Status status, String description) {
        this.taskId = taskId;
        this.name = taskName;
        this.status = status;
        this.description = description;
    }

    public Task(Task task) {
        this.taskId = task.taskId;
        this.name = task.name;
        this.description = task.description;
        this.status = task.status;
    }

    public void setId(Integer id) {
        taskId = id;
    }

    public Integer getId() {
        return taskId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TypeTask getType() {
        return TypeTask.TASK;
    }

    public Integer getEpicId() {
        return null;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Task task)) return false;

        return taskId == task.taskId;
    }

    @Override
    public int hashCode() {
        return taskId;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", taskId=" + taskId +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", type=" + getType() +
                ", epicId=" + getEpicId() +
                '}';
    }
}
