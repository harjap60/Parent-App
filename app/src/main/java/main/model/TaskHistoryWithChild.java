package main.model;

import androidx.room.Embedded;
import androidx.room.Relation;

public class TaskHistoryWithChild {
    @Embedded
    public TaskHistory taskHistory;

    @Relation(entity = Child.class,
            parentColumn = "childId",
            entityColumn = "childId")
    public Child child;
}
