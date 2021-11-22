package com.cmpt276.parentapp.model;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

/**
 * Cross reference object to represent many to many relationship between Task and Child tables
 *
 * It has the childId, taskId and order the child holds for the task.
 */
@Entity(primaryKeys = {"taskId", "childId"},
        foreignKeys = {
                @ForeignKey(entity = Child.class, parentColumns = "childId", childColumns = "childId", onDelete = CASCADE),
                @ForeignKey(entity = Task.class, parentColumns = "taskId", childColumns = "taskId", onDelete = CASCADE)
        }
)
public class ChildTaskCrossRef {
    private final int taskId;
    private final int childId;
    private int order;

    public ChildTaskCrossRef(int taskId, int childId, int order) {
        this.taskId = taskId;
        this.childId = childId;
        this.order = order;
    }

    public int getTaskId() {

        return taskId;
    }

    public int getChildId() {

        return childId;
    }

    public int getOrder() {

        return order;
    }

    public void setOrder(int order) {

        this.order = order;
    }
}
