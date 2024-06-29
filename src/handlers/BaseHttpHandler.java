package handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.IntersectTimeException;
import exception.NotFoundException;
import model.enums.Endpoint;
import model.enums.HttpError;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static model.enums.Endpoint.*;
import static model.responsecode.ResponseCode.*;

public abstract class BaseHttpHandler implements HttpHandler {
    protected static final Charset CHARSET = StandardCharsets.UTF_8;
    protected final TaskManager taskManager;
    protected ZonedDateTime dateTime = ZonedDateTime
            .of(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), ZoneId.of("Europe/Moscow"));

    BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected int getPathId(String pathEndpoint) {
        Integer id = null;

        try {
            id = Integer.parseInt(pathEndpoint);
        } catch (NumberFormatException e) {
            System.err.println("Введены некорректные данные: " + pathEndpoint + e);
        }

        if (id == null) throw new NotFoundException("Неверный id: " + pathEndpoint);

        return id;
    }

    protected void sendResponse(HttpExchange httpExchange, String body, int rCode) throws IOException {
        byte[] response = body.getBytes(CHARSET);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(rCode, response.length);
        httpExchange.getResponseBody().write(response);
        printSuccessMessage(rCode);
    }

    public void sendDeleteCode(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(DELETE_CODE, -1);
        printSuccessMessage(DELETE_CODE);
    }

    public void sendException(HttpExchange httpExchange, Exception exception) {

        try {
            switch (exception) {
                case NotFoundException e ->
                        sendResponse(httpExchange, HttpError.HTTP_NOT_FOUND_ERROR.toString(), NOT_FOUND_ERROR);
                case IntersectTimeException e ->
                        sendResponse(httpExchange, HttpError.HTTP_INTERSECTION_ERROR.toString(), INTERSECTION_ERROR);
                case JsonSyntaxException e ->
                        sendResponse(httpExchange, HttpError.HTTP_BAD_REQUEST_ERROR.toString(), BAD_REQUEST_ERROR);
                default -> {
                    exception.printStackTrace(System.out);
                    sendResponse(httpExchange, HttpError.HTTP_INTERNAL_SERVER_ERROR.toString(), INTERNAL_SERVER_ERROR);
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }

    protected Endpoint getEndpoint(String requestMethod) {

        switch (requestMethod) {
            case "GET":
                return GET;
            case "POST":
                return POST;
            case "DELETE":
                return DELETE;
            default:
                return UNKNOWN;
        }
    }

    private void printSuccessMessage(int rCode) {
        if (rCode <= 299 && rCode >= GET_CODE) {
            System.out.println("Успех " + dateTime);
        } else {
            System.out.println("Неудача " + dateTime);
        }
    }
}
