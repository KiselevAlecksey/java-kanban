package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String taskName, String description, Status status, Integer epicId) {
        super(taskName, description, status);
        this.epicId = epicId;
    }

    public Subtask(Integer taskId, String taskName, Status status, String description, Integer epicId,
                   Duration duration, LocalDateTime startTime) {
        super(taskId, taskName, status, description, duration, startTime);
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
