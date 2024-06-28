package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import exception.ManagerIOException;
import exception.NotFoundException;
import model.dto.Epic;
import model.dto.Subtask;
import model.dto.Task;
import model.enums.Endpoint;
import model.enums.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import service.inmemorymanager.InMemoryTaskManager;
import util.GsonConverter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static model.enums.Endpoint.*;
import static model.responsecode.ResponseCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Сервер менеджера задач")
public class HttpTaskServerTest {
    HistoryManager historyManager = Managers.getDefaultHistory();
    TaskManager manager;
    HttpTaskServer server;
    Gson gson = GsonConverter.getGson();
    LocalDateTime localDateTime;
    Duration duration;
    Task expectedTask;
    Epic expectedEpic;
    Subtask expectedSubtask;
    String json;
    URI url;

    @BeforeEach
    public void init() {
        historyManager = Managers.getDefaultHistory();
        manager = new InMemoryTaskManager(historyManager);
        server = new HttpTaskServer(manager);
        server.run();
        url = URI.create("http://localhost:8080/");

        localDateTime = LocalDateTime.of(2024, 1, 14, 10, 0);
        duration = Duration.ofMinutes(15);
    }

    @AfterEach
    public void end() {
        server.stop();
    }

    @DisplayName("Должен создать задачу")
    @Test
    public void ShouldCreateTask() {
        expectedTask = new Task("Новая задача2", "Описание2", Status.NEW);
        expectedTask.setId(1);
        json = gson.toJson(expectedTask);
        HttpResponse<String> response = getResponse(json, "tasks/", POST);

        assertEquals(POST_CODE, response.statusCode());

        Task actual = manager.getTaskById(expectedTask.getId());
        assertEqualsTask(expectedTask, actual, "задачи не совпадают в менеджере");

    }

    @DisplayName("Должен обновить существующую задачу")
    @Test
    public void ShouldUpdateTask() {
        createTasksThroughManager("task");
        Task newTask = new Task("Новая задача2", "Описание2", Status.NEW);
        newTask.setId(1);
        json = gson.toJson(newTask);
        HttpResponse<String> response = getResponse(json, "tasks/" + expectedTask.getId(), POST);

        assertEquals(POST_CODE, response.statusCode());

        Task actual = gson.fromJson(response.body(), Task.class);
        assertEqualsTask(newTask, actual, "задачи не совпадают");

        actual = manager.getTaskById(expectedTask.getId());
        assertEqualsTask(newTask, actual, "задачи не совпадают в менеджере");
    }

    @DisplayName("Должен вернуть задачу по id")
    @Test
    public void ShouldGetTask() {
        createTasksThroughManager("task");
        json = gson.toJson(expectedTask);
        HttpResponse<String> response = getResponse(json, "tasks/" + expectedTask.getId(), GET);

        assertEquals(GET_CODE, response.statusCode());

        Task actual = gson.fromJson(response.body(), Task.class);
        assertEqualsTask(expectedTask, actual, "задачи не совпадают");

        actual = manager.getTaskById(expectedTask.getId());
        assertEqualsTask(expectedTask, actual, "задачи не совпадают в менеджере");

    }

    @DisplayName("Должен вернуть все задачи")
    @Test
    public void ShouldGetAllTasks() {
        createTasksThroughManager("task");
        json = gson.toJson(manager.getAllTasks());
        HttpResponse<String> response = getResponse(json, "tasks/", GET);

        assertEquals(GET_CODE, response.statusCode());

        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());

