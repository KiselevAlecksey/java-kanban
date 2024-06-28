package handlers;

import com.sun.net.httpserver.HttpExchange;
import service.TaskManager;
import util.GsonConverter;

import java.io.IOException;

import static model.responsecode.ResponseCode.GET_CODE;
import static model.responsecode.ResponseCode.METHOD_NOT_ALLOWED;

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
                    sendResponse(exchange, "Метод нельзя применить к текущему ресурсу", METHOD_NOT_ALLOWED);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
