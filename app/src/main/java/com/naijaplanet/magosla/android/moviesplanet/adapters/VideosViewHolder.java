package com.naijaplanet.magosla.android.moviesplanet.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.databinding.library.baseAdapters.BR;

import com.naijaplanet.magosla.android.moviesplanet.R;
import com.naijaplanet.magosla.android.moviesplanet.databinding.MovieVideoItemBinding;
import com.naijaplanet.magosla.android.moviesplanet.models.Video;
import com.naijaplanet.magosla.android.moviesplanet.util.MovieDbUtil;

public class VideosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final VideosAdapter.OnEventListener mEventListener;
    private final MovieVideoItemBinding mBinding;

    public static VideosViewHolder create(LayoutInflater inflater, ViewGroup parent, VideosAdapter.OnEventListener eventListener){
        MovieVideoItemBinding binding = MovieVideoItemBinding.inflate(inflater, parent, false);
        return new VideosViewHolder(binding, eventListener);
    }

    private VideosViewHolder(MovieVideoItemBinding dataBinding, VideosAdapter.OnEventListener eventListener) {
        super(dataBinding.getRoot());
        mBinding = dataBinding;
        mEventListener = eventListener;
    }

    public void bind(Video item, int adapterPosition){
        itemView.setTag(item);
        mBinding.setVariable(BR.video, item);
        mBinding.setVariable(BR.thumbnail_url, MovieDbUtil.getYoutubeThumbnailUrl(item.getKey()));
        mBinding.setVariable(BR.sharable, adapterPosition == 0); // make first video sharable
        mBinding.actionPlay.setOnClickListener(this);
        mBinding.actionPlay.setTag(R.string.action_play, R.string.action);
        mBinding.actionShare.setOnClickListener(this);
        mBinding.actionShare.setTag(R.string.action_share, R.string.action);
    }

    @Override
    public void onClick(View v) {
        mEventListener.onItemClick(v, (Video) itemView.getTag());
    }
}
