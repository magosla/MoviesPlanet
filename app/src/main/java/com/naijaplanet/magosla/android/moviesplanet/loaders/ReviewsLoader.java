package com.naijaplanet.magosla.android.moviesplanet.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.naijaplanet.magosla.android.moviesplanet.data.ReviewsResult;
import com.naijaplanet.magosla.android.moviesplanet.util.MovieDbUtil;

public class ReviewsLoader extends AppLoader<ReviewsResult> {
    private static final String BUNDLE_PAGE_KEY = "page_key";
    private static final String BUNDLE_MOVIE_ID_KEY = "movie_id_key";

    public ReviewsLoader(@NonNull Context context, @NonNull LoaderManager loaderManager, @NonNull AppLoaderCallback<ReviewsResult> callback) {
        super(context, loaderManager, callback);
    }

    @Override
    Loader<ReviewsResult> createLoaderTask(Context context, int loaderId, @NonNull AppLoaderCallback<ReviewsResult> callback, @Nullable Bundle args) {
           return new ReviewsTask(context, callback, args);
    }

    /**
     *  Initiate a loading of items
     *
     * @param movieId the {@link com.naijaplanet.magosla.android.moviesplanet.models.Movie} id
     * @param page the the page to load
     */
    public void load(int movieId, int page) {
        Bundle arg = new Bundle();

        arg.putInt(BUNDLE_MOVIE_ID_KEY, movieId);
        arg.putInt(BUNDLE_PAGE_KEY, page < 0 ? 1 : page);
        int loaderId  = ("R" + movieId + page).hashCode();

        super.load(loaderId, arg);
    }

    private static class ReviewsTask extends AppAsyncTaskLoader<ReviewsResult>{

        ReviewsTask(Context context, AppLoaderCallback<ReviewsResult> loaderCallback, @Nullable Bundle args) {
            super(context, loaderCallback, args);
        }

        @Nullable
        @Override
        public ReviewsResult loadInBackground() {
            Bundle args = getArgs();
            int movieId = args.getInt(BUNDLE_MOVIE_ID_KEY, 0);
            int page = args.getInt(BUNDLE_PAGE_KEY, 1);

            return MovieDbUtil.getReviewsRecord(getContext(),movieId ,page, new MovieDbUtil.Callback() {
                @Override
                public void error(String msg) {
                    getLoaderCallback().onError(msg);
                }
            });
        }
    }
}
