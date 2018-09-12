package com.naijaplanet.magosla.android.moviesplanet.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.naijaplanet.magosla.android.moviesplanet.data.VideosResult;
import com.naijaplanet.magosla.android.moviesplanet.util.MovieDbUtil;

public class VideosLoader extends AppLoader<VideosResult> {
    private static final String BUNDLE_MOVIE_ID_KEY = "movie_id_key";

    public VideosLoader(@NonNull Context context, @NonNull LoaderManager loaderManager, @NonNull AppLoaderCallback<VideosResult> callback) {
        super(context, loaderManager, callback);
    }

    @Override
    Loader<VideosResult> createLoaderTask(Context context, int loaderId, @NonNull AppLoaderCallback<VideosResult> callback, @Nullable Bundle args) {
        return new VideosTask(context, callback, args);
    }

    /**
     * Initiate a loading of items
     *
     * @param movieId the {@link com.naijaplanet.magosla.android.moviesplanet.models.Movie} id
     */
    public void load(int movieId) {
        Bundle arg = new Bundle();

        arg.putInt(BUNDLE_MOVIE_ID_KEY, movieId);
        int loaderId = ("V" + movieId).hashCode();

        super.load(loaderId, arg);
    }

    private static class VideosTask extends AppAsyncTaskLoader<VideosResult> {

        VideosTask(Context context, AppLoaderCallback<VideosResult> loaderCallback, @Nullable Bundle args) {
            super(context, loaderCallback, args);
        }

        @Nullable
        @Override
        public VideosResult loadInBackground() {
            Bundle args = getArgs();
            int movieId = args.getInt(BUNDLE_MOVIE_ID_KEY, 0);

            return MovieDbUtil.getVideosRecord(getContext(), movieId, new MovieDbUtil.Callback() {
                @Override
                public void error(String msg) {
                    getLoaderCallback().onError(msg);
                }
            });
        }
    }
}
