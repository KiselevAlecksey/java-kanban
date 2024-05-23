package model;

public class Task {
    private String name;
    private Integer taskId;
    private String description;
    protected Status status;
    protected TypeTask type;
    private Integer epicId;

    public Task(String taskName, String description, Status status) {
        this.name = taskName;
        this.description = description;
        this.status = status;
    }

    public Task(Integer taskId, String taskName, String description, Status status) {
        this.taskId = taskId;
        this.name = taskName;
        this.description = description;
        this.status = status;
    }

    public Task(Integer taskId, TypeTask type, String taskName, Status status, String description, Integer epicId) {
        this.taskId = taskId;
        this.type = type;
        this.name = taskName;
        this.description = description;
        this.status = status;
        this.epicId = epicId;
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

    public void setType(TypeTask type) {
        this.type = TypeTask.TASK;
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
                ", type=" + type +
                ", epicId=" + epicId +
                '}';
    }
}
