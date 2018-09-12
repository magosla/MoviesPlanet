package com.naijaplanet.magosla.android.moviesplanet.util;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.naijaplanet.magosla.android.moviesplanet.Config;
import com.naijaplanet.magosla.android.moviesplanet.R;
import com.naijaplanet.magosla.android.moviesplanet.data.MoviesResult;
import com.naijaplanet.magosla.android.moviesplanet.data.ReviewsResult;
import com.naijaplanet.magosla.android.moviesplanet.data.VideosResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MovieDbUtil {
    private static OkHttpClient mHttpClient;

    /**
     * Get the photo url
     *
     * @param photoPath the photo path
     * @param width the width of the photo
     * @return the as {@link String} the photo url or an empty {@link String} if photoPath is empty
     */
    public static String getPhotoUrl(String photoPath, String width){
        if(TextUtils.isEmpty(photoPath)){
            return "";
        }
        Uri uri = Uri.parse(Config.MOVIEDB_IMAGE_BASE_URL).buildUpon()
                .appendPath(width).appendPath(photoPath.replace("/","")).build();

        return uri.toString();
    }

    /**
     * Get the OkHttpClient
     *
     * @return an instance of OkHttpClient
     */
    private static OkHttpClient getHttpClient() {
        // should be a singleton
        //OkHttpClient client = new OkHttpClient();
        if (mHttpClient == null) {
            mHttpClient = new OkHttpClient.Builder()
                    .addNetworkInterceptor(new StethoInterceptor())
                    .build();
        }
        return mHttpClient;
    }

    /**
     * The URL for Youtube video thumbnail
     *
     * @param key  the youtube video key
     * @return the url string
     */
    public static String getYoutubeThumbnailUrl(String key) {
        return String.format(Config.YOUTUBE_THUMBNAIL_URL_FORMAT, key);
    }


    /**
     * The URL for Youtube video URL
     *
     * @param key  the youtube video key
     * @return the url string
     */
    public static String getYoutubeVideoUrl(String key) {
        return String.format(Config.YOUTUBE_VIDEO_URL_FORMAT, key);
    }

    /**
     * The URL to use for the request
     *
     * @param page   the page number
     * @param filter the filter for the results
     * @return the url string
     */
    private static String getMoviesUrl(int page, String filter) {
        HttpUrl url = HttpUrl.parse(Config.MOVIEDB_BASE_URL);
        if(url != null) //noinspection DanglingJavadoc
        {
            HttpUrl.Builder urlBuilder = url.newBuilder();
            if (filter != null) {
                urlBuilder.addPathSegment(filter);
            }
            urlBuilder.addQueryParameter("page", String.valueOf(((page < 1) ? 1 : page)));

            return urlBuilder.build().toString();
        }
        return "";

    }

    /**
     * The URL for the  movies videos
     *
     * @param movieId   the id of the movie
     * @return the url string
     */
    private static String getMovieVideosUrl(int movieId) {
        HttpUrl url = HttpUrl.parse(Config.MOVIEDB_BASE_URL);
        if(url != null) //noinspection DanglingJavadoc
        {
            HttpUrl.Builder urlBuilder = url.newBuilder();
            urlBuilder.addPathSegments(String.format(Locale.getDefault(),Config.MOVIE_VIDEOS_URI_TEMPLATE,movieId));

            return urlBuilder.build().toString();
        }
        return "";
    }

    /**
     * The URL for movie reviews
     *
     * @param movieId   the id of the movie
     * @param page   the page number
     * @return the url string
     */
    private static String getMovieReviewsUrl(int movieId, int page) {
        HttpUrl url = HttpUrl.parse(Config.MOVIEDB_BASE_URL);
        if(url != null) //noinspection DanglingJavadoc
        {
            HttpUrl.Builder urlBuilder = url.newBuilder();
            urlBuilder.addPathSegments(String.format(Locale.getDefault(),Config.MOVIE_REVIEW_URI_TEMPLATE, movieId));
            urlBuilder.addQueryParameter("page", String.valueOf(((page < 1) ? 1 : page)));

            return urlBuilder.build().toString();
        }
        return "";

    }

    /**
     * Get the Movies record with the provided criteria
     *
     * @param context the application {@link Context}
     * @param page     the page number
     * @param filter   criteria to filter the result
     * @param callback the callback if an error was encountered
     * @return an instance of {{@link MoviesResult}}
     */
    public static MoviesResult getMoviesRecord(final Context context, int page, String filter, final Callback callback) {

        Request request = new Request.Builder()
                .url(getMoviesUrl(page, filter))
                .build();

        MoviesResult moviesResult = null;

        try {
            Response response = getHttpClient().newCall(request).execute();
            try {
                ResponseBody body = response.body();
                //noinspection StatementWithEmptyBody
                if (body != null) {
                    String responseData = body.string();
                    JSONObject moviesObject = new JSONObject(responseData);

                    if (moviesObject.has("results")) {
                        moviesResult = MoviesResult.fromJson(moviesObject);
                        // keep track of the filter used (if any) for this result
                        moviesResult.setResultFilter(filter);
                    }
                    // TODO what to do when the response has no results field
                } else {
                    // what to do when the response body is empty
                    performInternetCheck(callback, context);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                // TODO action to perform if there is a JSONException
            }
        } catch (IOException e) {
            e.printStackTrace();
            performInternetCheck(callback, context);

        }
        return moviesResult;
    }

    private static void performInternetCheck(final Callback callback, final Context context){
        new InternetCheck(new InternetCheck.Consumer() {
            @Override
            public void accept(Boolean internet) {
                if(!internet){
                    if(callback!= null) {
                        callback.error(context.getString(R.string.message_no_network_connection));
                    }
                }
            }
        });
    }

    /**
     * Get the Reviews record with the provided criteria
     *
     * @param context the application {@link Context}
     * @param movieId     the {@link com.naijaplanet.magosla.android.moviesplanet.models.Movie} id
     * @param page     the page number
     * @param callback the callback if an error was encountered
     * @return an instance of {{@link MoviesResult}}
     */
    public static ReviewsResult getReviewsRecord(final Context context, int movieId, int page, final Callback callback) {

        Request request = new Request.Builder()
                .url(getMovieReviewsUrl(movieId, page))
                .build();

        ReviewsResult reviewsResult = null;

        try {
            Response response = getHttpClient().newCall(request).execute();
            try {
                ResponseBody body = response.body();
                //noinspection StatementWithEmptyBody
                if (body != null) {
                    String responseData = body.string();
                    JSONObject moviesObject = new JSONObject(responseData);

                    if (moviesObject.has("results")) {
                        reviewsResult = ReviewsResult.fromJson(moviesObject);
                    }
                    // TODO what to do when the response has no results field
                } else {
                    // TODO what to do when the response body is empty
                }

            } catch (JSONException e) {
                e.printStackTrace();
                // TODO action to perform if there is a JSONException
            }
        } catch (IOException e) {
            e.printStackTrace();
            // TODO action to perform if there is an IOException error
            new InternetCheck(new InternetCheck.Consumer() {
                @Override
                public void accept(Boolean internet) {
                    if(!internet){
                        if(callback!= null) {
                            callback.error(context.getString(R.string.message_no_network_connection));
                        }
                    }
                }
            });
        }
        return reviewsResult;
    }

    /**
     * Get the Video record with the provided movie id
     *
     * @param context the application {@link Context}
     * @param movieId the id of the movie
     * @param callback the callback if an error was encountered
     * @return an instance of {@link VideosResult}
     */
    public static VideosResult getVideosRecord(final Context context, int movieId, final Callback callback) {

        Request request = new Request.Builder()
                .url(getMovieVideosUrl(movieId))
                .build();

        VideosResult videosResult = null;

        try {
            Response response = getHttpClient().newCall(request).execute();
            try {
                ResponseBody body = response.body();
                //noinspection StatementWithEmptyBody
                if (body != null) {
                    String responseData = body.string();
                    JSONObject moviesObject = new JSONObject(responseData);

                    if (moviesObject.has("results")) {
                        videosResult = VideosResult.fromJson(moviesObject);
                    }
                    // TODO what to do when the response has no results field
                } else {
                    // TODO what to do when the response body is empty
                }

            } catch (JSONException e) {
                e.printStackTrace();
                // TODO action to perform if there is a JSONException
            }
        } catch (IOException e) {
            e.printStackTrace();
            // action to perform if there is an IOException error
            performInternetCheck(callback,context);
        }
        return videosResult;
    }

    /**
     * Callback to provide further information if an error was encountered
     */
    public interface Callback {
        // TODO complete the callback interface methods
        void error(String msg);
    }
}