        assertNotNull(tasks);
        assertEquals(manager.getAllTasks().size(), tasks.size(), "Размер списка задач должен совпадать");
    }

    @DisplayName("Должен удалить задачу")
    @Test
    public void ShouldDeleteTask() {
        createTasksThroughManager("task");
        json = gson.toJson(expectedTask);
        HttpResponse<String> response = getResponse(json, "tasks/" + expectedTask.getId(), DELETE);

        assertEquals(DELETE_CODE, response.statusCode());

        assertEquals(0, manager.getAllTasks().size(), "Размер списка задач должен совпадать");
    }

    @DisplayName("Должен создать подзадачу")
    @Test
    public void ShouldCreateSubtask() {
        expectedEpic = manager.createEpic(new Epic("Новый эпик", "Описание", Status.NEW));
        Subtask newSubtask = new Subtask("Новая подзадача2", "Описание2", Status.NEW, expectedEpic.getId());
        newSubtask.setId(2);
        json = gson.toJson(newSubtask);
        HttpResponse<String> response = getResponse(json, "subtasks/", POST);

        assertEquals(POST_CODE, response.statusCode());

        Subtask actual = manager.getSubtaskById(newSubtask.getId());
        assertEqualsTask(newSubtask, actual, "подзадачи не совпадают в менеджере");
    }

    @DisplayName("Должен обновить существующую подзадачу")
    @Test
    public void ShouldUpdateSubtask() {
        createTasksThroughManager("subtask");
        Subtask newSubtask = new Subtask("Новая подзадача2", "Описание2", Status.NEW, expectedEpic.getId());
        newSubtask.setId(2);
        json = gson.toJson(newSubtask);
        HttpResponse<String> response = getResponse(json, "subtasks/" + expectedSubtask.getId(), POST);

        assertEquals(POST_CODE, response.statusCode());

        Subtask actual = gson.fromJson(response.body(), Subtask.class);
        assertEqualsTask(newSubtask, actual, "подзадачи не совпадают");

        actual = manager.getSubtaskById(expectedSubtask.getId());
        assertEqualsTask(newSubtask, actual, "подзадачи не совпадают в менеджере");
    }

    @DisplayName("Должен вернуть подзадачу по id")
    @Test
    public void ShouldGetSubtask() {
        createTasksThroughManager("subtask");
        json = gson.toJson(expectedSubtask);
        HttpResponse<String> response = getResponse(json, "subtasks/" + expectedSubtask.getId(), GET);

        assertEquals(GET_CODE, response.statusCode());

        Subtask actual = gson.fromJson(response.body(), Subtask.class);
        assertEqualsTask(expectedSubtask, actual, "подзадачи не совпадают");

        actual = manager.getSubtaskById(expectedSubtask.getId());
        assertEqualsTask(expectedSubtask, actual, "подзадачи не совпадают в менеджере");
    }

    @DisplayName("Должен вернуть все подзадачи")
    @Test
    public void ShouldGetAllSubtasks() {
        createTasksThroughManager("subtask");
        json = gson.toJson(manager.getAllSubtasks());
        HttpResponse<String> response = getResponse(json, "subtasks/", GET);

        assertEquals(GET_CODE, response.statusCode());

        List<Subtask> subtasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Subtask>>() {
        }.getType());

        assertNotNull(subtasks);
        assertEquals(manager.getAllSubtasks().size(), subtasks.size(), "Размер списка подзадач должен совпадать");
    }

    @DisplayName("Должен удалить подзадачу")
    @Test
    public void ShouldDeleteSubtask() {
        createTasksThroughManager("subtask");
        json = gson.toJson(expectedSubtask);
        HttpResponse<String> response = getResponse(json, "subtasks/" + expectedSubtask.getId(), DELETE);

        assertEquals(DELETE_CODE, response.statusCode());

        assertEquals(0, manager.getAllSubtasks().size(), "Размер списка подзадач должен совпадать");
    }

    @DisplayName("Должен создать эпик")
    @Test
    public void ShouldCreateEpic() {
        Epic expectedEpic = new Epic("Новый эпик2", "Описание2", Status.NEW);
        expectedEpic.setId(1);
        json = gson.toJson(expectedEpic);
        HttpResponse<String> response = getResponse(json, "epics/", POST);

        assertEquals(POST_CODE, response.statusCode());

        Epic actual = manager.getEpicById(expectedEpic.getId());
        assertEqualsTask(expectedEpic, actual, "эпики не совпадают в менеджере");
    }

    @DisplayName("Должен вернуть эпик по id")
    @Test
    public void ShouldGetEpic() {
        createTasksThroughManager("epic");
        json = gson.toJson(expectedEpic);
        HttpResponse<String> response = getResponse(json, "epics/" + expectedEpic.getId(), GET);

        assertEquals(GET_CODE, response.statusCode());

        Epic actual = gson.fromJson(response.body(), Epic.class);
        assertEqualsTask(expectedEpic, actual, "эпики не совпадают");

        actual = manager.getEpicById(expectedEpic.getId());
        assertEqualsTask(expectedEpic, actual, "эпики не совпадают в менеджере");
    }

    @DisplayName("Должен вернуть все эпики")
    @Test
    public void ShouldGetAllEpics() {
        createTasksThroughManager("epic");
        json = gson.toJson(manager.getAllEpics());
        HttpResponse<String> response = getResponse(json, "epics/", GET);

        assertEquals(GET_CODE, response.statusCode());

        List<Epic> epics = gson.fromJson(response.body(), new TypeToken<ArrayList<Epic>>() {
        }.getType());

        assertNotNull(epics);
        assertEquals(manager.getAllEpics().size(), epics.size(), "Размер списка эпиков должен совпадать");
    }

    @DisplayName("Должен вернуть все подзадачи эпика")
    @Test
    public void ShouldGetAllSubtasksEpic() {
        createTasksThroughManager("epic");
        json = gson.toJson(manager.getEpicSubtasks(expectedEpic));
        HttpResponse<String> response = getResponse(json, "epics/" + expectedEpic.getId() + "/subtasks", GET);

        assertEquals(GET_CODE, response.statusCode());

        List<Subtask> subtasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Subtask>>() {
        }.getType());

        assertNotNull(subtasks);
        assertEquals(manager
                .getEpicSubtasks(expectedEpic).size(), subtasks.size(), "Размер списка подзадач должен совпадать");
    }

    @DisplayName("Должен удалить эпик")
    @Test
    public void ShouldDeleteEpic() {
        createTasksThroughManager("epic");
        json = gson.toJson(expectedEpic);
        HttpResponse<String> response = getResponse(json, "epics/" + expectedEpic.getId(), DELETE);

        assertEquals(DELETE_CODE, response.statusCode());
        assertEquals(0, manager.getAllEpics().size(), "Размер списка подзадач должен совпадать");
        assertEquals(0, manager.getEpicSubtasks(expectedEpic).size(), "Размер списка подзадач должен совпадать");
    }

    @DisplayName("Должен вернуть историю задач")
    @Test
    public void ShouldGetHistory() {
        createTasksThroughManager("all");
        manager.getTaskById(expectedTask.getId());
        manager.getSubtaskById(expectedSubtask.getId());
        manager.getEpicById(expectedEpic.getId());
        HttpResponse<String> response = getResponse(json, "history/", GET);

        assertEquals(GET_CODE, response.statusCode());

        List<Task> history = manager.getHistory();

        assertNotNull(history);
        assertEquals(history.size(), 3, "Размер истории задач должен совпадать");
    }

    @DisplayName("Должен вернуть список приоритетных задач")
    @Test
    public void ShouldGetPrioritizedTasks() {
        createTasksThroughManager("all");
        HttpResponse<String> response = getResponse(json, "prioritized/", GET);

        assertEquals(GET_CODE, response.statusCode());

        List<Task> prioritizedList = manager.getPrioritizedTasks();

        assertNotNull(prioritizedList);
        assertEquals(prioritizedList.size(), 2, "Размер списка приоритетных задач должен совпадать");
    }

    private void assertEqualsTask(Task expected, Task actual, String message) {
        assertEquals(expected.getId(), actual.getId(), message + ", id");
        assertEquals(expected.getType(), actual.getType(), message + ", type");
        assertEquals(expected.getName(), actual.getName(), message + ", name");
        assertEquals(expected.getDescription(), actual.getDescription(), message + ", description");
        assertEquals(expected.getStatus(), actual.getStatus(), message + ", status");
        assertEquals(expected.getStartTime(), actual.getStartTime(), message + ", startTime");
    }

    private HttpResponse<String> getResponse(String requestBody, String path, Endpoint method) {

        try (HttpClient client = HttpClient.newHttpClient()) {
            url = URI.create("http://localhost:8080/" + path);
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(url);

            switch (method) {
                case GET -> requestBuilder.GET();
                case POST -> requestBuilder.POST(HttpRequest.BodyPublishers.ofString(requestBody));
                case DELETE -> requestBuilder.DELETE();
                default -> throw new NotFoundException("Unsupported method type: " + method);
            }

            HttpRequest request = requestBuilder.build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException exception) {
            exception.printStackTrace(System.out);
            throw new ManagerIOException("Подзадача не найдена: ", exception);
        }
    }

    private void createTasksThroughManager(String taskName) {

        switch (taskName) {
            case "task" -> expectedTask = manager.createTask(new Task("Новая задача", "Описание", Status.NEW));
            case "subtask", "epic" -> {
                expectedEpic = manager
                        .createEpic(new Epic("Новый эпик", "Описание", Status.NEW));
                expectedSubtask = manager
                        .createSubtask(new Subtask("Новая подзадача", "Описание", Status.NEW, expectedEpic.getId()));
            }
            case "all" -> {
                expectedEpic = manager
                        .createEpic(new Epic("Новый эпик", "Описание", Status.NEW));
                expectedSubtask = manager
                        .createSubtask(new Subtask("Новая подзадача", "Описание", Status.NEW, expectedEpic.getId()));
                expectedTask = manager
                        .createTask(new Task(2, "Новая задача", Status.NEW, "Описание", duration, localDateTime));
            }
        }
    }
}
