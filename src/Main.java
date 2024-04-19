import model.SubTask;
import service.*;
import model.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = Managers.getDefault();

        Task task1 = taskManager.createTask(new Task("Новая задача",
                "Описание", Status.NEW));
        Task task2 = taskManager.createTask(new Task("Новая задача2",
                "Описание новой задачи", Status.IN_PROGRESS));

        Epic epic = taskManager.createEpic(new Epic("Новый эпик",
                "Описание нового эпика", Status.NEW));
        SubTask subTask = taskManager.createSubTask(new SubTask("Новая подзадача",
                "Описание подазадачи", Status.DONE, epic.getId()));
        SubTask subTask2 = taskManager.createSubTask(new SubTask("Новая подзадача2",
                "Описание подазадачи2", Status.NEW, epic.getId()));

        Epic epic2 = taskManager.createEpic(new Epic("Новый эпик2",
                "Описание нового эпика", Status.NEW));
        SubTask subTask3 = taskManager.createSubTask(new SubTask("Новая подзадача3",
                "Описание подазадачи", Status.IN_PROGRESS, epic2.getId()));

        System.out.println(taskManager.getAllEpics() + "\n" + taskManager.getAllTasks() + "\n" +
                taskManager.getAllSubTasks());
        System.out.println();

        task1.setStatus(Status.DONE);
        task2.setStatus(Status.DONE);
        subTask.setStatus(Status.NEW);
        taskManager.updateSubTask(subTask);
        subTask3.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask3);
        System.out.println(taskManager.getAllEpics() + "\n" + taskManager.getAllTasks() + "\n" +
                taskManager.getAllSubTasks());

        System.out.println();

        taskManager.removeByTaskId(task1.getId());
        taskManager.removeByEpicId(epic.getId());

        System.out.println(taskManager.getAllEpics() + "\n" + taskManager.getAllTasks() + "\n" +
                taskManager.getAllSubTasks() + "\n");

        taskManager.printHistory();
        System.out.println();

        Task task3 = taskManager.createTask(new Task("Новая задача3",
                "Описание новой задачи3", Status.NEW));
        Task task4 = taskManager.createTask(new Task("Новая задача4",
                "Описание новой задачи4", Status.IN_PROGRESS));
        Epic epicSave = taskManager.createEpic(new Epic("Новый эпик история",
                "Описание нового эпика", Status.NEW));
        SubTask subTaskSave1 = taskManager.createSubTask(new SubTask("Новая подзадача история",
                "Описание подазадачи", Status.DONE, epicSave.getId()));
        SubTask subTaskSave2 = taskManager.createSubTask(new SubTask("Новая подзадача история2",
                "Описание подазадачи2", Status.NEW, epicSave.getId()));
        SubTask subTaskSave3 = taskManager.createSubTask(new SubTask("Новая подзадача история3",
                "Описание подазадачи3", Status.NEW, epicSave.getId()));

        taskManager.getTaskById(task3.getId());
        taskManager.getTaskById(task4.getId());
        taskManager.getEpicById(epicSave.getId());
        taskManager.getSubTaskById(subTaskSave1.getId());
        taskManager.getSubTaskById(subTaskSave2.getId());
        taskManager.getSubTaskById(subTaskSave3.getId());

        System.out.println(taskManager.getHistory());
    }
}
