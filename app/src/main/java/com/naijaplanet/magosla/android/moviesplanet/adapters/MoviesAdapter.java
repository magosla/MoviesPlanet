package com.naijaplanet.magosla.android.moviesplanet.adapters;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.naijaplanet.magosla.android.moviesplanet.R;
import com.naijaplanet.magosla.android.moviesplanet.models.Movie;
import com.naijaplanet.magosla.android.moviesplanet.models.MoviesRecord;
import com.squareup.picasso.Picasso;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesViewHolder> {
    @SuppressWarnings({"FieldCanBeLocal", "CanBeFinal"})
    private final Context mContext;
    private MoviesRecord mMoviesRecord;
    private final OnEventListener mEventListener;


    public interface OnEventListener {
        void onItemClick(Movie item);
    }

    public MoviesAdapter(Context context, OnEventListener eventListener) {
        mContext = context;
        mMoviesRecord = new MoviesRecord();
        mEventListener=eventListener;
    }

    public void setMoviesRecord(MoviesRecord moviesRecord){
        mMoviesRecord=moviesRecord;
        notifyDataSetChanged();
    }

    public MoviesRecord getMoviesRecord() {
        return mMoviesRecord;
    }


    @NonNull
    @Override
    public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return MoviesViewHolder.create(inflater,parent,this, mEventListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesViewHolder holder, int position) {
        holder.bind(mMoviesRecord.getMovieAt(position));
    }

    @Override
    public int getItemCount() {
        return mMoviesRecord.getTotalMoviesFetched();
    }


    @BindingAdapter("android:src")
    public static void setImageUrl(ImageView view, String url) {
        if(TextUtils.isEmpty(url)){
            return;
        }
        Picasso.get().load(url).placeholder(R.drawable.placeholder_500x750).into(view);
    }
}
