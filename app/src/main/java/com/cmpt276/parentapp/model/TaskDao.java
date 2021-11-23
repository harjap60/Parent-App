package com.cmpt276.parentapp.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

/**
 * Database Access Object to manage task objects in the database.
 *
 * This has all the queries and method required to handle interaction with the database.
 */
@Dao
public interface TaskDao {

    @Query("SELECT IFNULL(MAX(`order`) + 1, 0) FROM ChildTaskCrossRef WHERE taskId = :taskId")
    Single<Integer> getNextOrder(int taskId);

    @Query("UPDATE ChildTaskCrossRef SET `order` = `order` - 1 " +
            "WHERE taskId = :taskId and `order` > :minOrder")
    Completable decrementOrder(int taskId, int minOrder);

    @Query("SELECT " +
            "t.taskId as t_taskId," +
            "t.name as t_name, " +
            "c.*, " +
            "ref.`order` " +
            "FROM Task t " +
            "LEFT OUTER JOIN childtaskcrossref ref ON t.taskId = ref.taskId " +
            "LEFT OUTER JOIN child c ON c.childId = ref.childId WHERE t.taskId = :taskId " +
            "ORDER BY ref.`order` LIMIT 1")
    Single<TaskWithChild> getTaskWithNextChild(int taskId);

    @Query("SELECT " +
            "t.taskId as t_taskId," +
            "t.name as t_name, " +
            "c.*, " +
            "ref.`order`" +
            "FROM Task t " +
            "LEFT OUTER JOIN (SELECT taskId,  childId, min(`order`) as `order` from CHILDTASKCROSSREF group by taskId) ref on ref.taskId = t.taskId " +
            "LEFT OUTER JOIN child c ON c.childId = ref.childId ")
    Single<List<TaskWithChild>> getTasksWithFirstChild();

    @Query("SELECT * FROM task ORDER BY taskId")
    Single<List<Task>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insert(Task task);

    @Update
    Completable update(Task... tasks);

    @Delete
    Completable delete(Task... tasks);

    @Query("UPDATE ChildTaskCrossRef set `order` = :order WHERE taskId = :taskId and childId = :childId")
    Completable updateOrder(int taskId, int childId, int order);

    @Insert
    Completable insertRef(ChildTaskCrossRef... crossRef);

    @Insert
    Completable updateRef(ChildTaskCrossRef... crossRef);

    @Delete
    Completable DeleteRef(ChildTaskCrossRef... crossRef);
}
