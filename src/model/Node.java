package model;

import model.dto.Task;

public final class Node<T> {
    Task value;
    Node<Task> next;
    Node<Task> prev;

    public Node(Node<Task> prev, Task value, Node<Task> next) {
        this.value = value;
        this.next = next;
        this.prev = prev;
    }

    public Task getValue() {
        return value;
    }

    public void setValue(Task value) {
        this.value = value;
    }

    public Node<Task> getNext() {
        return next;
    }

    public void setNext(Node<Task> next) {
        this.next = next;
    }

    public Node<Task> getPrev() {
        return prev;
    }

    public void setPrev(Node<Task> prev) {
        this.prev = prev;
    }
}

