package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Эпик")
class EpicTest {

    Epic epic;
    Epic epicDifferent;
    SubTask subTask;
    TaskManager taskManager = Managers.getDefault();
    int subTaskCount = 0;

    @BeforeEach
    void init() {
        epic = taskManager.createEpic(new Epic("name", "description", Status.NEW));
        epicDifferent = taskManager.createEpic(new Epic("name", "description", Status.NEW));
        subTask = taskManager.createSubTask(new SubTask("подзадача",
                "описание", Status.DONE, epic.getId()));
    }

    @Test
    @DisplayName("должен совпадать со своей копией")
    void shouldEqualsWithCopy() {
        epicDifferent = epic;
        assertEqualsTask(epicDifferent, epic, "Задачи должны совпадать");
    }

    @Test
    @DisplayName("должен возвращать id списка задач")
    void shouldGetNotNull() {
        epic.addSubTaskId(subTask.getId());
        assertNotNull(epic);

        List<Integer> list = epic.getSubTasksId();
        for (Integer i : list) {
            epicDifferent.addSubTaskId(i);
        }

        Integer[] arrayExpected;
        Integer[] arrayActual;
        arrayExpected = epic.getSubTasksId().toArray(new Integer[0]);
        arrayActual = epicDifferent.getSubTasksId().toArray(new Integer[0]);

        assertArrayEquals(arrayExpected, arrayActual, "должны совпадать");
    }

    @Test
    @DisplayName("должен удалять подзадачу по id")
    void shouldRemoveSubtask() {
        epic.removeIdSubTask(subTask.getId());
        List<Integer> list = epic.getSubTasksId();
        assertEquals(subTaskCount, list.size(), "должны совпадать");
    }

    private static void assertEqualsTask(Epic expected, Epic actual, String message) {
        assertEquals(expected.getId(), actual.getId(), message + ", id");
        assertEquals(expected.getName(), actual.getName(), message + ", name");
        assertEquals(expected.getDescription(), actual.getDescription(), message + ", description");
        assertEquals(expected.getStatus(), actual.getStatus(), message + ", status");
    }

}