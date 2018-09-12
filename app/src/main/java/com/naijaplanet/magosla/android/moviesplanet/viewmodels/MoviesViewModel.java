package com.naijaplanet.magosla.android.moviesplanet.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.naijaplanet.magosla.android.moviesplanet.R;
import com.naijaplanet.magosla.android.moviesplanet.data.MoviesResult;
import com.naijaplanet.magosla.android.moviesplanet.data.room.AppDatabase;
import com.naijaplanet.magosla.android.moviesplanet.data.room.FavoriteMovie;
import com.naijaplanet.magosla.android.moviesplanet.loaders.AppLoader;
import com.naijaplanet.magosla.android.moviesplanet.models.Movie;
import com.naijaplanet.magosla.android.moviesplanet.models.MoviesRecord;
import com.naijaplanet.magosla.android.moviesplanet.util.MovieDbUtil;

import java.util.Arrays;
import java.util.List;

public class MoviesViewModel extends AndroidViewModel implements AppLoader.AppLoaderCallback<MoviesResult> {

    @SuppressWarnings("FieldCanBeLocal")
    private final AppDatabase mDb;
    private final String mFavoriteFilterValue;
    private final LiveData<List<FavoriteMovie>> mFavoriteMovies;
    private final MoviesRecord mMoviesRecord = new MoviesRecord();
    private final MutableLiveData<MoviesResult> mMoviesResultLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mErrorLiveData = new MutableLiveData<>();
    private final MediatorLiveData<MoviesRecord> mMovieRecordLiveData = new MediatorLiveData<>();
    private final MutableLiveData<Boolean> mLoading = new MutableLiveData<>();
    private String mCurrentFilter;
    private int mCurrentPage;
    private boolean mIsLoading; // when data is currently being loaded
    private boolean mLastFetchHasError;


    public MoviesViewModel(Application application) {
        super(application);
        mDb = AppDatabase.getDatabase(this.getApplication());

        mFavoriteMovies = mDb.favoriteMoviesDao().findAll();
        mFavoriteFilterValue = getApplication().getString(R.string.pref_filter_favorite_value);
    }

    /**
     * If the last fetch has error
     *
     * @return return true if there was an error
     */
    public boolean hadFetchError() {
        return mLastFetchHasError;
    }

    /**
     * Checks if there is available {@link MoviesRecord}
     *
     * @return true if there is a record
     */
    public boolean hasRecord() {
        return !mMoviesRecord.isEmpty();
    }

    /**
     * Observer the mLoading state of data
     *
     * @return {@link LiveData<Boolean>}
     */
    public LiveData<Boolean> isLoading() {
        return mLoading;
    }

    /**
     * Use to observe the errors
     *
     * @return {@link LiveData<String>}
     */
    public LiveData<String> getErrors() {
        return mErrorLiveData;
    }

    /**
     * Get the movies record LiveData
     *
     * @return {@link LiveData<MoviesRecord>}
     */
    public LiveData<MoviesRecord> getMoviesRecord() {

        return mMovieRecordLiveData;
    }

    /**
     * reloads the record if there was error
     */
    public void reLoad() {
        loadMovies(mCurrentFilter, mCurrentPage, true);
    }

    /**
     * Load movies
     *
     * @param filter the movie filter
     * @param page   the page to fetch
     */
    public void loadMovies(String filter, int page) {
        loadMovies(filter, page, false);
    }

    /**
     * Load movies
     *
     * @param filter      the movie filter
     * @param page        the page to fetch
     * @param forceReload if to forcefully reload the content
     */
    @SuppressWarnings("WeakerAccess")
    public void loadMovies(String filter, int page, boolean forceReload) {
        // reset the record on filter change
        if (!filter.equals(mCurrentFilter)) {
            if (mIsLoading) { // if we are changing filter within an uncompleted request
                updateLoadingStatus(false);
            }
            mMoviesRecord.reset();
        }
        if (mFavoriteFilterValue.equals(filter)) {
            initializeFavoriteLiveData(page);
        } else {
            initializeMovieResultLiveData();
            // make http request on parameter change of when forced to do so
            if (!(filter.equals(mCurrentFilter) && mCurrentPage == page) || forceReload) {
                new FetchAsyncTask(getApplication(), filter, page, this).execute();
            }
        }
        mCurrentPage = page;
        mCurrentFilter = filter;
    }

