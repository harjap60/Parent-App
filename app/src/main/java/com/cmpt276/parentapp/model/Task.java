package com.cmpt276.parentapp.model;

import androidx.annotation.NonNull;

public class Task {
    private String name;

    public Task(String name) {
        this.setName(name);
    }

    @NonNull
    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name == null || name.isEmpty()){
            throw new IllegalArgumentException("Name cannot be empty");
        }

        this.name = name;
    }
}
