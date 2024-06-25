package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTasksId = new ArrayList<>();

    public Epic(String taskName, String description, Status status) {
        super(taskName, description, status);

    }

    public Epic(Integer taskId, String taskName, Status status,
                String description, Duration duration, LocalDateTime startTime) {
        super(taskId, taskName, status, description, duration, startTime);
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
