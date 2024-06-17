package service.inmemorymanager;

import org.junit.jupiter.api.DisplayName;
import service.Managers;
import service.TaskManagerTest;

@DisplayName("менеджер задач")
class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    public InMemoryTaskManager createTaskManager() {
        return (InMemoryTaskManager) Managers.getDefault();
    }

}