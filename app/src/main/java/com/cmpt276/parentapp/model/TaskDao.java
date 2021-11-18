package com.cmpt276.parentapp.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface TaskDao {

    @Query("SELECT IFNULL(MAX(`order`) + 1, 0) FROM ChildTaskCrossRef where taskId = :taskId")
    Single<Integer> getNextOrder(int taskId);

    @Query("UPDATE ChildTaskCrossRef SET `order` = `order` - 1 " +
            "WHERE taskId = :taskId and `order` > :minOrder")
    Completable decrementOrder(int taskId, int minOrder);

    @Query("SELECT * FROM Task WHERE uid = :taskId")
    Single<TaskWithChildren> getTaskWithChildren(int taskId);

    @Query("SELECT * FROM task")
    Single<List<Task>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Task... tasks);

    @Update
    Completable update(Task... tasks);

    @Delete
    Completable delete(Task... tasks);
}
