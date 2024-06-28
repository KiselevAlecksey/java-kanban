package server;

import com.sun.net.httpserver.HttpServer;
import handlers.*;
import model.dto.Epic;
import model.dto.Subtask;
import model.dto.Task;
import model.enums.Status;
import service.HistoryManager;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    TaskManager taskManager;
    HistoryManager historyManager;
    HttpServer httpServer;
    Epic preEpic;
    int year = 4;
    int month = 8;
    int day = 15;
    int hour = 16;
    int minutes = 23;

    public HttpTaskServer(TaskManager taskManager) {
        historyManager = Managers.getDefaultHistory();
        this.taskManager = taskManager;
    }

    public static void main(String[] args) {
        HttpTaskServer server = new HttpTaskServer(Managers.getDefault());
        server.createTasks();
        server.run();

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void run() {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks", new TaskHttpHandler(taskManager));
            httpServer.createContext("/subtasks", new SubtaskHttpHandler(taskManager));
            httpServer.createContext("/epics", new EpicHttpHandler(taskManager));
            httpServer.createContext("/history", new HistoryHttpHandler(taskManager));
            httpServer.createContext("/prioritized", new PrioritizedHttpHandler(taskManager));

            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }

    public void stop() {
        httpServer.stop(0);
    }

    private void createTasks() {
        if (taskManager.getAllTasks().isEmpty()) {
            for (int i = 0; i < 5; i++) {

                Task taskManagerTask = taskManager.createTask(new Task("Новая задача" + i,
                        "Описание" + i, Status.NEW));

                taskManager.getTaskById(taskManagerTask.getId()).setStartTime(LocalDateTime.of(year,
                        month, day, hour + i, minutes));
                taskManager.getTaskById(taskManagerTask.getId()).setDuration(Duration.ofMinutes(15));
            }
        }

        if (taskManager.getAllSubtasks().isEmpty()) {

            for (int i = 0; i < 5; i++) {
                preEpic = taskManager.createEpic(new Epic("Новый эпик" + i,
                        "Описание" + i, Status.NEW));

                for (int j = 0; j < 2; j++) {
                    Subtask subtask = taskManager.createSubtask(new Subtask("Новая подзадача" + i + j,
                            "Описание" + i + j, Status.NEW, preEpic.getId()));
                    taskManager.getSubtaskById(subtask.getId()).setStartTime(LocalDateTime.of(year + 1,
                            month, day + i, hour + j, minutes));
                    taskManager.updateSubtask(subtask);
                }
                hour++;
            }
        }
    }

}
