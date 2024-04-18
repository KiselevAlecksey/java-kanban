package service;

import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("менеджер истории")
class InMemoryHistoryManagerTest {

    Task task;
    List<Task> list;
    final int count = 12;
    HistoryManager historyManager;
    TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault();
        list = new ArrayList<>();
        task = new Task("Новая задача", "Описание", Status.NEW);
    }


    @Test
    @DisplayName("должен быть не пустым")
    void shouldAddCorrectTasks() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    @DisplayName("должен сохранять значения по порядку, " +
            "при заполнении удалять значение с индексом 0, добавлять с индексом 9")
    void shouldSaveCorrectTasks() {
        for (int i = 0; i < count; i++) {
            task = new Task("Новая задача" + i, "Описание" + i, Status.NEW);
            historyManager.add(task);
        }
        Task task = new Task("Новая задача2", "Описание2", Status.NEW);
        Task task2 = new Task("Новая задача11", "Описание11", Status.NEW);
        List<Task> tasks = historyManager.getHistory();

        assertEqualsTask(task, tasks.get(0), "задачи не совпадают");
        assertEqualsTask(task2, tasks.get(9), "задачи не совпадают");
    }

    private static void assertEqualsTask(Task expected, Task actual, String message) {
        assertEquals(expected.getId(), actual.getId(), message + ", id");
        assertEquals(expected.getName(), actual.getName(), message + ", name");
        assertEquals(expected.getDescription(), actual.getDescription(), message + ", description");
        assertEquals(expected.getStatus(), actual.getStatus(), message + ", status");
    }
}