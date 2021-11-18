package com.cmpt276.parentapp.model;

import androidx.room.Delete;
import androidx.room.Insert;

import io.reactivex.rxjava3.core.Completable;

public interface ChildTaskCrossRefDao {

    @Insert
    Completable insert(ChildTaskCrossRef... crossRef);


    @Delete
    Completable Delete(ChildTaskCrossRef... crossRef);
}
