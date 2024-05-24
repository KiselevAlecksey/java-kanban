package service.infilemanager;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.TaskManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("менеджер задач из файла")
public class FileBackedTaskManagerTest {

    TaskManager taskManager;
    Task task;
    Epic epic;
    SubTask subTask;
    File file;
    List<Task> emptyList;

    @BeforeEach
    void init() {

        try {
            file = File.createTempFile("testFileBackedManager", null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        taskManager = new FileBackedTaskManager(file.toPath());
        emptyList = new ArrayList<>();

    }

    @Test
    @DisplayName("должен вернуть пустой список из файла")
    void shouldReturnEmptyListFromFile() {

        assertEquals(taskManager.getAllTasks(), emptyList, "задачи не совпадают");
        assertEquals(taskManager.getAllSubTasks(), emptyList, "задачи не совпадают");
        assertEquals(taskManager.getAllEpics(), emptyList, "задачи не совпадают");

        FileBackedTaskManager taskManager1 = FileBackedTaskManager.loadFromFile(file.toPath());

        assertEquals(taskManager.getAllTasks(), taskManager1.getAllTasks(), "задачи не совпадают");
        assertEquals(taskManager.getAllTasks(), taskManager1.getAllSubTasks(), "задачи не совпадают");
        assertEquals(taskManager.getAllTasks(), taskManager1.getAllEpics(), "задачи не совпадают");
    }

    @Test
    @DisplayName("задачи должны совпадать")
    void shouldTasksMatch() {
        task = taskManager.createTask(new Task("Новая задача", "Описание", Status.NEW));
        epic = taskManager.createEpic(new Epic("Новый эпик", "Описание", Status.NEW));
        subTask = taskManager.createSubTask(new SubTask("Новая задача",
                "Описание", Status.NEW, epic.getId()));

        taskManager = FileBackedTaskManager.loadFromFile(file.toPath());
        List<Task> tasks = taskManager.getAllTasks();
        List<SubTask> subtasks = taskManager.getAllSubTasks();
        List<Epic> epics = taskManager.getAllEpics();

        assertEqualsTask(task, tasks.getFirst(), "задачи не совпадают");
        assertEqualsTask(epic, epics.getFirst(), "задачи не совпадают");
        assertEqualsTask(subTask, subtasks.getFirst(), "задачи не совпадают");
    }

    private static void assertEqualsTask(Task expected, Task actual, String message) {
        assertEquals(expected.getId(), actual.getId(), message + ", id");
        assertEquals(expected.getType(), actual.getType(), message + ", type");
        assertEquals(expected.getName(), actual.getName(), message + ", name");
        assertEquals(expected.getDescription(), actual.getDescription(), message + ", description");
        assertEquals(expected.getStatus(), actual.getStatus(), message + ", status");
    }
}
