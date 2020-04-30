package de.fs.fintech.geogame.data;

public class Todo {
    private final long id;
    private final String body;
    private final int priority;

    public Todo(long id, String body, int priority) {
        this.id = id;
        this.body = body;
        this.priority = priority;
    }

    public Todo(String body, int priority) {
        this(-1L, body, priority);
    }

    public long getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public int getPriority() {
        return priority;
    }
}
