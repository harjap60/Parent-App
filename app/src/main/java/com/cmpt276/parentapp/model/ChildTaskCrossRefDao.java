package com.cmpt276.parentapp.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;

import io.reactivex.rxjava3.core.Completable;

/**
 * Database Access Object for ChildTaskCrossRef. This handles  insertion and deletion of the
 * relation between task and child objects.
 */
@Dao
public interface ChildTaskCrossRefDao {

    @Insert
    Completable insert(ChildTaskCrossRef... crossRef);


    @Delete
    Completable Delete(ChildTaskCrossRef... crossRef);
}
