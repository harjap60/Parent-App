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

/**
 * This is a Database Access Object that handles interactions with the child table in the
 * room database.
 */
@Dao
public interface ChildDao {
    @Query("SELECT * FROM child")
    Single<List<Child>> getAll();

    @Query("SELECT * FROM child WHERE childId = :childId")
    Single<Child> get(int childId);

    @Query("SELECT * FROM child ORDER BY coinFlipOrder LIMIT 1")
    Single<Child> getChildForNextFlip();

    @Query("UPDATE child set coinFlipOrder = coinFlipOrder - 1 WHERE coinFlipOrder > :min")
    Completable decrementCoinFlipOrder(int min);

    @Query("SELECT IFNULL(MAX(coinFlipOrder) + 1, 0) FROM child")
    Single<Integer> getNextCoinFlipOrder();

    @Query("SELECT * FROM child ORDER BY coinFlipOrder")
    Single<List<Child>> getChildrenForFlip();

    @Query("SELECT COUNT(*) > 0 FROM Child")
    Single<Boolean> hasChildren();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Child... children);

    @Delete
    Completable delete(Child... children);

    @Update
    Completable update(Child... children);
}
