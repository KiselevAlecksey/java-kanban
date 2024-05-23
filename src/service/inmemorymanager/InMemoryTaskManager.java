package service.inmemorymanager;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.HistoryManager;
import service.TaskManager;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected int counter = 0;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();

    HistoryManager historyManager;
    private Path path;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public int generateId() {
        return ++counter;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public void removeTasks() {

        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
        }

        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        } else {
            throw new NotFoundException("Задача не найдена: " + id);
        }

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
        } else {
            throw new NotFoundException("Задача не найдена:" + key);
        }
    }

    @Override
    public void removeByTaskId(int taskId) {
        int id = getTaskById(taskId).getId();
        tasks.remove(id);

        historyManager.remove(id);
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void removeSubTasks() {

        for (Integer id : subTasks.keySet()) {
            historyManager.remove(id);
        }

        subTasks.clear();

        for (Epic value : epics.values()) {
            updateEpic(value);
        }

    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask task = subTasks.get(id);
        if (task != null) {
            historyManager.add(task);
        } else {
            throw new NotFoundException("Подзадача не найдена: " + id);
        }

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
            if (epic == null) {
                throw new NotFoundException("Эпик не найден:" + epicId);
            }
            updateEpic(epic);
        }
        return task;
    }

    @Override
    public void removeBySubTaskId(int subTaskId) {
        SubTask remoteSubtask = subTasks.remove(subTaskId);

        if (remoteSubtask == null) throw new NotFoundException("Подзадача не найдена: " + subTaskId);

        int epicId = remoteSubtask.getEpicId();
        Epic epic = getEpicById(epicId);
        epic.removeIdSubTask(subTaskId); // эта строчка
        updateEpic(epic);
        historyManager.remove(subTaskId);
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeEpics() {

        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }

        epics.clear();

        for (Integer id : subTasks.keySet()) {
            historyManager.remove(id);
        }

        subTasks.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        } else {
            throw new NotFoundException("Эпик не найден: " + id);
        }

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
    public Epic removeByEpicId(int epicId) {
        Epic epic = getEpicById(epicId);
        List<SubTask> subTasksList = new ArrayList<>(getSubTasksEpic(epic));

        for (SubTask subTask : subTasksList) {
            if (subTask != null) {
                Integer id = subTask.getId();
                removeBySubTaskId(id);
                subTasks.remove(id);

                historyManager.remove(id);
            } else {
                throw new NotFoundException("Подзадача не найдена у эпика: " + epicId);
            }
        }

        subTasksList.clear();
        epics.remove(epicId);

        historyManager.remove(epicId);

        return epic;
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
        return new ArrayList<>(historyManager.getHistory());
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
                } else {
                    throw new NotFoundException("Подзадача не найдена у эпика: " + epic.getId());
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

    @Override
    public void printHistory() {
        int i = 0;
        List<Task> history = new ArrayList<>(historyManager.getHistory());

        for (Task task : history) {
            System.out.println((i + 1) + ". - " + task);
            ++i;
        }
    }

    @Override
    public void printTasks() {
        for (Task task : tasks.values()) {
            System.out.println(task);
        }
    }

    @Override
    public void printSubTasks() {
        for (SubTask task : subTasks.values()) {
            System.out.println(task);
        }
    }

    @Override
    public void printEpics() {
        for (Epic task : epics.values()) {
            System.out.println(task);
        }
    }

}
