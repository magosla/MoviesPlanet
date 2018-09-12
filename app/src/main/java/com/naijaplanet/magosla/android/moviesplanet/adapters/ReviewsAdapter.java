package com.naijaplanet.magosla.android.moviesplanet.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.naijaplanet.magosla.android.moviesplanet.models.Review;
import com.naijaplanet.magosla.android.moviesplanet.models.ReviewsRecord;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsViewHolder>{
    private final Context mContext;
    private final ReviewsRecord mReviewsRecord;

    public void updateItems(List<Review> reviews){
        mReviewsRecord.appendReviews(reviews);
        notifyDataSetChanged();
    }

    public ReviewsRecord getItemsRecord() {
        return mReviewsRecord;
    }

    public ReviewsAdapter(Context context){
        mContext = context;
        mReviewsRecord = new ReviewsRecord();
    }

    @NonNull
    @Override
    public ReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        return ReviewsViewHolder.create(inflater,parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsViewHolder holder, int position) {
        holder.bind(mReviewsRecord.getReviewAt(position));
    }

    @Override
    public int getItemCount() {
        return mReviewsRecord.getTotalReviewsFetched();
    }
}
