package model;

public class SubTask extends Task {
    private  Epic epic;

    public SubTask(String taskName, String description, Status status, Epic epic) {
        super(taskName, description, status);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }
}
