package com.cmpt276.parentapp.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Child.class}, version = 1)
public abstract class ParentAppDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "parent-app-database";
    private static ParentAppDatabase instance;

    public static ParentAppDatabase getInstance(Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(context,
                    ParentAppDatabase.class,
                    DATABASE_NAME).build();
        }
        return instance;
    }

    public abstract ChildDao childDao();
}
