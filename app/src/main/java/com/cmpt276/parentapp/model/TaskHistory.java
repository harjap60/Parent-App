package com.cmpt276.parentapp.model;

import static androidx.room.ForeignKey.CASCADE;
import static androidx.room.ForeignKey.SET_NULL;

import androidx.annotation.Nullable;
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

    @Nullable
    private final Long childId;

    private final long taskId;
    private final LocalDateTime date;

    @PrimaryKey(autoGenerate = true)
    private long taskHistoryId;

    public TaskHistory(@Nullable Long childId, long taskId, LocalDateTime date) {
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

    @Nullable
    public Long getChildId() {
        return childId;
    }

    public long getTaskId() {
        return taskId;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
