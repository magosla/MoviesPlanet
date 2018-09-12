package com.naijaplanet.magosla.android.moviesplanet.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naijaplanet.magosla.android.moviesplanet.Config;
import com.naijaplanet.magosla.android.moviesplanet.R;
import com.naijaplanet.magosla.android.moviesplanet.data.room.FavoriteId;
import com.naijaplanet.magosla.android.moviesplanet.databinding.FragmentFavoriteBinding;
import com.naijaplanet.magosla.android.moviesplanet.models.Movie;
import com.naijaplanet.magosla.android.moviesplanet.viewmodels.FavoriteViewModel;

public class FavoriteFragment extends Fragment {
    private FragmentFavoriteBinding mBinding;
    private int mMovieId;
    private Movie mMovie;

    // for demo purpose
    private boolean isFavorite;

    private FavoriteViewModel favoriteViewModel;

    private LiveData<FavoriteId> mIsFavorite;

    public static FavoriteFragment newInstance(Movie movie) {
        FavoriteFragment f = new FavoriteFragment();
        Bundle args = new Bundle();
        args.putParcelable(Config.EXTRA_MOVIE_KEY, movie);

        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);


        Bundle arg = getArguments();
        if(arg != null) {
            mMovie = arg.getParcelable(Config.EXTRA_MOVIE_KEY);
        }
        mMovieId = mMovie != null ? mMovie.getId() : 0;

        favoriteViewModel = ViewModelProviders.of(this).get(FavoriteViewModel.class);
        mIsFavorite = favoriteViewModel.isFavorite(mMovieId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mBinding = FragmentFavoriteBinding.inflate(inflater, container, false);

        setUpButton();


        return mBinding.getRoot();

    }

    /**
     * Update the state of the button
     */
    private void updateUiState() {

        int buttonImage = isFavorite ? R.drawable.ic_star_white_100dp : R.drawable.ic_star_white_stroke_100dp;

        mBinding.actionFavorite.setEnabled(true);

        mBinding.actionFavorite
                .setImageResource(buttonImage);
    }

    @Override
    public void onPause() {
        super.onPause();
        mIsFavorite.removeObservers(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        mIsFavorite.observe(this, new Observer<FavoriteId>() {
            @Override
            public void onChanged(@Nullable FavoriteId favoriteId) {
                isFavorite = favoriteId != null;
                updateUiState();
            }
        });
    }

    private void setUpButton() {
        mBinding.actionFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isEnabled()) {
                    v.setEnabled(false);
                    updateFavorite();
                }
            }
        });
    }

    private void updateFavorite() {
        if (isFavorite) {
            favoriteViewModel.deleteFavorite(mMovieId);

        } else {
            favoriteViewModel.addToFavorite(mMovie);
        }

    }
}
