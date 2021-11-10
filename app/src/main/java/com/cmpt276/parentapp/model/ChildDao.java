package com.cmpt276.parentapp.model;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

public interface ChildDao {
    @Query("SELECT * FROM child")
    List<Child> getAll();

    @Query("SELECT * FROM child WHERE uid IN (:userIds)")
    List<Child> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM Child WHERE name LIKE :name")
    List<Child> findByName(String name);

    @Insert
    void insertAll(Child... users);

    @Delete
    void delete(Child user);
}
