package com.cmpt276.parentapp.model;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

/**
 * This object represents a task with an associated list of children from the room database.
 */
public class TaskWithChildren {
    @Embedded
    public Task task;

    @Relation(
            parentColumn = "taskId",
            entityColumn = "childId",
            associateBy = @Junction(ChildTaskCrossRef.class)
    )
    public List<Child> children;
}
