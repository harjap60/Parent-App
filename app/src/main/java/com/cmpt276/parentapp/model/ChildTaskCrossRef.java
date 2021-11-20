package com.cmpt276.parentapp.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(primaryKeys = {"taskId", "childId"},
        foreignKeys = {
                @ForeignKey(entity = Child.class, parentColumns = "childId", childColumns = "childId"),
                @ForeignKey(entity = Task.class, parentColumns = "taskId", childColumns = "taskId")
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
