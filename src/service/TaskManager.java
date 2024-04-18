package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;

public interface TaskManager {
    int generateId();

    List<Task> getAllTasks();

    void removeTasks();

    Task getTaskById(int id);

    Task createTask(Task task);

    void updateTask(Task task);

    void removeByTaskId(int taskId);

    List<SubTask> getAllSubTasks();

    void removeSubTasks();

    SubTask getSubTaskById(int id);

    SubTask createSubTask(SubTask task);

    SubTask updateSubTask(SubTask task);

    void removeBySubTaskId(int subTaskId);

    List<Epic> getAllEpics();

    void removeEpics();

    Epic getEpicById(int id);

    Epic createEpic(Epic epic);

    void updateEpic(Epic epic);

    void removeByEpicId(int epicId);

    List<Integer> getSubTasksEpic(Epic epic);

    void updateStatusEpic(Epic epic);

    void printHistory();

}
