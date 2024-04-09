package model;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private final ArrayList<Integer> subTasksId = new ArrayList<>();

    public Epic(String taskName, String description, Status status) {
        super(taskName, description, status);

    }

    public ArrayList<Integer> getSubTasksId() {
        ArrayList<Integer> list = new ArrayList<>(subTasksId);
        return list;
    }

    public void addSubTaskId(SubTask subTask) {
        subTasksId.add(subTask.getId());
    }

    public void removeSubTaskById(Integer id) {
        subTasksId.remove(id);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        Epic epic = (Epic) object;
        return Objects.equals(subTasksId, epic.subTasksId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTasksId);
    }
}
