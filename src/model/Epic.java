package model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<SubTask> subTasks = new ArrayList<>();

    public Epic(String taskName, String description, Status status) {
        super(taskName, description, status);

    }

    public ArrayList<SubTask> getSubTasks() {
        ArrayList<SubTask> list = new ArrayList<>(subTasks);
        return list;
    }

    public void addSubTask(SubTask subTask) {
        subTasks.add(subTask);
    }

    public void removeSubTask(SubTask subTask) {
        subTasks.remove(subTask);
    }
}
