package com.naijaplanet.magosla.android.moviesplanet.data.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;


@Dao
public interface FavoriteMoviesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addMovies(FavoriteMovie... movies);

    @Query("DELETE FROM favorite_movies WHERE id= :movieId")
    void deleteMovieById(int movieId);

    @Query("SELECT * FROM favorite_movies")
    LiveData<List<FavoriteMovie>> findAll();

    @SuppressWarnings("unused")
    @Query("SELECT * FROM favorite_movies LIMIT :offset, :length")
    LiveData<List<FavoriteMovie>> findWithLimits(int offset, int length);

    @Query("SELECT id FROM favorite_movies WHERE id = :movieId LIMIT 1")
     LiveData<FavoriteId> isFavorite(int movieId);
}
