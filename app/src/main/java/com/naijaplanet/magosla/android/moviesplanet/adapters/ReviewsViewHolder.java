package com.naijaplanet.magosla.android.moviesplanet.adapters;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.android.databinding.library.baseAdapters.BR;

import com.naijaplanet.magosla.android.moviesplanet.databinding.MovieReviewItemBinding;
import com.naijaplanet.magosla.android.moviesplanet.models.Review;

public class ReviewsViewHolder extends RecyclerView.ViewHolder{
    private final ViewDataBinding mBinding;
    public static ReviewsViewHolder create(LayoutInflater inflater, ViewGroup parent){
        ViewDataBinding binding = MovieReviewItemBinding.inflate(inflater, parent, false);

        return new ReviewsViewHolder(binding);

    }

    private ReviewsViewHolder(ViewDataBinding dataBinding) {
        super(dataBinding.getRoot());
        mBinding = dataBinding;
    }

    public void bind(Review item){
        itemView.setTag(item);
        mBinding.setVariable(BR.review, item);
        mBinding.setVariable(BR.icon_letter, String.valueOf(item.getAuthor().charAt(0)));
        mBinding.executePendingBindings();
    }

}
