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
public interface ChildDao {
    @Query("SELECT * FROM child")
    Single<List<Child>> getAll();

    @Query("SELECT * FROM child WHERE uid = :userid")
    Single<Child> get(int userid);

    @Query("SELECT * FROM child WHERE uid IN (:userIds)")
    Single<List<Child>> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM Child WHERE name LIKE :name")
    Single<List<Child>> findByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(Child... children);

    @Delete
    Completable delete(Child child);

    @Update
    Completable update(Child child);
}