    private void initializeMovieResultLiveData() {
        // ensure Room live data is not being observed
        if (mFavoriteFilterValue.equals(mCurrentFilter)) {
            mMovieRecordLiveData.removeSource(mFavoriteMovies);
        }

        // if it is already being a source, ignore
        if (TextUtils.isEmpty(mCurrentFilter) || mFavoriteFilterValue.equals(mCurrentFilter)) {
            mMovieRecordLiveData.addSource(mMoviesResultLiveData, new Observer<MoviesResult>() {
                @Override
                public void onChanged(@Nullable MoviesResult moviesResult) {
                    toMovieRecord(moviesResult, false);
                }
            });
        }
    }

    private void updateLoadingStatus(boolean loading) {
        mIsLoading = loading;
        mLoading.postValue(loading);
    }

    private void initializeFavoriteLiveData(final int page) {
        // ensure Room live data is not being observed
        if (!(TextUtils.isEmpty(mCurrentFilter) || mFavoriteFilterValue.equals(mCurrentFilter))) {
            mMovieRecordLiveData.removeSource(mMoviesResultLiveData);
        }
        // Add source only when it's not currently a source
        if (!mFavoriteFilterValue.equals(mCurrentFilter)) {
            mMovieRecordLiveData.addSource(mFavoriteMovies, new Observer<List<FavoriteMovie>>() {
                @Override
                public void onChanged(@Nullable List<FavoriteMovie> favoriteMovies) {
                    // indicate data is loading
                    updateLoadingStatus(true);

                    loadFavoriteMovies(favoriteMovies, page);
                }
            });
        }
    }

    private void loadFavoriteMovies(List<FavoriteMovie> favoriteMovies, int page) {
        MoviesResult moviesResult = new MoviesResult();
        if (favoriteMovies != null) {
            Movie[] movies = new Movie[favoriteMovies.size()];
            int counter = 0;
            for (FavoriteMovie fm : favoriteMovies) {
                Movie movie = new Movie();

                movie.setId(fm.getId());
                movie.setOverview(fm.getOverview());
                movie.setReleaseDate(fm.getReleaseDate());
                movie.setOriginalTitle(fm.getOriginalTitle());
                movie.setTitle(fm.getTitle());
                movie.setBackdropPath(fm.getBackdropPath());
                movie.setPosterPath(fm.getPosterPath());
                movie.setPopularity(fm.getPopularity());
                movie.setVoteCount(fm.getVoteCount());
                movie.setVoteAverage(fm.getVoteAverage());
                movie.setVideo(fm.getVideo());
                movie.setAdult(fm.getAdult());

                movies[counter++] = movie;
            }
            moviesResult.setResults(Arrays.asList(movies));
            moviesResult.setPage(page);
            moviesResult.setResultFilter(mFavoriteFilterValue);
        }
        toMovieRecord(moviesResult, true);
    }


    private void toMovieRecord(MoviesResult moviesResult, boolean reset) {
        if(reset){
            mMoviesRecord.reset();
        }
        if (moviesResult != null) {
            mMoviesRecord.addMovies(moviesResult);
        }
        // if there was result

        mMovieRecordLiveData.postValue(mMoviesRecord);

        // notify the observer of completed mLoading;
        updateLoadingStatus(false);
    }

    @Override
    public void loadingItems() {
        mLastFetchHasError = false;
        updateLoadingStatus(true);
    }

    @Override
    public void onLoadFinished(MoviesResult result) {
        mMoviesResultLiveData.postValue(result);
    }

    @Override
    public void onError(String errorMessage) {
        mLastFetchHasError = true;
        mErrorLiveData.postValue(errorMessage);
    }

    /**
     * AsyncTask class to fetch movie record over the web
     */
    private static class FetchAsyncTask extends AsyncTask<Void, Void, MoviesResult> {
        private final AppLoader.AppLoaderCallback<MoviesResult> mCallback;
        private final Application mContext;
        private final int mPage;
        private final String mFilter;

        FetchAsyncTask(final Application context, String filter, int page, final AppLoader.AppLoaderCallback<MoviesResult> callback) {
            mContext = context;
            mFilter = filter;
            mPage = page;
            mCallback = callback;
        }

        @Override
        protected void onPreExecute() {
            mCallback.loadingItems();
        }

        @Override
        protected void onPostExecute(MoviesResult moviesResult) {
            mCallback.onLoadFinished(moviesResult);
        }

        @Override
        protected MoviesResult doInBackground(Void... args) {
            return MovieDbUtil.getMoviesRecord(mContext, mPage, mFilter, new MovieDbUtil.Callback() {
                @Override
                public void error(String msg) {
                    if (mCallback != null)
                        mCallback.onError(msg);
                }
            });
        }
    }
}
