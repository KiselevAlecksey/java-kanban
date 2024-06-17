package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.nio.file.Path;
import java.util.List;

public interface TaskManager {
    int generateId();

    List<Task> getAllTasks();

    void removeTasks();

    Task getTaskById(int id);

    Task createTask(Task task);

    void updateTask(Task task);

    void removeByTaskId(int taskId);

    List<Subtask> getAllSubtasks();

    void removeSubtasks();

    Subtask getSubtaskById(int id);

    Subtask createSubtask(Subtask task);

    Subtask updateSubtask(Subtask task);

    void removeBySubTaskId(int subtaskId);

    List<Epic> getAllEpics();

    void removeEpics();

    Epic getEpicById(int id);

    Epic createEpic(Epic epic);

    void updateEpic(Epic epic);

    Epic removeByEpicId(int epicId);

    List<Subtask> getEpicSubtasks(Epic epic);

    void updateStatusEpic(Epic epic);

    List<Task> getPrioritizedTasks();

    void printHistory();

    List<Task> getHistory();

    Path getPath();

    void printTasks();

    void printEpics();

    void printSubtasks();
}
