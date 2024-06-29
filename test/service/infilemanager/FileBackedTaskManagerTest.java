package service.infilemanager;

import exception.ManagerIOException;
import model.dto.Epic;
import model.dto.Subtask;
import model.dto.Task;
import model.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.TaskManager;
import service.TaskManagerTest;
import service.inmemorymanager.InMemoryHistoryManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Менеджер задач из файла")
public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    TaskManager taskManager;
    File file;
    List<Task> emptyList;
    LocalDateTime localDateTime;
    Duration duration;
    Task task;
    Epic epic;
    Subtask subtask;

    @Override
    public FileBackedTaskManager createTaskManager() {
        try {
            file = File.createTempFile("testFileBackedManager", null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        return new FileBackedTaskManager(historyManager, file.toPath());
    }

    @BeforeEach
    public void init() {
        super.init();
        localDateTime = LocalDateTime.of(2024, 1, 14, 10, 0);
        duration = Duration.ofMinutes(15);
        taskManager = createTaskManager();
        emptyList = new ArrayList<>();
    }

    @Test
    @DisplayName("должен приводить к ошибке загруки файла")
    void shouldThrowIOException() {
        assertThrows(ManagerIOException.class,
                () -> FileBackedTaskManager.loadFromFile(Path.of("такого файла не существует")));
    }

    @Test
    @DisplayName("должен вернуть пустой список из файла")
    void shouldReturnEmptyListFromFile() {

        assertEquals(taskManager.getAllTasks(), emptyList, "задачи не совпадают");
        assertEquals(taskManager.getAllSubtasks(), emptyList, "задачи не совпадают");
        assertEquals(taskManager.getAllEpics(), emptyList, "задачи не совпадают");

        FileBackedTaskManager taskManager1 = FileBackedTaskManager.loadFromFile(file.toPath());

        assertEquals(taskManager.getAllTasks(), taskManager1.getAllTasks(), "задачи не совпадают");
        assertEquals(taskManager.getAllTasks(), taskManager1.getAllSubtasks(), "задачи не совпадают");
        assertEquals(taskManager.getAllTasks(), taskManager1.getAllEpics(), "задачи не совпадают");
    }

    @Test
    @DisplayName("задачи должны совпадать")
    void shouldTasksMatch() {
        task = taskManager.createTask(new Task("Новая задача", "Описание", Status.NEW));
        taskManager.getTaskById(task.getId()).setStartTime(localDateTime);
        epic = taskManager.createEpic(new Epic("Новый эпик", "Описание", Status.NEW));
        subtask = taskManager.createSubtask(new Subtask("Новая задача",
                "Описание", Status.NEW, epic.getId()));
        taskManager.getSubtaskById(subtask.getId()).setStartTime(localDateTime.plusMinutes(30));
        taskManager.updateSubtask(subtask);
        taskManager = FileBackedTaskManager.loadFromFile(file.toPath());
        List<Task> tasks = taskManager.getAllTasks();
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        List<Epic> epics = taskManager.getAllEpics();

        assertEqualsTask(task, tasks.getFirst(), "задачи не совпадают");
        assertEqualsTask(epic, epics.getFirst(), "задачи не совпадают");
        assertEqualsTask(subtask, subtasks.getFirst(), "задачи не совпадают");
    }

    @Override
    public void assertEqualsTask(Task expected, Task actual, String message) {
        super.assertEqualsTask(expected, actual, message);
    }
}
