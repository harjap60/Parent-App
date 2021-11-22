package com.cmpt276.parentapp.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity to represent Tasks in the room database.
 *
 * This just has the name of the task and a unique id
 */
@Entity
public class Task {

    @PrimaryKey(autoGenerate = true)
    private int taskId;

    private String name;

    public Task(String name) {

        this.setName(name);
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        this.name = name;
    }

    public int getTaskId() {

        return taskId;
    }

    public void setTaskId(int taskId) {

        this.taskId = taskId;
    }

    @NonNull
    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                '}';
    }
}
