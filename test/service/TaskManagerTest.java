package service;

import exception.IntersectTimeException;
import exception.NotFoundException;
import model.dto.Epic;
import model.dto.Subtask;
import model.dto.Task;
import model.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;
    HistoryManager historyManager;
    String name;
    Status status;
    LocalDateTime localDateTime;
    Duration duration;
    Task task;
    Epic epic;
    Subtask subtask;

    public abstract T createTaskManager();

    @BeforeEach
    public void init() {
        taskManager = createTaskManager();
        historyManager = Managers.getDefaultHistory();
        localDateTime = LocalDateTime.of(2024, 1, 14, 10, 0);
        duration = Duration.ofMinutes(15);
        task = taskManager.createTask(new Task("Новая задача", "Описание", Status.NEW));
        taskManager.getTaskById(task.getId()).setStartTime(localDateTime);
        taskManager.updateTask(task);
        epic = taskManager.createEpic(new Epic("Новый эпик", "Описание", Status.NEW));
        subtask = taskManager.createSubtask(new Subtask("Новая задача",
                "Описание", Status.NEW, epic.getId()));
        taskManager.getSubtaskById(subtask.getId()).setStartTime(localDateTime.plusMinutes(30));
        taskManager.updateSubtask(subtask);
        name = "новая задача обновление";
        status = Status.DONE;
    }


    @Test
    @DisplayName("должен вернуть исключение пересечение временных отрезков")
    void shouldReturnExceptionIntersectTimeSection() {
        assertThrows(IntersectTimeException.class, () -> {
            Task task1 = taskManager.createTask(new Task("Новая задача", "Описание", Status.NEW));
            taskManager.getTaskById(task1.getId()).setStartTime(localDateTime);
            taskManager.updateTask(task1);
        });
    }

    @Test
    @DisplayName("должен вернуть не пустой список задач")
    void shouldReturnNotNullList() {
        assertNotNull(taskManager.getAllTasks(), "Задача не найдена.");
        assertNotNull(taskManager.getAllSubtasks(), "Задача не найдена.");
        assertNotNull(taskManager.getAllEpics(), "Задача не найдена.");
    }

    @Test
    @DisplayName("должен вернуть пустой список задач")
    void shouldRemoveTasksLists() {
        List<Task> list = new ArrayList<>();

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            taskManager.removeSubtasks();
        });

        taskManager.removeTasks();
        taskManager.removeEpics();

        assertEquals(list, taskManager.getAllTasks(), "Список не пустой.");
        assertEquals("Подзадача не найдена у эпика: " + epic.getId(),
                thrown.getMessage(), "Список не пустой.");
        assertEquals(list, taskManager.getAllEpics(), "Список не пустой.");
    }

    @Test
    @DisplayName("должен вернуть пустую задачу")
    void shouldRemoveTasksById() {

        int id = task.getId();
        taskManager.removeByTaskId(task.getId());
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            taskManager.removeByTaskId(task.getId());
        });
        assertEquals("Задача не найдена: " + id, thrown.getMessage(), "Задача не удалена.");

        id = subtask.getId();
        taskManager.removeBySubtaskId(subtask.getId());
        thrown = assertThrows(NotFoundException.class, () -> {
            taskManager.removeBySubtaskId(subtask.getId());
        });
        assertEquals("Подзадача не найдена: " + id, thrown.getMessage(), "Задача не удалена.");

        id = epic.getId();
        taskManager.removeByEpicId(epic.getId());
        thrown = assertThrows(NotFoundException.class, () -> {
            taskManager.removeByEpicId(epic.getId());
        });
        assertEquals("Эпик не найден: " + id, thrown.getMessage(), "Задача не удалена.");

    }

    @Test
    @DisplayName("должен вернуть задачу")
    void shouldAddDifferentTasks() {
        assertNotNull(task.getId(), "Задача не найдена.");
        assertNotNull(epic.getId(), "Задача не найдена.");
        assertNotNull(subtask.getId(), "Задача не найдена.");
    }

    @Test
    @DisplayName("должен изменить статус эпика")
    void shouldChangeEpicStatus() {
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Новая задача2",
                "Описание2", Status.DONE, epic.getId()));
        subtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);

        assertEquals(status, epic.getStatus(), "Статус не совпадает.");
        subtask.setStatus(Status.NEW);
        subtask2.setStatus(Status.NEW);
        taskManager.updateSubtask(subtask);
        taskManager.updateSubtask(subtask2);
        status = Status.NEW;

        assertEquals(status, epic.getStatus(), "Статус не совпадает.");
        subtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);
        status = Status.IN_PROGRESS;

        assertEquals(status, epic.getStatus(), "Статус не совпадает.");

        subtask.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        taskManager.updateSubtask(subtask2);
        assertEquals(status, epic.getStatus(), "Статус не совпадает.");
    }

    @Test
    @DisplayName("должны совпадать поля задач после обновления")
    void shouldUpdateTasks() {
        Task taskUpdated = new Task(task.getId(), "новая задача обновление",
                Status.DONE, task.getDescription(), duration, localDateTime);
        taskManager.updateTask(taskUpdated);

        assertEquals(task.getId(), taskUpdated.getId(), "id не совпадает.");
        assertEquals(name, taskUpdated.getName(), "Имя не совпадает.");
        assertEquals(Status.DONE, taskUpdated.getStatus(), "Статус не совпадает.");
    }

    @Test
    @DisplayName("должны совпадать поля подзадач после обновления")
    void shouldUpdateSubTasks() {
        String name = "новая задача обновление";

        Subtask subtaskUpdated = new Subtask(subtask.getId(), name,
                Status.DONE, subtask.getDescription(), epic.getId(), duration, localDateTime.plusMinutes(60));
        taskManager.updateSubtask(subtaskUpdated);

        assertEquals(subtask.getId(), subtaskUpdated.getId(), "id не совпадает.");
        assertEquals(name, subtaskUpdated.getName(), "Имя не совпадает.");
        assertEquals(Status.DONE, subtaskUpdated.getStatus(), "Статус не совпадает.");
    }

    @Test
    @DisplayName("должны совпадать поля эпиков после обновления")
    void shouldUpdateEpics() {
        Epic epicUpdated = new Epic(epic.getId(), name, Status.DONE, epic.getDescription(), duration, localDateTime);
        taskManager.updateEpic(epicUpdated);

        assertEquals(epic.getId(), epicUpdated.getId(), "id не совпадает.");
        assertEquals(name, epicUpdated.getName(), "Имя не совпадает.");
        assertEquals(Status.DONE, epicUpdated.getStatus(), "Статус не совпадает.");
    }

    @Test
    @DisplayName("поле epic должно быть null")
    void shouldNotAddSubTaskInEpicField() {
        String name = "новая задача обновление";

        Subtask subtaskUpdated = new Subtask(subtask.getId(), name,
                Status.DONE, subtask.getDescription(), subtask.getId(), duration, localDateTime.plusMinutes(60));

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            taskManager.updateSubtask(subtaskUpdated);
        });

        assertEquals("Эпик не найден: " + subtask.getId(), thrown.getMessage(),
                "исключения не совпадают");
        assertEquals(name, subtaskUpdated.getName(), "Имя не совпадает.");
        assertEquals(Status.DONE, subtaskUpdated.getStatus(), "Статус не совпадает.");
    }

    @Test
    @DisplayName("должен вернуть список задач")
    void shouldGetListTasks() {
        final int idTask = task.getId();
        final Task taskSaved = taskManager.getTaskById(idTask);

        assertNotNull(taskSaved, "Задача не найдена.");
        assertEqualsTask(task, taskSaved, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEqualsTask(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    @DisplayName("должен вернуть пустую задачу из истории")
    void shouldDeleteIdSubTasksFromHistory() {
        Task taskNull = null;
        int id = subtask.getId();

        taskManager.removeBySubtaskId(id);
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            taskManager.removeBySubtaskId(id);
        });

        assertEquals("Подзадача не найдена: " + id, thrown.getMessage(), "Задача не удалена.");

        for (Task task : taskManager.getHistory()) {
            if (id == task.getId()) {
                taskNull = task;
            }
        }
        assertNull(taskNull, "Задача не удалена");
    }

    public void assertEqualsTask(Task expected, Task actual, String message) {
        assertEquals(expected.getId(), actual.getId(), message + ", id");
        assertEquals(expected.getType(), actual.getType(), message + ", type");
        assertEquals(expected.getName(), actual.getName(), message + ", name");
        assertEquals(expected.getDescription(), actual.getDescription(), message + ", description");
        assertEquals(expected.getStatus(), actual.getStatus(), message + ", status");
        assertEquals(expected.getStartTime(), actual.getStartTime(), message + ", startTime");
    }
}
