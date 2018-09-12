package com.naijaplanet.magosla.android.moviesplanet;

public class Config {
    public static final String MOVIEDB_THUMBNAIL_SIZE = "w185";
    public static final String MOVIEDB_FULL_SIZE = "w500";
    public static final String MOVIEDB_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String MOVIEDB_BASE_URL = "https://api.themoviedb.org/3/movie?language=en-US&api_key="+BuildConfig.MOVIEDB_API_KEY;
    public static final String EXTRA_MOVIE_KEY = "extra_movie";
    public static final String MOVIE_VIDEOS_URI_TEMPLATE = "%d/videos";
    public static final String MOVIE_REVIEW_URI_TEMPLATE = "%d/reviews";
    public static final String YOUTUBE_THUMBNAIL_URL_FORMAT = "https://i.ytimg.com/vi/%s/hqdefault.jpg";
    public static final String YOUTUBE_VIDEO_URL_FORMAT = "http://youtu.be/%s";

    public static final String DATABASE_NAME = "movies_planet";


}
