package main.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

/**
 * This object represents a task with an associated list of children from the room database.
 */
public class TaskWithHistory {
    @Embedded
    public Task task;

    @Relation(
            entity = TaskHistory.class,
            parentColumn = "taskId",
            entityColumn = "taskId"
    )
    public List<TaskHistoryWithChild> history;
}
