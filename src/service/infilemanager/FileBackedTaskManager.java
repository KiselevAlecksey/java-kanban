package service.infilemanager;

import model.*;
import service.HistoryManager;
import service.TaskManager;
import service.inmemorymanager.InMemoryHistoryManager;
import service.inmemorymanager.InMemoryTaskManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final Path path;
    private int listSize = 0;

    public FileBackedTaskManager(HistoryManager historyManager, Path path) {
        super(historyManager);
        this.path = path;
    }

    public FileBackedTaskManager(Path path) {
        super(new InMemoryHistoryManager());
        this.path = path;
    }

    @Override
    public int generateId() {
        return super.generateId();
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    @Override
    public Task createTask(Task task) {
        super.createTask(task);

        save();
        return task;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeByTaskId(int taskId) {
        super.removeByTaskId(taskId);
        save();
    }

    @Override
    public void removeSubTasks() {
        super.removeSubTasks();
        save();
    }

    @Override
    public SubTask createSubTask(SubTask task) {
        super.createSubTask(task);

        save();
        return task;
    }

    @Override
    public SubTask updateSubTask(SubTask task) {
        super.updateSubTask(task);
        save();
        return task;
    }

    @Override
    public void removeBySubTaskId(int subTaskId) {
        super.removeBySubTaskId(subTaskId);
        save();
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    @Override
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);

        save();
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public Epic removeByEpicId(int epicId) {
        Epic epic = getEpicById(epicId);
        super.removeByEpicId(epicId);
        save();
        return epic;
    }

    @Override
    public void updateStatusEpic(Epic epic) {
        super.updateStatusEpic(epic);
        save();
    }

    public static FileBackedTaskManager loadFromFile(Path path) {
        FileBackedTaskManager manager = new FileBackedTaskManager(path);
        manager.loadFromFile();
        return manager;
    }

    private void save() {

        try (BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(path.toString(), false), StandardCharsets.UTF_8))) {

            listSize = tasks.size() + subTasks.size() + epics.size();
            List<Task> list = new ArrayList<>(listSize);

            bufferedWriter.write("id,type,name,status,description,epic\n");
            list.addAll(tasks.values());
            list.addAll(epics.values());
            list.addAll(subTasks.values());

            for (Task task : list) {
                bufferedWriter.write(toStringInFile(task) + "\n");
            }

        } catch (IOException exception) {
            throw new RuntimeException("Ошибка в файле: " + path.getFileName(), exception);
        }
    }

    private void loadFromFile() {
        List<Task> list = new ArrayList<>(listSize);
        String line = null;
        int id = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(path.toString()), StandardCharsets.UTF_8))) {

            while (reader.ready()) {
                line = reader.readLine();
                Task task = fromString(line);
                list.add(task);

                if (task != null && task.getId() > id) {
                    id = task.getId();
                }
            }

        } catch (IOException exception) {
            throw new RuntimeException("Ошибка в файле: " + path.getFileName(), exception);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.err.println("В строке: " + line + " - недостаточно данных " + e);
        }
        putInMap(list);
        counter = id;
    }

    private void putInMap(List<Task> list) {

        for (int i = 1; i < list.size(); i++) {
            TypeTask type;

            try {
                type = list.get(i).getType();
            } catch (NullPointerException e) {
                System.err.println("Невозможно прочитать строку");
                continue;
            }

            Task task = list.get(i);
            Integer id = list.get(i).getId();

            switch (type) {
                case TASK:
                    tasks.put(id, task);
                    break;

                case EPIC:
                    epics.put(id, (Epic) task);
                    break;

                case SUBTASK:
                    subTasks.put(id, (SubTask) task);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + type);
            }
        }
    }

    public String toStringInFile(Task task) {
        return task.getId() +
                "," + task.getType() +
                ',' + task.getName() +
                ',' + task.getStatus() +
                ',' + task.getDescription() +
                ',' + task.getEpicId() +
                ',';
    }

    private Task fromString(String value) {

        Task task = null;
        String[] array;

        try {
            array = value.split(",");
            Integer id = Integer.parseInt(array[0]);
            TypeTask type = TypeTask.valueOf(array[1]);
            String name = array[2];
            Status status = Status.valueOf(array[3]);
            String description = array[4];
            Integer epicId;

            if (array[5].equals("null")) {
                epicId = null;
            } else {
                epicId = Integer.parseInt(array[5]);
            }

            switch (type) {
                case TASK:
                    task = new Task(id, type, name, status, description, epicId);
                    break;

                case EPIC:
                    task = new Epic(id, type, name, status, description, epicId);
                    break;

                case SUBTASK:
                    task = new SubTask(id, type, name, status, description, epicId);
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + type);
            }

        } catch (NumberFormatException e) {
            System.err.println("Нет данных для преобразования: " + e);
        }
        return task;
    }

}
