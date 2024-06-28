package handlers;

import com.sun.net.httpserver.HttpExchange;
import model.dto.Subtask;
import model.enums.Endpoint;
import service.TaskManager;
import util.GsonConverter;

import static model.responsecode.ResponseCode.*;

public class SubtaskHttpHandler extends BaseHttpHandler {

    public SubtaskHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        System.out.println("Старт обработки подзадачи " + dateTime);

        try (exchange) {

            Endpoint endpoint = getEndpoint(exchange.getRequestMethod());
            String[] pathEndpoint = exchange.getRequestURI().getPath().split("/");

            try {
                switch (endpoint) {

                    case GET -> {

                        if (pathEndpoint.length == 2) {
                            sendResponse(exchange, GsonConverter
                                    .getGson().toJson(taskManager.getAllSubtasks()), GET_CODE);
                        } else if (pathEndpoint.length == 3) {
                            sendResponse(exchange, GsonConverter
                                    .getGson().toJson(taskManager.getSubtaskById(getPathId(pathEndpoint[2]))), GET_CODE);
                        }
                    }

                    case POST -> {
                        String body = new String(exchange.getRequestBody().readAllBytes(), CHARSET);
                        Subtask subtask = GsonConverter.getGson().fromJson(body, Subtask.class);

                        if (pathEndpoint.length == 2) {
                            taskManager.createSubtask(subtask);
                            sendResponse(exchange, GsonConverter
                                    .getGson().toJson(taskManager.getSubtaskById(subtask.getId())), POST_CODE);
                        } else if (pathEndpoint.length == 3) {
                            taskManager.getSubtaskById(subtask.getId());
                            taskManager.updateSubtask(subtask);
                            sendResponse(exchange, GsonConverter
                                    .getGson().toJson(taskManager.getSubtaskById(subtask.getId())), POST_CODE);
                        }
                    }

                    case DELETE -> {
                        int id = getPathId(pathEndpoint[2]);
                        taskManager.removeBySubtaskId(id);
                        sendDeleteCode(exchange);
                    }

                    case UNKNOWN -> {
                        sendResponse(exchange, "Неизвестная команда", BAD_REQUEST_ERROR);
                    }

                    default -> sendResponse(exchange, "Внутренняя ошибка сервера", INTERNAL_SERVER_ERROR);
                }

            } catch (Exception exception) {
                sendException(exchange, exception);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        System.out.println("Конец обработки подзадачи " + dateTime);
    }
}
