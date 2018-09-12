package com.naijaplanet.magosla.android.moviesplanet.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;


@SuppressWarnings({"WeakerAccess", "unused", "unchecked"})
abstract public class AppLoader<D>  implements LoaderManager.LoaderCallbacks<D> {
    private final Context mContext;
    private final LoaderManager mLoaderManager;
    private final AppLoaderCallback mCallback;


    public AppLoader(@NonNull Context context, @NonNull LoaderManager loaderManager, @NonNull AppLoaderCallback<D> callback) {
        mContext = context;
        mLoaderManager = loaderManager;
        mCallback = callback;
    }

    /**
     * Load is called to run the loader
     * @param loaderId the loader ID
     * @param arg the arguments bundle
     */
    public void load(int loaderId, @Nullable Bundle arg) {

        //Loader<String> moviesResult = mLoaderManager.getLoader(MOVIE_LIST_LOADER);

        mLoaderManager.initLoader(loaderId, arg, this);
    }


    public interface AppLoaderCallback<D> {
        /**
         * when the data is about to the loaded
         */
        void loadingItems();

        /**
         * When the loading of the items is completed
         *
         * @param result the {@link D}
         */
        void onLoadFinished(D result);

        /**
         * When an error is detected in the loading process
         *
         * @param errorMessage the error message
         */
        void onError(String errorMessage);
    }

    /**
     * Creates an instance of the loader for example {@link AsyncTaskLoader<D>}
     * @param context the application {@link Context}
     * @param loaderId the loader id
     * @param callback the {@link AppLoaderCallback <D>}
     * @param args the argument {@link Bundle}
     * @return the loader
     */
    abstract Loader<D> createLoaderTask(Context context, int loaderId, @NonNull AppLoaderCallback<D> callback, @Nullable Bundle args);

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        // return a new loader task
        return createLoaderTask(mContext, id, mCallback, args);

    }

    @Override
    public void onLoadFinished(@NonNull Loader<D> loader, D data) {
        if (data != null) {
            mCallback.onLoadFinished(data);
        }else{
            // destroy the loader so it can be retried on request
            mLoaderManager.destroyLoader(loader.getId());
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<D> loader) {

    }
}
