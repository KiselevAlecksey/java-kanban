package service;

import model.*;
import model.SubTask;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int counter = 0;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public int generateId() {
        return ++counter;
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> list = new ArrayList<>(tasks.values());

        return list;
    }

    @Override
    public void removeTasks() {
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);

        return task;
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);

        return task;
    }

    @Override
    public void updateTask(Task task) {
        int key = task.getId();

        if (getTaskById(key) != null) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void removeByTaskId(int taskId) {
        int id = getTaskById(taskId).getId();
        tasks.remove(id);
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        List<SubTask> list = new ArrayList<>(subTasks.values());

        return list;
    }

    @Override
    public void removeSubTasks() {
        subTasks.clear();

        for (Epic value : epics.values()) {
            updateEpic(value);
        }
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask task = subTasks.get(id);
        historyManager.add(task);

        return task;
    }

    @Override
    public SubTask createSubTask(SubTask task) {
        task.setId(generateId());
        subTasks.put(task.getId(), task);
        int epicId = task.getEpicId();
        Epic epic = getEpicById(epicId);
        epic.addSubTaskId(task.getId());
        epics.put(epic.getId(), epic);
        updateEpic(epic);

        return task;
    }

    @Override
    public SubTask updateSubTask(SubTask task) {
        int key = task.getId();
        
        if (getSubTaskById(key) != null) {
            subTasks.put(task.getId(), task);
            int epicId = task.getEpicId();
            Epic epic = getEpicById(epicId);
            if (epic == null) return null;
            updateEpic(epic);
        }
        return task;
    }

    @Override
    public void removeBySubTaskId(int subTaskId) {
        int id = getSubTaskById(subTaskId).getId();
        SubTask remove = getSubTaskById(subTaskId);
        int epicId = remove.getEpicId();
        Epic epic = getEpicById(epicId);
        subTasks.remove(id);
        updateEpic(epic);
    }

    @Override
    public List<Epic> getAllEpics() {
        List<Epic> list = new ArrayList<>(epics.values());

        return list;
    }

    @Override
    public void removeEpics() {
        epics.clear();
        subTasks.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);

        return epic;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);

        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic saved = epics.get(epic.getId());
        saved.setName(epic.getName());
        saved.setDescription(epic.getDescription());
        updateStatusEpic(saved);
        epics.put(saved.getId(), saved);
    }

    @Override
    public void removeByEpicId(int epicId) {
        Epic epic = getEpicById(epicId);
        List<SubTask> subTasksList = new ArrayList<>(getSubTasksEpic(epic));

        for (SubTask subTask : subTasksList) {
            if (subTask != null) {
                Integer id = subTask.getId();
                removeBySubTaskId(id);
                subTasks.remove(id);
            }
        }

        subTasksList.clear();
        epics.remove(epicId);
    }

    @Override
    public List<SubTask> getSubTasksEpic(Epic epic) {
        List<Integer> subTasksIds = new ArrayList<>(epic.getSubTasksId());
        List<SubTask> list = new ArrayList<>();

        for (Integer subTaskId : subTasksIds) {
            list.add(subTasks.get(subTaskId));
        }

        return list;
    }

    @Override
    public void updateStatusEpic(Epic epic) {
        List<SubTask> list = new ArrayList<>(getSubTasksEpic(epic));

        if (list.isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            calculateStatusEpic(list, epic);
        }
    }

    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>(historyManager.getHistory());
        return history;
    }

    private void calculateStatusEpic(List<SubTask> list, Epic epic) {
        boolean isDifference = false;
            boolean hasDifference = false;
            Status status = null;

            for (SubTask subTask : list) {
                if ((subTask != null)) {
                    if (status == null) {
                        status = subTask.getStatus();
                    }
                    isDifference = subTask.getStatus().equals(status);
                }

                if (!isDifference) {
                    hasDifference = true;
                    break;
                }
            }
        setStatusEpic(epic, hasDifference, status);
    }

    private void setStatusEpic(Epic epic, boolean hasDifference, Status status) {
        if (hasDifference) {
            epic.setStatus(Status.IN_PROGRESS);
        } else {
            epic.setStatus(status);
        }
    }

    public void printHistory() {
        int i = 0;
        List<Task> history = new ArrayList<>(historyManager.getHistory());

        for (Task task : history) {
            System.out.println((i + 1) + ". - " + task);
            ++i;
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
