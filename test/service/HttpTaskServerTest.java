package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.dto.Epic;
import model.dto.Subtask;
import model.dto.Task;
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
    HttpRequest request;
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
        Task expectedTask = new Task("Новая задача2", "Описание2", Status.NEW);
        expectedTask.setId(1);

        try (HttpClient client = HttpClient.newHttpClient()) {

            json = gson.toJson(expectedTask);
            url = URI.create("http://localhost:8080/tasks");
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(POST_CODE, response.statusCode());

            Task actual = manager.getTaskById(expectedTask.getId());
            assertEqualsTask(expectedTask, actual, "задачи не совпадают в менеджере");

        } catch (InterruptedException | IOException exception) {
            exception.printStackTrace(System.out);
        }
    }

    @DisplayName("Должен обновить существующую задачу")
    @Test
    public void ShouldUpdateTask() {
        expectedTask = manager.createTask(new Task("Новая задача", "Описание", Status.NEW));
        Task newTask = new Task("Новая задача2", "Описание2", Status.NEW);
        newTask.setId(1);
        try (HttpClient client = HttpClient.newHttpClient()) {

            json = gson.toJson(newTask);
            url = URI.create("http://localhost:8080/tasks/" + expectedTask.getId());
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(POST_CODE, response.statusCode());

            Task actual = gson.fromJson(response.body(), Task.class);
            assertEqualsTask(newTask, actual, "задачи не совпадают");

            actual = manager.getTaskById(expectedTask.getId());
            assertEqualsTask(newTask, actual, "задачи не совпадают в менеджере");

        } catch (InterruptedException | IOException exception) {
            exception.printStackTrace(System.out);
        }
    }

    @DisplayName("Должен вернуть задачу по id")
    @Test
    public void ShouldGetTask() {
        expectedTask = manager.createTask(new Task("Новая задача", "Описание", Status.NEW));
        try (HttpClient client = HttpClient.newHttpClient()) {

            json = gson.toJson(expectedTask);
            url = URI.create("http://localhost:8080/tasks/" + expectedTask.getId());
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(GET_CODE, response.statusCode());

            Task actual = gson.fromJson(response.body(), Task.class);
            assertEqualsTask(expectedTask, actual, "задачи не совпадают");

            actual = manager.getTaskById(expectedTask.getId());
            assertEqualsTask(expectedTask, actual, "задачи не совпадают в менеджере");

        } catch (InterruptedException | IOException exception) {
            exception.printStackTrace(System.out);
        }
    }

    @DisplayName("Должен вернуть все задачи")
    @Test
    public void ShouldGetAllTasks() {
        expectedTask = manager.createTask(new Task("Новая задача", "Описание", Status.NEW));
        try (HttpClient client = HttpClient.newHttpClient()) {

            json = gson.toJson(manager.getAllTasks());
            url = URI.create("http://localhost:8080/tasks/");
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(GET_CODE, response.statusCode());

            List<Task> tasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
            }.getType());

            assertNotNull(tasks);
            assertEquals(manager.getAllTasks().size(), tasks.size(), "Размер списка задач должен совпадать");

        } catch (InterruptedException | IOException exception) {
            exception.printStackTrace(System.out);
        }
    }

    @DisplayName("Должен удалить задачу")
    @Test
    public void ShouldDeleteTask() {
        expectedTask = manager.createTask(new Task("Новая задача", "Описание", Status.NEW));

        try (HttpClient client = HttpClient.newHttpClient()) {

            json = gson.toJson(expectedTask);
            url = URI.create("http://localhost:8080/tasks/" + expectedTask.getId());
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(DELETE_CODE, response.statusCode());

            assertEquals(0, manager.getAllTasks().size(), "Размер списка задач должен совпадать");

        } catch (InterruptedException | IOException exception) {
            exception.printStackTrace(System.out);
        }
    }

    @DisplayName("Должен создать подзадачу")
    @Test
    public void ShouldCreateSubtask() {
        expectedEpic = manager.createEpic(new Epic("Новый эпик", "Описание", Status.NEW));
        Subtask newSubtask = new Subtask("Новая подзадача2", "Описание2", Status.NEW, expectedEpic.getId());
        newSubtask.setId(2);
        try (HttpClient client = HttpClient.newHttpClient()) {

            json = gson.toJson(newSubtask);
            url = URI.create("http://localhost:8080/subtasks");
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(POST_CODE, response.statusCode());

            Subtask actual = manager.getSubtaskById(newSubtask.getId());
            assertEqualsTask(newSubtask, actual, "подзадачи не совпадают в менеджере");

        } catch (InterruptedException | IOException exception) {
            exception.printStackTrace(System.out);
        }
    }

    @DisplayName("Должен обновить существующую подзадачу")
    @Test
    public void ShouldUpdateSubtask() {
        expectedEpic = manager
                .createEpic(new Epic("Новый эпик", "Описание", Status.NEW));
        expectedSubtask = manager
                .createSubtask(new Subtask("Новая подзадача", "Описание", Status.NEW, expectedEpic.getId()));
        Subtask newSubtask = new Subtask("Новая подзадача2", "Описание2", Status.NEW, expectedEpic.getId());
        newSubtask.setId(2);

        try (HttpClient client = HttpClient.newHttpClient()) {

            json = gson.toJson(newSubtask);
            url = URI.create("http://localhost:8080/subtasks/" + expectedSubtask.getId());
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(POST_CODE, response.statusCode());

            Subtask actual = gson.fromJson(response.body(), Subtask.class);
            assertEqualsTask(newSubtask, actual, "подзадачи не совпадают");

            actual = manager.getSubtaskById(expectedSubtask.getId());
            assertEqualsTask(newSubtask, actual, "подзадачи не совпадают в менеджере");

        } catch (InterruptedException | IOException exception) {
            exception.printStackTrace(System.out);
        }
    }

    @DisplayName("Должен вернуть подзадачу по id")
    @Test
    public void ShouldGetSubtask() {
        expectedEpic = manager
                .createEpic(new Epic("Новый эпик", "Описание", Status.NEW));
        expectedSubtask = manager
                .createSubtask(new Subtask("Новая подзадача", "Описание", Status.NEW, expectedEpic.getId()));

        try (HttpClient client = HttpClient.newHttpClient()) {

            json = gson.toJson(expectedSubtask);
            url = URI.create("http://localhost:8080/subtasks/" + expectedSubtask.getId());
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(GET_CODE, response.statusCode());

            Subtask actual = gson.fromJson(response.body(), Subtask.class);
            assertEqualsTask(expectedSubtask, actual, "подзадачи не совпадают");

            actual = manager.getSubtaskById(expectedSubtask.getId());
            assertEqualsTask(expectedSubtask, actual, "подзадачи не совпадают в менеджере");

        } catch (InterruptedException | IOException exception) {
            exception.printStackTrace(System.out);
        }
    }

    @DisplayName("Должен вернуть все подзадачи")
    @Test
    public void ShouldGetAllSubtasks() {
        expectedEpic = manager
                .createEpic(new Epic("Новый эпик", "Описание", Status.NEW));
        expectedSubtask = manager
                .createSubtask(new Subtask("Новая подзадача", "Описание", Status.NEW, expectedEpic.getId()));

        try (HttpClient client = HttpClient.newHttpClient()) {

            json = gson.toJson(manager.getAllSubtasks());
            url = URI.create("http://localhost:8080/subtasks/");
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(GET_CODE, response.statusCode());

            List<Subtask> subtasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Subtask>>() {
            }.getType());

            assertNotNull(subtasks);
            assertEquals(manager.getAllSubtasks().size(), subtasks.size(), "Размер списка подзадач должен совпадать");

        } catch (InterruptedException | IOException exception) {
            exception.printStackTrace(System.out);
        }
    }

    @DisplayName("Должен удалить подзадачу")
    @Test
    public void ShouldDeleteSubtask() {
        expectedEpic = manager
                .createEpic(new Epic("Новый эпик", "Описание", Status.NEW));
        expectedSubtask = manager
                .createSubtask(new Subtask("Новая подзадача", "Описание", Status.NEW, expectedEpic.getId()));

        try (HttpClient client = HttpClient.newHttpClient()) {

            json = gson.toJson(expectedSubtask);
            url = URI.create("http://localhost:8080/subtasks/" + expectedSubtask.getId());
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(DELETE_CODE, response.statusCode());

            assertEquals(0, manager.getAllSubtasks().size(), "Размер списка подзадач должен совпадать");

        } catch (InterruptedException | IOException exception) {
            exception.printStackTrace(System.out);
        }
    }

    @DisplayName("Должен создать эпик")
    @Test
    public void ShouldCreateEpic() {
        Epic expectedEpic = new Epic("Новый эпик2", "Описание2", Status.NEW);
        expectedEpic.setId(1);
        try (HttpClient client = HttpClient.newHttpClient()) {

            json = gson.toJson(expectedEpic);
            url = URI.create("http://localhost:8080/epics");
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(POST_CODE, response.statusCode());

            Epic actual = manager.getEpicById(expectedEpic.getId());
            assertEqualsTask(expectedEpic, actual, "эпики не совпадают в менеджере");

        } catch (InterruptedException | IOException exception) {
            exception.printStackTrace(System.out);
        }
    }

    @DisplayName("Должен вернуть эпик по id")
    @Test
    public void ShouldGetEpic() {
        expectedEpic = manager
                .createEpic(new Epic("Новый эпик", "Описание", Status.NEW));
        expectedSubtask = manager
                .createSubtask(new Subtask("Новая подзадача", "Описание", Status.NEW, expectedEpic.getId()));

        try (HttpClient client = HttpClient.newHttpClient()) {

            json = gson.toJson(expectedEpic);
            url = URI.create("http://localhost:8080/epics/" + expectedEpic.getId());
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(GET_CODE, response.statusCode());

            Epic actual = gson.fromJson(response.body(), Epic.class);
            assertEqualsTask(expectedEpic, actual, "эпики не совпадают");

            actual = manager.getEpicById(expectedEpic.getId());
            assertEqualsTask(expectedEpic, actual, "эпики не совпадают в менеджере");

        } catch (InterruptedException | IOException exception) {
            exception.printStackTrace(System.out);
        }
    }

    @DisplayName("Должен вернуть все эпики")
    @Test
    public void ShouldGetAllEpics() {
        expectedEpic = manager
                .createEpic(new Epic("Новый эпик", "Описание", Status.NEW));
        expectedSubtask = manager
                .createSubtask(new Subtask("Новая подзадача", "Описание", Status.NEW, expectedEpic.getId()));

        try (HttpClient client = HttpClient.newHttpClient()) {

            json = gson.toJson(manager.getAllEpics());
            url = URI.create("http://localhost:8080/epics/");
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(GET_CODE, response.statusCode());

            List<Epic> epics = gson.fromJson(response.body(), new TypeToken<ArrayList<Epic>>() {
            }.getType());

            assertNotNull(epics);
            assertEquals(manager.getAllEpics().size(), epics.size(), "Размер списка эпиков должен совпадать");

        } catch (InterruptedException | IOException exception) {
            exception.printStackTrace(System.out);
        }
    }

    @DisplayName("Должен вернуть все подзадачи эпика")
    @Test
    public void ShouldGetAllSubtasksEpic() {
        expectedEpic = manager
                .createEpic(new Epic("Новый эпик", "Описание", Status.NEW));
        expectedSubtask = manager
                .createSubtask(new Subtask("Новая подзадача", "Описание", Status.NEW, expectedEpic.getId()));

        try (HttpClient client = HttpClient.newHttpClient()) {

            json = gson.toJson(manager.getEpicSubtasks(expectedEpic));
            url = URI.create("http://localhost:8080/epics/");
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(GET_CODE, response.statusCode());

            List<Subtask> subtasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Subtask>>() {
            }.getType());

            assertNotNull(subtasks);
            assertEquals(manager
                    .getEpicSubtasks(expectedEpic).size(), subtasks.size(), "Размер списка подзадач должен совпадать");

        } catch (InterruptedException | IOException exception) {
            exception.printStackTrace(System.out);
        }
    }

    @DisplayName("Должен удалить эпик")
    @Test
    public void ShouldDeleteEpic() {
        expectedEpic = manager
                .createEpic(new Epic("Новый эпик", "Описание", Status.NEW));
        expectedSubtask = manager
                .createSubtask(new Subtask("Новая подзадача", "Описание", Status.NEW, expectedEpic.getId()));

        try (HttpClient client = HttpClient.newHttpClient()) {

            json = gson.toJson(expectedEpic);
            url = URI.create("http://localhost:8080/epics/" + expectedEpic.getId());
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(DELETE_CODE, response.statusCode());

            assertEquals(0, manager.getAllEpics().size(), "Размер списка подзадач должен совпадать");
            assertEquals(0, manager.getEpicSubtasks(expectedEpic).size(), "Размер списка подзадач должен совпадать");
        } catch (InterruptedException | IOException exception) {
            exception.printStackTrace(System.out);
        }
    }

    @DisplayName("Должен вернуть историю задач")
    @Test
    public void ShouldGetHistory() {
        expectedEpic = manager
                .createEpic(new Epic("Новый эпик", "Описание", Status.NEW));
        expectedSubtask = manager
                .createSubtask(new Subtask("Новая подзадача", "Описание", Status.NEW, expectedEpic.getId()));
        expectedTask = manager
                .createTask(new Task(2, "Новая задача", Status.NEW, "Описание", duration, localDateTime));
        manager.getTaskById(expectedTask.getId());
        manager.getSubtaskById(expectedSubtask.getId());
        manager.getEpicById(expectedEpic.getId());

        try (HttpClient client = HttpClient.newHttpClient()) {

            url = URI.create("http://localhost:8080/history/");
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(GET_CODE, response.statusCode());

            List<Task> history = manager.getHistory();

            assertNotNull(history);
            assertEquals(history.size(), 3, "Размер истории задач должен совпадать");

        } catch (InterruptedException | IOException exception) {
            exception.printStackTrace(System.out);
        }
    }

    @DisplayName("Должен вернуть список приоритетных задач")
    @Test
    public void ShouldGetPrioritizedTasks() {
        expectedEpic = manager
                .createEpic(new Epic("Новый эпик", "Описание", Status.NEW));
        expectedSubtask = manager
                .createSubtask(new Subtask("Новая подзадача", "Описание", Status.NEW, expectedEpic.getId()));
        expectedTask = manager
                .createTask(new Task(2, "Новая задача", Status.NEW, "Описание", duration, localDateTime));

        try (HttpClient client = HttpClient.newHttpClient()) {

            url = URI.create("http://localhost:8080/prioritized/");
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(GET_CODE, response.statusCode());

            List<Task> prioritizedList = manager.getPrioritizedTasks();

            assertNotNull(prioritizedList);
            assertEquals(prioritizedList.size(), 2, "Размер списка приоритетных задач должен совпадать");

        } catch (InterruptedException | IOException exception) {
            exception.printStackTrace(System.out);
        }
    }

    private void assertEqualsTask(Task expected, Task actual, String message) {
        assertEquals(expected.getId(), actual.getId(), message + ", id");
        assertEquals(expected.getType(), actual.getType(), message + ", type");
        assertEquals(expected.getName(), actual.getName(), message + ", name");
        assertEquals(expected.getDescription(), actual.getDescription(), message + ", description");
        assertEquals(expected.getStatus(), actual.getStatus(), message + ", status");
        assertEquals(expected.getStartTime(), actual.getStartTime(), message + ", startTime");
    }
}
