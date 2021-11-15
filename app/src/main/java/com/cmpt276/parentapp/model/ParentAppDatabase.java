package com.cmpt276.parentapp.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Database(
        entities = {Child.class, CoinFlip.class},
        views = {ChildCoinFlip.class},
        version = 10
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
