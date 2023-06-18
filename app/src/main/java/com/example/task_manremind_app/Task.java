package com.example.task_manremind_app;

public class Task {
    private int id;
    private String title;
    private String description;
    private String dueDate;
    private int priority;

    public Task(int id, String title, String description, String dueDate, int priority) {
        this.id = this.id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public int getPriority() {
        return priority;
    }

    public void setId(long id) {
    }
}
