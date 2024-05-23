package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("менеджер задач")
class InMemoryTaskManagerTest {

    TaskManager taskManager;
    HistoryManager historyManager;
    Task task;
    Epic epic;
    SubTask subTask;
    String name;
    Status status;

    @BeforeEach
    void init() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
        task = taskManager.createTask(new Task("Новая задача", "Описание", Status.NEW));
        epic = taskManager.createEpic(new Epic("Новый эпик", "Описание", Status.NEW));
        subTask = taskManager.createSubTask(new SubTask("Новая задача",
                "Описание", Status.NEW, epic.getId()));
        name = "новая задача обновление";
        status = Status.DONE;
    }

    @Test
    @DisplayName("должен вернуть не пустой список задач")
    void shouldReturnNotNullList() {
        assertNotNull(taskManager.getAllTasks(), "Задача не найдена.");
        assertNotNull(taskManager.getAllSubTasks(), "Задача не найдена.");
        assertNotNull(taskManager.getAllEpics(), "Задача не найдена.");
    }

    @Test
    @DisplayName("должен вернуть пустой список задач")
    void shouldRemoveTasksLists() {
        List<Task> list = new ArrayList<>();

        taskManager.removeTasks();
        taskManager.removeSubTasks();
        taskManager.removeEpics();

        assertEquals(list, taskManager.getAllTasks(), "Список не пустой.");
        assertEquals(list, taskManager.getAllSubTasks(), "Список не пустой.");
        assertEquals(list, taskManager.getAllEpics(), "Список не пустой.");
    }

    @Test
    @DisplayName("должен вернуть пустую задачу")
    void shouldRemoveTasksById() {
        taskManager.removeByTaskId(task.getId());
        taskManager.removeBySubTaskId(subTask.getId());
        taskManager.removeByEpicId(epic.getId());

        assertNull(taskManager.getTaskById(task.getId()), "Задача не удалена.");
        assertNull(taskManager.getSubTaskById(subTask.getId()), "Задача не удалена.");
        assertNull(taskManager.getEpicById(epic.getId()), "Задача не удалена.");
    }


    @Test
    @DisplayName("должен вернуть задачу")
    void shouldAddDifferentTasks() {
        assertNotNull(task.getId(), "Задача не найдена.");
        assertNotNull(epic.getId(), "Задача не найдена.");
        assertNotNull(subTask.getId(), "Задача не найдена.");
    }

    @Test
    @DisplayName("должен изменить статус эпика")
    void shouldChangeEpicStatus() {
        SubTask subTask2 = taskManager.createSubTask(new SubTask("Новая задача2",
                "Описание2", Status.DONE, epic.getId()));
        subTask.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask);

        assertEquals(status, epic.getStatus(), "Статус не совпадает.");
        subTask.setStatus(Status.NEW);
        subTask2.setStatus(Status.NEW);
        taskManager.updateSubTask(subTask);
        taskManager.updateSubTask(subTask2);
        status = Status.NEW;

        assertEquals(status, epic.getStatus(), "Статус не совпадает.");
        subTask.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask);
        status = Status.IN_PROGRESS;

        assertEquals(status, epic.getStatus(), "Статус не совпадает.");
    }

    @Test
    @DisplayName("должны совпадать поля задач после обновления")
    void shouldUpdateTasks() {
        Task taskUpdated = new Task(task.getId(), "новая задача обновление",
                task.getDescription(), Status.DONE);
        taskManager.updateTask(taskUpdated);

        assertEquals(task.getId(), taskUpdated.getId(), "id не совпадает.");
        assertEquals(name, taskUpdated.getName(), "Имя не совпадает.");
        assertEquals(Status.DONE, taskUpdated.getStatus(), "Статус не совпадает.");
    }

    @Test
    @DisplayName("должны совпадать поля подзадач после обновления")
    void shouldUpdateSubTasks() {
        String name = "новая задача обновление";

        SubTask subTaskUpdated = new SubTask(subTask.getId(), name,
                subTask.getDescription(), Status.DONE, epic.getId());
        taskManager.updateSubTask(subTaskUpdated);

        assertEquals(subTask.getId(), subTaskUpdated.getId(), "id не совпадает.");
        assertEquals(name, subTaskUpdated.getName(), "Имя не совпадает.");
        assertEquals(Status.DONE, subTaskUpdated.getStatus(), "Статус не совпадает.");
    }

    @Test
    @DisplayName("должны совпадать поля эпиков после обновления")
    void shouldUpdateEpics() {
        Epic epicUpdated = new Epic(epic.getId(), name, epic.getDescription(), Status.DONE);
        taskManager.updateEpic(epicUpdated);

        assertEquals(epic.getId(), epicUpdated.getId(), "id не совпадает.");
        assertEquals(name, epicUpdated.getName(), "Имя не совпадает.");
        assertEquals(Status.DONE, epicUpdated.getStatus(), "Статус не совпадает.");
    }

    @Test
    @DisplayName("поле epic должно быть null")
    void shouldNotAddSubTaskInEpicField() {
        String name = "новая задача обновление";

        SubTask subTaskUpdated = new SubTask(subTask.getId(), name,
                subTask.getDescription(), Status.DONE, subTask.getId());

        assertNull(taskManager.updateSubTask(subTaskUpdated), "эпик существует.");
        assertEquals(name, subTaskUpdated.getName(), "Имя не совпадает.");
        assertEquals(Status.DONE, subTaskUpdated.getStatus(), "Статус не совпадает.");
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

    private static void assertEqualsTask(Task expected, Task actual, String message) {
        assertEquals(expected.getId(), actual.getId(), message + ", id");
        assertEquals(expected.getName(), actual.getName(), message + ", name");
        assertEquals(expected.getDescription(), actual.getDescription(), message + ", description");
        assertEquals(expected.getStatus(), actual.getStatus(), message + ", status");
    }

    @Test
    @DisplayName("должен вернуть пустую задачу истории")
    void shouldDeleteIdSubTasksFromHistory() {
        Task taskNull = null;
        int id = subTask.getId();

        taskManager.removeBySubTaskId(subTask.getId());

        for (Task task : taskManager.getHistory()) {
            if (id == task.getId()) {
                taskNull = task;
            }
        }
        assertNull(taskNull, "Задача не удалена");
    }

}