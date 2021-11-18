package com.cmpt276.parentapp.model;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class TaskWithChildren {
    @Embedded
    public Task task;

    @Relation(
            parentColumn = "playlistId",
            entityColumn = "songId",
            associateBy = @Junction(ChildTaskCrossRef.class)
    )
    public List<Child> children;
}
