package com.cmpt276.parentapp.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.io.ByteArrayOutputStream;
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
                ChildTaskCrossRef.class
        },
        version = 14
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

    /**
     * Copied from the URL below.
     *
     * Converts String to Bitmap
     * https://stackoverflow.com/questions/4989182/converting-java-bitmap-to-byte-array
     *
     * @param value The image encoded in base64
     * @return Bitmap The Bitmap image from base64 encoded image
     */
    @TypeConverter
    public static Bitmap fromString(String value) {
        byte[] decodedByte = Base64.decode(value, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    /**
     * Copied from the URL below.
     *
     * Converts Bitmap to String
     * https://stackoverflow.com/questions/4989182/converting-java-bitmap-to-byte-array
     * @param image The Bitmap image to be encoded to base64
     * @return String The base64 encoded image as a string
     */
    @TypeConverter
    public static String fromBitmap(Bitmap image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
        byte[] b = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
}
