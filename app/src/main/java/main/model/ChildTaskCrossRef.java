package main.model;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

/**
 * Cross reference object to represent many to many relationship between Task and Child tables
 * <p>
 * It has the childId, taskId and order the child holds for the task.
 */
@Entity(primaryKeys = {"taskId", "childId"},
        foreignKeys = {
                @ForeignKey(entity = Child.class, parentColumns = "childId", childColumns = "childId", onDelete = CASCADE),
                @ForeignKey(entity = Task.class, parentColumns = "taskId", childColumns = "taskId", onDelete = CASCADE)
        }
)
public class ChildTaskCrossRef {

    private final long taskId;

    @ColumnInfo(index = true)
    private final long childId;

    private long order;

    public ChildTaskCrossRef(long taskId, long childId, long order) {
        this.taskId = taskId;
        this.childId = childId;
        this.order = order;
    }

    public long getTaskId() {
        return taskId;
    }

    public long getChildId() {
        return childId;
    }

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
    }
}
