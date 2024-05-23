package service.inmemorymanager;

import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.Managers;
import service.TaskManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("менеджер истории")
class InMemoryHistoryManagerTest {

    Task task;
    List<Task> list;
    final int count = 12;
    HistoryManager historyManager;
    TaskManager taskManager;
    int countDelete = 0;
    int startId = 0;
    int midId;
    int endId;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault();
        list = new ArrayList<>();
        task = new Task("Новая задача", "Описание", Status.NEW);
        countDelete = 0;
        startId = 0;
        midId = 0;
        endId = 0;
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
    @DisplayName("должен сохранять значения по порядку, без повторов")
    void shouldSaveCorrectTasks() {
        LinkedList<Task> uniqueTasks = new LinkedList<>();

        for (int i = 0; i < count; i++) {
            task = new Task(i,"Новая задача" + i, "Описание" + i, Status.NEW);
            historyManager.add(task);
            uniqueTasks.add(task);
        }

        for (int i = 0; i < count; i++) {
            task = new Task(i,"Новая задача" + i, "Описание" + i, Status.NEW);
            historyManager.add(task);
        }

        list = historyManager.getHistory();

        for (int i = 0; i < count; i++) {
            assertEqualsTask(list.get(i), uniqueTasks.get(i), "задачи не совпадают");
        }

        assertEquals(list.size(), uniqueTasks.size(), "длина не совпадает");
    }

    private static void assertEqualsTask(Task expected, Task actual, String message) {
        assertEquals(expected.getId(), actual.getId(), message + ", id");
        assertEquals(expected.getName(), actual.getName(), message + ", name");
        assertEquals(expected.getDescription(), actual.getDescription(), message + ", description");
        assertEquals(expected.getStatus(), actual.getStatus(), message + ", status");
    }


    @Test
    @DisplayName("должен удалять задачи из истории")
    void shouldDeleteHistoryTask() {

        for (int i = 1; i < count + 1; i++) {
            task = new Task(i,"Новая задача" + i, "Описание" + i, Status.NEW);
            historyManager.add(task);
        }

        list = historyManager.getHistory();

        historyManager.remove(startId);
        updateList();
        Task task1 = new Task(countDelete,"Новая задача" + countDelete,
                "Описание" + countDelete, Status.NEW);

        historyManager.remove(midId);
        updateList();
        Task task7 = new Task(midId + countDelete,"Новая задача" + (midId + countDelete),
                "Описание" + (midId + countDelete), Status.NEW);

        historyManager.remove(endId);
        updateList();
        Task task12 = new Task(endId + countDelete,"Новая задача" + (endId + countDelete),
                "Описание" + (endId + countDelete), Status.NEW);

        assertEqualsTask(task1, list.get(startId), "должны совпадать");
        assertEqualsTask(task7, list.get(midId), "должны совпадать");
        assertEqualsTask(task12, list.get(endId), "должны совпадать");
    }

    private void updateList() {
        countDelete++;
        list = historyManager.getHistory();
        midId = list.size() / 2;
        endId = list.size() - 1;
    }

}