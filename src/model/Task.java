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

    @Override
    public String toString() {
        return "Task{" +
                "taskName='" + name + '\'' +
                ", taskId=" + taskId +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
