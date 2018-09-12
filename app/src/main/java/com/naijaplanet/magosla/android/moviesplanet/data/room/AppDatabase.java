package com.naijaplanet.magosla.android.moviesplanet.data.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.naijaplanet.magosla.android.moviesplanet.Config;

@Database(entities = {FavoriteMovie.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public static AppDatabase getDatabase(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, Config.DATABASE_NAME).build();
        }
        return instance;
    }

    public abstract FavoriteMoviesDao favoriteMoviesDao();
}
