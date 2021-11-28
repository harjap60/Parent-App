package com.cmpt276.parentapp.model;

import static androidx.room.ForeignKey.CASCADE;
import static androidx.room.ForeignKey.SET_NULL;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

@Entity(
        foreignKeys = {
                @ForeignKey(entity = Child.class, parentColumns = "childId", childColumns = "childId", onDelete = SET_NULL),
                @ForeignKey(entity = Task.class, parentColumns = "taskId", childColumns = "taskId", onDelete = CASCADE),
        }
)
public class TaskHistory {

    private final int childId;
    private final int taskId;
    private final LocalDateTime date;
    @PrimaryKey(autoGenerate = true)
    private long taskHistoryId;

    public TaskHistory(int childId, int taskId, LocalDateTime date) {
        this.childId = childId;
        this.taskId = taskId;
        this.date = date;
    }

    public long getTaskHistoryId() {
        return taskHistoryId;
    }

    public void setTaskHistoryId(long taskHistoryId) {
        this.taskHistoryId = taskHistoryId;
    }

    public int getChildId() {
        return childId;
    }

    public int getTaskId() {
        return taskId;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
