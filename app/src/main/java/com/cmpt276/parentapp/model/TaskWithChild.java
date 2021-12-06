package com.cmpt276.parentapp.model;

import androidx.room.Embedded;

/**
 * This object represents a task with an associated list of children from the room database.
 */
public class TaskWithChild {
    @Embedded(prefix = "t_")
    public Task task;

    @Embedded
    public Child child;

    public long order;
}
