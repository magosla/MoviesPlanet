package com.naijaplanet.magosla.android.moviesplanet.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.naijaplanet.magosla.android.moviesplanet.data.room.AppDatabase;
import com.naijaplanet.magosla.android.moviesplanet.data.room.FavoriteId;
import com.naijaplanet.magosla.android.moviesplanet.data.room.FavoriteMovie;
import com.naijaplanet.magosla.android.moviesplanet.models.Movie;

public class FavoriteViewModel extends AndroidViewModel {
    public FavoriteViewModel(@NonNull Application application) {
        super(application);
        mDb = AppDatabase.getDatabase(this.getApplication());
    }

    /**
     * Observer to know when a  movie if favorite
     * @param movieId the movie id
     * @return {@link LiveData < FavoriteId >}
     */
    public LiveData<FavoriteId> isFavorite(int movieId) {
        return mDb.favoriteMoviesDao().isFavorite(movieId);
    }

    /**
     * Removes a movie from favorite
     * @param movieId the id of the movie
     */
    public void deleteFavorite(int movieId) {
        new DeleteAsyncTask(mDb).execute(movieId);
    }

    private final AppDatabase mDb;

    /**
     * Add a movie to favorite
     * @param movie the {@link Movie} instance
     */
    public void addToFavorite(Movie movie) {
        FavoriteMovie fm = new FavoriteMovie();
        fm.setId(movie.getId());
        fm.setOverview(movie.getOverview());
        fm.setReleaseDate(movie.getReleaseDate());
        fm.setOriginalTitle(movie.getOriginalTitle());
        fm.setTitle(movie.getTitle());
        fm.setBackdropPath(movie.getBackdropPath());
        fm.setPosterPath(movie.getPosterPath());
        fm.setPopularity(movie.getPopularity());
        fm.setVoteCount(movie.getVoteCount());
        fm.setVoteAverage(movie.getVoteAverage());
        fm.setVideo(movie.getVideo());
        fm.setAdult(movie.getAdult());
        new AddAsyncTask(mDb).execute(fm);
    }


    /**
     * AsyncTask class to add movies to favorite
     */
    private static class AddAsyncTask extends AsyncTask<FavoriteMovie, Void, Void> {
        private final AppDatabase mDb;

        AddAsyncTask(AppDatabase appDatabase) {
            mDb = appDatabase;
        }

        @Override
        protected Void doInBackground(FavoriteMovie... favoriteMovies) {
            for (FavoriteMovie fm : favoriteMovies) {
                mDb.favoriteMoviesDao().addMovies(fm);
            }
            return null;
        }
    }

    /**
     * AsyncTask class to remove a movie from Favorite
     */
    private static class DeleteAsyncTask extends AsyncTask<Integer, Void, Void> {
        private final AppDatabase mDb;

        DeleteAsyncTask(AppDatabase appDatabase) {
            mDb = appDatabase;
        }

        @Override
        protected Void doInBackground(Integer... movieIds) {
            for (int movieId : movieIds) {
                mDb.favoriteMoviesDao().deleteMovieById(movieId);
            }
            return null;
        }
    }
}
