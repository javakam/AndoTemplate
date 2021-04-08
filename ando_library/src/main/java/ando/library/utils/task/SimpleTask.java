package ando.library.utils.task;

public abstract class SimpleTask implements Runnable {
    long SEQ; // the identity for task

    public String taskName;

    public Priority priority;

    public SimpleTask() {
        priority = Priority.NORMAL;
    }

    public SimpleTask(Priority priority) {
        this.priority = priority == null ? Priority.NORMAL : priority;
    }
}
