package com.example.smarthealthcare.database;

import androidx.room.TypeConverter;

public class Converters {
    @TypeConverter
    public static long fromTimestamp(String value) {
        return value == null ? 0 : Long.parseLong(value);
    }

    @TypeConverter
    public static String dateToTimestamp(long value) {
        return value == 0 ? null : String.valueOf(value);
    }
}
