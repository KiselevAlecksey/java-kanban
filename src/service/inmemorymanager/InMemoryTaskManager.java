package service.inmemorymanager;

import exception.IntersectTimeException;
import exception.NotFoundException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.HistoryManager;
import service.TaskManager;

import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int counter = 0;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected Set<Task> prioritizedTaskList = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    protected HistoryManager historyManager;
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

        tasks.keySet().forEach(id -> historyManager.remove(id));

        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("Задача не найдена: " + id);
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        checkTaskTimeIntersection(task);
        tasks.put(task.getId(), task);
        prioritizedTaskList.add(task);

        return task;
    }

    @Override
    public void updateTask(Task task) {
        task.setEndTime();
        checkTaskTimeIntersection(task);
        int key = task.getId();

        if (getTaskById(key) == null) {
            throw new NotFoundException("Задача не найдена:" + key);
        }

        tasks.put(task.getId(), task);
        prioritizedTaskList.add(task);
    }

    @Override
    public void removeByTaskId(int taskId) {
        int id = getTaskById(taskId).getId();
        tasks.remove(id);

        historyManager.remove(id);
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeSubtasks() {

        subtasks.keySet().forEach(id -> historyManager.remove(id));

        subtasks.clear();

        epics.values().forEach(this::updateEpic);

    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask task = subtasks.get(id);
        if (task == null) {
            throw new NotFoundException("Подзадача не найдена: " + id);
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask createSubtask(Subtask task) {
        task.setId(generateId());
        checkTaskTimeIntersection(task);
        subtasks.put(task.getId(), task);
        int epicId = task.getEpicId();
        Epic epic = getEpicById(epicId);
        epic.addSubTaskId(task.getId());
        epics.put(epic.getId(), epic);
        updateEpic(epic);
        prioritizedTaskList.add(task);

        return task;
    }

    @Override
    public Subtask updateSubtask(Subtask task) {
        checkTaskTimeIntersection(task);
        task.setEndTime();
        int key = task.getId();
        int epicId = task.getEpicId();
        Epic epic = getEpicById(epicId);

        if (getSubtaskById(key) != null) {
            subtasks.put(task.getId(), task);

            if (epic == null) {
                throw new NotFoundException("Эпик не найден:" + epicId);
            }
            updateEpic(epic);
        }
        prioritizedTaskList.add(task);

        return task;
    }

    @Override
    public void removeBySubTaskId(int subtaskId) {
        Subtask remoteSubtask = subtasks.remove(subtaskId);

        if (remoteSubtask == null) {
            throw new NotFoundException("Подзадача не найдена: " + subtaskId);
        }

        int epicId = remoteSubtask.getEpicId();
        Epic epic = getEpicById(epicId);
        epic.removeIdSubTask(subtaskId); // эта строчка
        updateEpic(epic);

        historyManager.remove(subtaskId);
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeEpics() {

        epics.keySet().forEach(id -> historyManager.remove(id));

        epics.clear();

        subtasks.keySet().forEach(id -> historyManager.remove(id));

        subtasks.clear();
    }

    @Override
    public Epic getEpicById(int id) {

        if (epics.get(id) == null) {
            throw new NotFoundException("Эпик не найден: " + id);
        }
        historyManager.add(epics.get(id));

        return epics.get(id);
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
        List<Subtask> subTasksList = new ArrayList<>(getEpicSubtasks(epic));

        subTasksList.forEach(subTask -> {
            if (subTask == null) {
                throw new NotFoundException("Подзадача не найдена у эпика: " + epicId);
            }
            Integer id = subTask.getId();
            removeBySubTaskId(id);
            subtasks.remove(id);
            historyManager.remove(id);
        });

        subTasksList.clear();
        epics.remove(epicId);
        historyManager.remove(epicId);

        return epic;
    }

    @Override
    public List<Subtask> getEpicSubtasks(Epic epic) {
        List<Integer> subTasksIds = new ArrayList<>(epic.getSubTasksId());

        return subTasksIds.stream().map(subtasks::get).collect(Collectors.toList());
    }

    @Override
    public void updateStatusEpic(Epic epic) {
        List<Subtask> list = new ArrayList<>(getEpicSubtasks(epic));

        if (list.isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            calculateStatusEpic(list, epic);
        }
    }

    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    private void calculateStatusEpic(List<Subtask> list, Epic epic) {
        if (list.getFirst() == null) {
            throw new NotFoundException("Подзадача не найдена у эпика: " + epic.getId());
        }
        boolean isDifference;
        boolean hasDifference = false;
        Status status = null;
        LocalDateTime start = list.getFirst().getStartTime();
        LocalDateTime end = list.getLast().getEndTime();
        Duration duration = Duration.ZERO;

        for (Subtask subtask : list) {
            if ((subtask == null)) throw new NotFoundException("Подзадача не найдена у эпика: " + epic.getId());
            if (status == null) status = subtask.getStatus();

            isDifference = subtask.getStatus().equals(status);

                if (!isDifference) {
                    hasDifference = true;
                    break;
                }

            if (start.isAfter(subtask.getStartTime())) start = subtask.getStartTime();
            if (end.isBefore(subtask.getEndTime())) end = subtask.getEndTime();
            duration = duration.plus(subtask.getDuration());
            }
        epic.setStartTime(start);
        epic.setEndTime(end);
        epic.setDuration(duration);
        setStatusEpic(epic, hasDifference, status);
    }

    private void setStatusEpic(Epic epic, boolean hasDifference, Status status) {
        if (hasDifference) {
            epic.setStatus(Status.IN_PROGRESS);
        } else {
            epic.setStatus(status);
        }
    }

    private boolean checkTaskTimeIntersection(Task task) {

        getPrioritizedTasks().stream()
                .filter(t -> !Objects.equals(t.getId(), task.getId()))
                .filter(t -> (t.getStartTime().equals(task.getStartTime()))
                        || (t.getStartTime().isBefore(task.getEndTime())) & (t.getEndTime().isAfter(task.getStartTime())))
                .forEach(t -> {
                    throw new IntersectTimeException("Пересечение \n"
                            + task + "\n" + task.getStartTime() + "\n с задачей: " + t + "\n" + t.getStartTime());
                });
        return false;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTaskList);
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
        tasks.values().forEach(System.out::println);
    }

    @Override
    public void printSubtasks() {
        subtasks.values().forEach(System.out::println);
    }

    @Override
    public void printEpics() {
        epics.values().forEach(System.out::println);
    }

}
