package com.cmpt276.parentapp.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The main database class that is used by Room to create a concrete class that creates and manages
 * the app database.
 * <p>
 * This is a singleton as it is expensive to keep creating these database connection instances.
 * <p>
 * This class needs to have methods to return DAOs for each entity we have.
 */
@Database(
        entities = {
                Child.class,
                CoinFlip.class,
                Task.class,
                TaskHistory.class,
                ChildTaskCrossRef.class
        },
        version = 17
)
@TypeConverters({Converters.class})
public abstract class ParentAppDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "parent-app-database";
    private static ParentAppDatabase instance;

    public static ParentAppDatabase getInstance(Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(context,
                    ParentAppDatabase.class,
                    DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract ChildDao childDao();

    public abstract CoinFlipDao coinFlipDao();

    public abstract TaskDao taskDao();
}

class Converters {

    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    @TypeConverter
    public static LocalDateTime fromTimestamp(String value) {
        return value == null ? null : LocalDateTime.parse(value, DateTimeFormatter.ofPattern(PATTERN));
    }

    @TypeConverter
    public static String dateToTimestamp(LocalDateTime date) {
        return date == null ? null : date.format(DateTimeFormatter.ofPattern(PATTERN));
    }
}
