package service;

import model.*;
import model.SubTask;

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
        int key = task.getId();
        if (getTaskById(key) != null) {
            tasks.put(task.getId(), task);
        }
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
        int epicId = task.getEpicId();
        Epic epic = getEpicById(epicId);
        epic.addSubTaskId(task);
        epics.put(epic.getId(), epic);
        updateEpic(epic);
        return task;
    }

    public void updateSubTask(SubTask task) {
        int key = task.getId();
        
        if (getSubTaskById(key) != null) {
            subTasks.put(task.getId(), task);
            int epicId = task.getEpicId();
            Epic epic = getEpicById(epicId);
            updateEpic(epic);
        }
    }

    public void removeBySubTaskId(int subTaskId) {
        int id = getSubTaskById(subTaskId).getId();
        SubTask remove = getSubTaskById(subTaskId);
        int epicId = remove.getEpicId();
        Epic epic = getEpicById(epicId);
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
        ArrayList<Integer> subTaskList = getSubTasksEpic(epic);

        for (int subTaskId : subTaskList) {
            SubTask subTaskNotEmpty = subTasks.get(subTaskId);
            if (subTaskNotEmpty == null) {
                Epic epicNew = epics.get(epic.getId());
                epicNew.removeSubTaskById(subTaskId);
                subTasks.remove(subTaskId);
                epics.put(epicNew.getId(), epicNew);
            }
        }

        Epic saved = epics.get(epic.getId());
        saved.setName(epic.getName());
        saved.setDescription(epic.getDescription());
        updateStatusEpic(saved);
        epics.put(saved.getId(), saved);
    }

    public void removeByEpicId(int epicId) {
        Epic epic = getEpicById(epicId);
        ArrayList<Integer> subTasksList = getSubTasksEpic(epic);
        for (int subTaskId : subTasksList) {
            removeBySubTaskId(subTaskId);
            subTasks.remove(subTaskId);
        }
        subTasksList.clear();
        epics.remove(epicId);
    }

    public ArrayList<Integer> getSubTasksEpic(Epic epic) {
        ArrayList<Integer> list = epic.getSubTasksId();
        return list;
    }

    private void updateStatusEpic(Epic epic) {
        ArrayList<Integer> list = getSubTasksEpic(epic);

        if (list.isEmpty()){
            epic.setStatus(Status.NEW);
        } else {
            calculateStatusEpic(list, epic);
        }
    }

    private void calculateStatusEpic(ArrayList<Integer> list, Epic epic) {
        boolean allDONE = true;
        boolean allNEW = true;

        for (int id : list) {
            SubTask subTask = getSubTaskById(id);
            if (subTask != null) {
                Status status = subTask.getStatus();

                if (status != Status.NEW) allNEW = false;
                if (status != Status.DONE) allDONE = false;
            }
        }
        setStatusEpic(epic, allNEW, allDONE);
    }

    private void setStatusEpic(Epic epic, boolean allNEW, boolean allDONE) {
        if (allNEW) {
            epic.setStatus(Status.NEW);
        } else if (allDONE) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
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
