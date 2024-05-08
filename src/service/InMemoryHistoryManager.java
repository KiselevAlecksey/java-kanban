package service;

import model.Node;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager{
    private final LinkedListTasks<Task> linkedListTasks = new LinkedListTasks();
    private final Map<Integer, Node<Task>> history = new HashMap<>();


    @Override
    public Task add(Task task) {
        if (task == null) {
            return null;
        }

        Node<Task> node = history.get(task.getId());

        if (node != null) {
            linkedListTasks.removeNode(node);
        }

        linkedListTasks.linkLast(task);
        history.put(task.getId(), linkedListTasks.last);
        return new Task(task);
    }

    @Override
    public List<Task> getHistory() {
        return linkedListTasks.getTasks();
    }

    @Override
    public int remove(int id) {
        Node<Task> node = history.get(id);

        if (node != null) {
            linkedListTasks.removeNode(node);
        }
        history.remove(id);
        return id;
    }

    private static class LinkedListTasks<Task> {

        private int size = 0;
        private Node<Task> first;
        private Node<Task> last;

        public LinkedListTasks() {
        }

        public void linkLast(Task task) {

            final Node<Task> l = last;
            final Node<Task> newNode = new Node<>(l, task, null);
            last = newNode;
            if (l == null)
                first = newNode;
            else
                l.setNext(newNode);
            size++;
        }

        public List<Task> getTasks() {
            List<Task> list = new ArrayList<>();
            Node<Task> node = first;

            while(node != null) {
                list.add(node.getValue());
                node = node.getNext();
            }

            return list;
        }

        public Node<Task> removeNode(Node<Task> node) {
            Node<Task> next = node.getNext();
            Node<Task> prev = node.getPrev();

            if ((next == null) && (prev == null)) {
                first = null;
                last = null;
                size--;
                return node;
            }

            if ((next != null) && (prev != null)) {
                next.setPrev(prev);
                prev.setNext(next);
            }

            if (prev == null) {
                next.setPrev(null);
                first = next;
            }

            if (next == null) {
                prev.setNext(null);
                last = prev;
            }

            size--;
            return node;
        }

    }

}
