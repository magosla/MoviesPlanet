package com.naijaplanet.magosla.android.moviesplanet.data;

import com.naijaplanet.magosla.android.moviesplanet.models.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


public class ReviewsResult extends Results<Review>{
    private int movieId;

    public int getMovieId() {
        return movieId;
    }

    @SuppressWarnings("WeakerAccess")
    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public static ReviewsResult fromJson(JSONObject data){
        ReviewsResult reviewsResult = new ReviewsResult();

        try {
            if(data.has("page")) {
                reviewsResult.setPage(data.getInt("page"));
            }
            if (data.has("total_results")){
                reviewsResult.setTotalResults(data.getInt("total_results"));
            }
            if (data.has("total_pages")){
                reviewsResult.setTotalPages(data.getInt("total_pages"));
            }
            if (data.has("id")){
                reviewsResult.setMovieId(data.getInt("id"));
            }
            if (data.has("results")){
                JSONArray reviewsObject = data.getJSONArray("results");
                Review[] reviews = new Review[reviewsObject.length()];
                for (int i = 0; i < reviewsObject.length(); i++){
                    JSONObject reviewObject = reviewsObject.getJSONObject(i);
                    Review review = new Review();
                    review.setAuthor(reviewObject.getString("author"));
                    review.setContent(reviewObject.getString("content"));
                    review.setId(reviewObject.getString("id"));

                    // append to the list of reviews
                    reviews[i] = review;
                }
                reviewsResult.setResults(Arrays.asList(reviews));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviewsResult;
    }
}
