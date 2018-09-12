package com.naijaplanet.magosla.android.moviesplanet.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naijaplanet.magosla.android.moviesplanet.models.Video;

import java.util.ArrayList;
import java.util.List;

public class VideosAdapter extends RecyclerView.Adapter<VideosViewHolder> {
    private final Context mContext;
    private List<Video> mVideos;
    private final OnEventListener mEventListener;

    public VideosAdapter(Context context, OnEventListener eventListener){
        mContext = context;
        mEventListener = eventListener;
        mVideos = new ArrayList<>();
    }

    /**
     * get the items in the adapter
     * @return the {@link List<Video>} currently available
     */
    public List<Video> getItems(){
        return mVideos;
    }
    public void setItems(List<Video> videos){
        mVideos = videos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        return VideosViewHolder.create(layoutInflater, parent, mEventListener);
    }

    @Override
    public void onBindViewHolder(@NonNull VideosViewHolder holder, int position) {
            holder.bind(mVideos.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mVideos.size();
    }


    public interface OnEventListener {
        void onItemClick(View view, Video item);
    }
}
