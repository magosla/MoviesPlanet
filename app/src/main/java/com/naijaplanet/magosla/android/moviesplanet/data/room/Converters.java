package com.naijaplanet.magosla.android.moviesplanet.data.room;

import android.arch.persistence.room.TypeConverter;

@SuppressWarnings("WeakerAccess")
public class Converters {
    @TypeConverter
    public static int boolToInt(boolean value){
        return value ? 1 : 0;
    }

    @TypeConverter
    public static boolean intToBoolean(int value){
        return value > 0;
    }
}
