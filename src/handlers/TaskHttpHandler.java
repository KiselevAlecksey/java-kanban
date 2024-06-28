package handlers;

import com.sun.net.httpserver.HttpExchange;
import model.dto.Task;
import model.enums.Endpoint;
import service.TaskManager;
import util.GsonConverter;

import static model.responsecode.ResponseCode.*;

public class TaskHttpHandler extends BaseHttpHandler {

    public TaskHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        System.out.println("Старт обработки задачи " + dateTime);

        try (exchange) {

            Endpoint endpoint = getEndpoint(exchange.getRequestMethod());
            String[] pathEndpoint = exchange.getRequestURI().getPath().split("/");

            try {
                switch (endpoint) {

                    case GET -> {
                        if (pathEndpoint.length == 2) {
                            sendResponse(exchange, GsonConverter
                                    .getGson().toJson(taskManager.getAllTasks()), GET_CODE);
                        } else if (pathEndpoint.length == 3) {
                            sendResponse(exchange, GsonConverter
                                    .getGson().toJson(taskManager.getTaskById(getPathId(pathEndpoint[2]))), GET_CODE);
                        }
                    }

                    case POST -> {
                        String body = new String(exchange.getRequestBody().readAllBytes(), CHARSET);
                        Task task = GsonConverter.getGson().fromJson(body, Task.class);

                        if (pathEndpoint.length == 2) {
                            taskManager.createTask(task);
                            sendResponse(exchange, GsonConverter
                                    .getGson().toJson(taskManager.getTaskById(task.getId())), POST_CODE);
                        } else if (pathEndpoint.length == 3) {
                            taskManager.getTaskById(task.getId());
                            taskManager.updateTask(task);
                            sendResponse(exchange, GsonConverter
                                    .getGson().toJson(taskManager.getTaskById(task.getId())), POST_CODE);
                        }
                    }

                    case DELETE -> {
                        int id = getPathId(pathEndpoint[2]);
                        taskManager.removeByTaskId(id);
                        sendDeleteCode(exchange);
                    }

                    case UNKNOWN -> {
                        sendResponse(exchange, "Неизвестная команда", BAD_REQUEST_ERROR);
                    }

                    default -> sendResponse(exchange, "Внутренняя ошибка сервера", INTERNAL_SERVER_ERROR);
                }

            } catch (Exception exception) {
                sendException(exchange, exception);
                exception.printStackTrace(System.out);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        System.out.println("Конец обработки задачи " + dateTime);
    }
}
