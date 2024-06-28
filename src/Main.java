import model.dto.Epic;
import model.dto.Subtask;
import model.dto.Task;
import model.enums.Status;
import service.Managers;
import service.TaskManager;
import service.infilemanager.FileBackedTaskManager;

import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = Managers.getDefault();
        Path path = taskManager.getPath();
        taskManager = FileBackedTaskManager.loadFromFile(path);

        Epic preEpic;
        Subtask taskManagerSubtask;
        int day = 14;
        int hour = 10;

        if (taskManager.getAllTasks().isEmpty()) {
            for (int i = 0; i < 10; i++) {

                Task taskManagerTask = taskManager.createTask(new Task("Новая задача" + i,
                        "Описание" + i, Status.NEW));

                taskManager.getTaskById(taskManagerTask.getId()).setStartTime(LocalDateTime.of(2024,
                        6, 13, 10 + i, 0));
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
                    taskManager.getSubtaskById(subtask.getId()).setStartTime(LocalDateTime.of(2024,
                            6, day + i, hour + j, 0));
                    taskManager.updateSubtask(subtask);
                }
                hour++;

            }
        }


        preEpic = taskManager.createEpic(new Epic("Новый эпик" + 0,
                "Описание" + 0, Status.NEW));

        taskManagerSubtask = taskManager.createSubtask(new Subtask("Новая подзадача" + 0 + 0,
                "Описание" + 0 + 0, Status.NEW, preEpic.getId()));

        taskManager.getSubtaskById(taskManagerSubtask.getId()).setStartTime(LocalDateTime.of(2024,
                6, 14 + 11, 10, 0));
        taskManager.getSubtaskById(taskManagerSubtask.getId()).setDuration(Duration.ofMinutes(11));
        taskManager.updateSubtask(taskManagerSubtask);
        taskManager.updateEpic(preEpic);

        Task taskManagerTask = taskManager.createTask(new Task("Новая задача" + 0,
                "Описание" + 0, Status.NEW));

        taskManager.getTaskById(taskManagerTask.getId()).setStartTime(LocalDateTime.of(2024,
                6, 13, 10 + 11, 0));
        taskManager.getTaskById(taskManagerTask.getId()).setDuration(Duration.ofMinutes(15));
        taskManager.updateTask(taskManagerTask);

        taskManager.printTasks();
        taskManager.printEpics();
        taskManager.printSubtasks();


        taskManager.removeTasks();
        taskManager.removeEpics();
        taskManager.removeSubtasks();

        System.out.println();

        Task task1 = taskManager.createTask(new Task("Новая задача",
                "Описание", Status.NEW));
        Task task2 = taskManager.createTask(new Task("Новая задача2",
                "Описание новой задачи", Status.IN_PROGRESS));

        Epic epic = taskManager.createEpic(new Epic("Новый эпик",
                "Описание нового эпика", Status.NEW));
        Subtask subTask = taskManager.createSubtask(new Subtask("Новая подзадача",
                "Описание подазадачи", Status.DONE, epic.getId()));
        Subtask subTask2 = taskManager.createSubtask(new Subtask("Новая подзадача2",
                "Описание подазадачи2", Status.NEW, epic.getId()));

        Epic epic2 = taskManager.createEpic(new Epic("Новый эпик2",
                "Описание нового эпика", Status.NEW));
        Subtask subTask3 = taskManager.createSubtask(new Subtask("Новая подзадача3",
                "Описание подазадачи", Status.IN_PROGRESS, epic2.getId()));

        System.out.println(taskManager.getAllEpics() + "\n" + taskManager.getAllTasks() + "\n" +
                taskManager.getAllSubtasks());
        System.out.println();

        task1.setStatus(Status.DONE);
        task2.setStatus(Status.DONE);
        subTask.setStatus(Status.NEW);
        taskManager.updateSubtask(subTask);
        subTask3.setStatus(Status.DONE);
        taskManager.updateSubtask(subTask3);
        System.out.println(taskManager.getAllEpics() + "\n" + taskManager.getAllTasks() + "\n" +
                taskManager.getAllSubtasks());

        System.out.println();

        taskManager.removeByTaskId(task1.getId());

        System.out.println("удаление эпика" + taskManager.removeByEpicId(epic.getId()));

        System.out.println(taskManager.getAllEpics() + "\n" + taskManager.getAllTasks() + "\n" +
                taskManager.getAllSubtasks() + "\n");

        taskManager.printHistory();
        System.out.println();

        Task task3 = taskManager.createTask(new Task("Новая задача3",
                "Описание новой задачи3", Status.NEW));
        Task task4 = taskManager.createTask(new Task("Новая задача4",
                "Описание новой задачи4", Status.IN_PROGRESS));
        Epic epicSave = taskManager.createEpic(new Epic("Новый эпик история",
                "Описание нового эпика", Status.NEW));
        Subtask subTaskSave1 = taskManager.createSubtask(new Subtask("Новая подзадача история",
                "Описание подазадачи", Status.DONE, epicSave.getId()));
        Subtask subTaskSave2 = taskManager.createSubtask(new Subtask("Новая подзадача история2",
                "Описание подазадачи2", Status.NEW, epicSave.getId()));
        Subtask subTaskSave3 = taskManager.createSubtask(new Subtask("Новая подзадача история3",
                "Описание подазадачи3", Status.NEW, epicSave.getId()));

        taskManager.getTaskById(task3.getId());
        taskManager.getTaskById(task4.getId());
        taskManager.getEpicById(epicSave.getId());
        taskManager.getSubtaskById(subTaskSave1.getId());
        taskManager.getSubtaskById(subTaskSave2.getId());
        taskManager.getSubtaskById(subTaskSave3.getId());

        taskManager.printHistory();

    }
}
