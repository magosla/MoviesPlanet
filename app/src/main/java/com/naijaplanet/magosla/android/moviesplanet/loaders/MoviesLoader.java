package com.naijaplanet.magosla.android.moviesplanet.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.naijaplanet.magosla.android.moviesplanet.data.MoviesResult;
import com.naijaplanet.magosla.android.moviesplanet.util.MovieDbUtil;

@SuppressWarnings("ALL")
public class MoviesLoader implements LoaderManager.LoaderCallbacks<MoviesResult> {
    private static final String BUNDLE_FILTER_KEY = "filter";
    private static final String BUNDLE_PAGE_KEY = "page";

    private final Context mContext;
    private final LoaderManager mLoaderManager;
    private final MovieLoaderCallback mCallback;

    public MoviesLoader(@NonNull Context context, @NonNull LoaderManager loaderManager, @NonNull MovieLoaderCallback callback) {
        mContext = context;
        mLoaderManager = loaderManager;
        mCallback = callback;

    }

    public void load(int page, String filter) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_FILTER_KEY, filter);
        bundle.putInt(BUNDLE_PAGE_KEY, page);

        //Loader<String> moviesResult = mLoaderManager.getLoader(MOVIE_LIST_LOADER);

        mLoaderManager.initLoader(getLoaderKey(page, filter), bundle, this);
    }

    /**
     * Get a unique key for the loader request
     *
     * @param page   the page number
     * @param filter the filter used for the search
     * @return the generated key
     */
    private int getLoaderKey(int page, String filter) {
        String keyString = filter + page;
        return keyString.hashCode();
    }

    @Override
    public void onLoadFinished(@NonNull Loader<MoviesResult> loader, MoviesResult data) {

        if (data != null) {
            mCallback.onLoadFinished(data);
        }else{
            // destroy the loader so it can be retried on request
            mLoaderManager.destroyLoader(loader.getId());
        }

    }


    @NonNull
    @Override
    public Loader<MoviesResult> onCreateLoader(int id, @Nullable final Bundle args) {
        return new MovieListLoader(mContext, args, mCallback);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<MoviesResult> loader) {

    }

    public interface MovieLoaderCallback {
        /**
         * when the movie list is about to the loaded
         */
        void loadingItems();

        /**
         * When the loading of the movie list is completed
         *
         * @param moviesResult the {@link MoviesResult}
         */
        void onLoadFinished(MoviesResult moviesResult);

        /**
         * When an error is detected in the loading process
         *
         * @param errorMessage the error message
         */
        void onError(String errorMessage);
    }

    /**
     * An AsyncTaskLoader implementation to load Movie list
     */
    private static class MovieListLoader extends AsyncTaskLoader<MoviesResult> {
        private final Bundle mArgs;
        private final MovieLoaderCallback mMovieLoaderCallback;

        MovieListLoader(Context context, @Nullable final Bundle args, MovieLoaderCallback movieLoaderCallback) {
            super(context);
            mArgs = args;
            mMovieLoaderCallback = movieLoaderCallback;
            onContentChanged();
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            if (mArgs == null) {
                return;
            }
            if (takeContentChanged()) {
                forceLoad();
                mMovieLoaderCallback.loadingItems();
            }
        }

        @Nullable
        @Override
        public MoviesResult loadInBackground() {
            int page = mArgs.getInt(BUNDLE_PAGE_KEY, 1);
            String filter = mArgs.getString(BUNDLE_FILTER_KEY, null);

            return MovieDbUtil.getMoviesRecord(getContext(),page, filter, new MovieDbUtil.Callback() {
                @Override
                public void error(String msg) {
                    if (mMovieLoaderCallback != null)
                        mMovieLoaderCallback.onError(msg);
                }
            });
        }

    }
}
