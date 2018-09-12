package com.naijaplanet.magosla.android.moviesplanet.data;

import com.naijaplanet.magosla.android.moviesplanet.models.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

@SuppressWarnings({"WeakerAccess", "FieldCanBeLocal"})
public class VideosResult extends Results<Video>{
    private int movieId;


    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public static VideosResult fromJson(JSONObject data){
        VideosResult videosResult = new VideosResult();

        try {
            if (data.has("id")){
                videosResult.setMovieId(data.getInt("id"));
            }
            if (data.has("results")){
                JSONArray videosObject = data.getJSONArray("results");
                Video[] videos = new Video[videosObject.length()];
                for (int i = 0; i < videosObject.length(); i++){
                    JSONObject videoObject = videosObject.getJSONObject(i);
                    Video video = new Video();
                    video.setId(videoObject.getString("id"));
                    video.setKey(videoObject.getString("key"));
                    video.setName(videoObject.getString("name"));
                    video.setSite(videoObject.getString("site"));
                    video.setType(videoObject.getString("type"));
                    video.setSize(videoObject.getInt("size"));
                    video.setId(videoObject.getString("id"));

                    // append to the list of reviews
                    videos[i] = video;
                }
                videosResult.setResults(Arrays.asList(videos));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return videosResult;
    }
}
