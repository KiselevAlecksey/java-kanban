import model.SubTask;
import service.TaskManager;
import model.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

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
                taskManager.getAllSubTasks());
        System.out.println();
    }
}
