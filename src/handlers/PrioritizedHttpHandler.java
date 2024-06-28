package handlers;

import com.sun.net.httpserver.HttpExchange;
import service.TaskManager;
import util.GsonConverter;

import java.io.IOException;

import static model.responsecode.ResponseCode.BAD_REQUEST_ERROR;
import static model.responsecode.ResponseCode.GET_CODE;

public class PrioritizedHttpHandler extends BaseHttpHandler {

    public PrioritizedHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try (exchange) {
            try {
                if (exchange.getRequestMethod().equals("GET")) {
                    sendResponse(exchange, GsonConverter.getGson().toJson(taskManager.getPrioritizedTasks()), GET_CODE);
                } else {
                    sendResponse(exchange, "Ошибка обработки запроса", BAD_REQUEST_ERROR);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
