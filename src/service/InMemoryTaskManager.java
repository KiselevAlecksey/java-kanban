package service;

import model.*;
import model.SubTask;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int counter = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
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
        List<Integer> subTaskList = getSubTasksEpic(epic);
        List<Integer> list = new ArrayList<>();
        boolean removeTrue = false;

        for (Integer subTaskId : subTaskList) {
            SubTask subTaskNotEmpty = subTasks.get(subTaskId);
            if (subTaskNotEmpty == null) {
                list.add(subTaskId);
                removeTrue = true;
            }
        }

         if(removeTrue) {
             int size = list.size();
             for (int i = 0; i < size; i++) {
                 int id = list.get(i);
                 Epic epicNew = epics.get(epic.getId());
                 epicNew.removeSubTaskById(id);
                 subTasks.remove(id);
                 epics.put(epicNew.getId(), epicNew);
             }

         }

        Epic saved = epics.get(epic.getId());
        saved.setName(epic.getName());
        saved.setDescription(epic.getDescription());
        updateStatusEpic(saved);
        epics.put(saved.getId(), saved);
    }

    @Override
    public void removeByEpicId(int epicId) {
        Epic epic = getEpicById(epicId);
        List<Integer> subTasksList = getSubTasksEpic(epic);
        List<Integer> list = new ArrayList<>();
        for (int subTaskId : subTasksList) {
            list.add(subTaskId);

        }
        for (int i = 0; i < list.size(); i++) {
            int id = list.get(i);
            removeBySubTaskId(id);
            subTasks.remove(id);
        }

        subTasksList.clear();
        epics.remove(epicId);
    }

    @Override
    public ArrayList<Integer> getSubTasksEpic(Epic epic) {
        ArrayList<Integer> list = epic.getSubTasksId();

        return list;
    }

    @Override
    public void updateStatusEpic(Epic epic) {
        ArrayList<Integer> list = getSubTasksEpic(epic);

        if (list.isEmpty()){
            epic.setStatus(Status.NEW);
        } else {
            calculateStatusEpic(list, epic);
        }
    }

    private void calculateStatusEpic(List<Integer> list, Epic epic) {
        boolean isDifference = false;
            boolean hasDifference = false;
            Status status = null;

            for (int id : list) {
                SubTask subTask = getSubTaskById(id);
                if ((subTask != null) && (status == null)) {
                    status = subTask.getStatus();
                }
                isDifference = subTask.getStatus().equals(status);
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
        List<Task> history = historyManager.getHistory();

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
