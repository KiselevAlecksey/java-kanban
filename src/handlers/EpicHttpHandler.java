package handlers;

import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import model.dto.Epic;
import model.enums.Endpoint;
import service.TaskManager;
import util.GsonConverter;

import static model.responsecode.ResponseCode.*;

public class EpicHttpHandler extends BaseHttpHandler {

    public EpicHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        System.out.println("Старт обработки эпика " + dateTime);

        try (exchange) {

            Endpoint endpoint = getEndpoint(exchange.getRequestMethod());
            String[] pathEndpoint = exchange.getRequestURI().getPath().split("/");

            try {
                switch (endpoint) {

                    case GET -> {
                        if (pathEndpoint.length == 2) {
                            sendResponse(exchange, GsonConverter
                                    .getGson().toJson(taskManager.getAllEpics()), GET_CODE);
                        } else if (pathEndpoint.length == 3) {
                            sendResponse(exchange,
                                    GsonConverter.getGson()
                                            .toJson(taskManager.getEpicById(getPathId(pathEndpoint[2]))), GET_CODE);
                        } else if (pathEndpoint.length == 4) {
                            sendResponse(exchange,
                                    GsonConverter.getGson()
                                            .toJson(taskManager.getEpicSubtasks(taskManager
                                                    .getEpicById(getPathId(pathEndpoint[2])))), GET_CODE);
                        }
                    }

                    case POST -> {
                        String body = new String(exchange.getRequestBody().readAllBytes(), CHARSET);
                        Epic epic = GsonConverter.getGson().fromJson(body, Epic.class);

                        try {
                            taskManager.getEpicById(epic.getId());
                            taskManager.updateEpic(epic);
                            sendResponse(exchange, GsonConverter
                                    .getGson().toJson(taskManager.getEpicById(epic.getId())), POST_CODE);
                        } catch (NotFoundException e) {
                            taskManager.createEpic(epic);
                            sendResponse(exchange, GsonConverter
                                    .getGson().toJson(taskManager.getEpicById(epic.getId())), POST_CODE);
                        }
                    }

                    case DELETE -> {
                        int id = getPathId(pathEndpoint[2]);
                        taskManager.removeByEpicId(id);
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
        System.out.println("Конец обработки эпика " + dateTime);
    }
}
