package service;

import model.*;
import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private int counter = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    public int generateId() {
        return ++counter;
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> list = new ArrayList<>(tasks.values());

        return list;
    }

    public void removeTasks() {
        tasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void removeByTaskId(int taskId) {
        int id = getTaskById(taskId).getId();
        tasks.remove(id);
    }

    public ArrayList<SubTask> getAllSubTasks() {
        ArrayList<SubTask> list = new ArrayList<>(subTasks.values());

        return list;
    }

    public void removeSubTasks() {
        subTasks.clear();

        for (Epic value : epics.values()) {
            updateEpic(value);
        }
    }

    public SubTask getSubTaskById(int id) {
        SubTask task = subTasks.get(id);
        return task;
    }

    public SubTask createSubTask(SubTask task) {
        task.setId(generateId());
        subTasks.put(task.getId(), task);
        Epic epic = task.getEpic();
        epic.addSubTask(task);
        epics.put(epic.getId(), epic);
        updateEpic(epic);
        return task;
    }

    public void updateSubTask(SubTask task) {
        subTasks.put(task.getId(), task);
        updateEpic(task.getEpic());
    }

    public void removeBySubTaskId(int subTaskId) {
        int id = getSubTaskById(subTaskId).getId();
        SubTask remove = getSubTaskById(subTaskId);
        Epic epic = remove.getEpic();
        subTasks.remove(id);
        updateEpic(epic);
    }

    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> list = new ArrayList<>(epics.values());

        return list;
    }

    public void removeEpics() {
        epics.clear();
        subTasks.clear();
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    public void updateEpic(Epic epic) {
        ArrayList<SubTask> subTaskList = getSubTasksEpic(epic);

        for (SubTask subTask : subTaskList) {
            SubTask subTaskNotEmpty = subTasks.get(subTask.getId());
            if (subTaskNotEmpty == null) {
                Epic epicNew = epics.get(epic.getId());
                epicNew.removeSubTask(subTask);
                epics.put(epicNew.getId(), epicNew);
            }
        }

        Epic saved = epics.get(epic.getId());
        saved.setName(epic.getName());
        saved.setDescription(epic.getDescription());
        calculateStatus(saved);
        epics.put(saved.getId(), saved);
    }

    public void removeByEpicId(int epicId) {
        Epic epic = getEpicById(epicId);
        ArrayList<SubTask> subTasksList = getSubTasksEpic(epic);
        for (SubTask subTask : subTasksList) {
            removeBySubTaskId(subTask.getId());
        }
        subTasksList.clear();
        epics.remove(epicId);
    }

    public ArrayList<SubTask> getSubTasksEpic(Epic epic) {
        ArrayList<SubTask> list = epic.getSubTasks();
        return list;
    }

    private void calculateStatus(Epic epic) {
        ArrayList<SubTask> list = getSubTasksEpic(epic);

        if (list.isEmpty()){
            epic.setStatus(Status.NEW);
        } else {
            getNewStatusEpic(list, epic);
        }
    }

    private void getNewStatusEpic(ArrayList<SubTask> list, Epic epic) {
        Status statusNew = Status.NEW;
        Status statusInProgress = Status.IN_PROGRESS;
        Status statusDone = Status.DONE;
        boolean allDONE = true;
        boolean allNEW = true;

        for (SubTask task : list) {
            Status status = task.getStatus();

            if (status != statusNew) allNEW = false;
            if (status != statusDone) allDONE = false;
        }
        if (allNEW) {
            epic.setStatus(statusNew);
        } else if (allDONE) {
            epic.setStatus(statusDone);
        } else {
            epic.setStatus(statusInProgress);
        }
    }

    public void printTasks() {
        for (Task task : tasks.values()) {
            System.out.println(task);
        }
    }

    public void printSubTasks() {
        for (SubTask task : subTasks.values()) {
            System.out.println(task);
        }
    }

    public void printEpics() {
        for (Epic task : epics.values()) {
            System.out.println(task);
        }
    }
}
