package com.naijaplanet.magosla.android.moviesplanet.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naijaplanet.magosla.android.moviesplanet.adapters.ReviewsAdapter;
import com.naijaplanet.magosla.android.moviesplanet.data.ReviewsResult;
import com.naijaplanet.magosla.android.moviesplanet.databinding.FragmentReviewsBinding;
import com.naijaplanet.magosla.android.moviesplanet.loaders.AppLoader;
import com.naijaplanet.magosla.android.moviesplanet.loaders.ReviewsLoader;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

@SuppressWarnings("ConstantConditions")
public class ReviewsFragment extends Fragment {
    private static final String BUNDLE_MOVIE_ID = "movie_id";
    private ReviewsLoader mReviewsLoader;
    private ReviewsAdapter mReviewsAdapter;
    //private ReviewsRecord mReviewsRecord;
    private FragmentReviewsBinding mBinding;
    private int mMovieId;

    public static ReviewsFragment newInstance(int movieId) {
        ReviewsFragment f = new ReviewsFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_MOVIE_ID, movieId);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Bundle arg = getArguments();
        mMovieId = arg != null ? arg.getInt(BUNDLE_MOVIE_ID, 0) : 0;

        mReviewsAdapter = new ReviewsAdapter(getContext());

        mReviewsLoader = new ReviewsLoader(getContext(), getActivity().getSupportLoaderManager(), new AppLoader.AppLoaderCallback<ReviewsResult>() {
            @Override
            public void loadingItems() {
                mBinding.tvLoading.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadFinished(ReviewsResult result) {
                if (result != null && result.getPage() != mReviewsAdapter.getItemsRecord().getCurrentPage()) {
                    mReviewsAdapter.updateItems(result.getResults());
                }

                if (result != null && result.getResults().isEmpty() && mReviewsAdapter.getItemsRecord().isEmpty()) {
                    mBinding.tvNoReview.setVisibility(View.VISIBLE);
                }
                mBinding.tvLoading.setVisibility(View.GONE);
            }

            @Override
            public void onError(String errorMessage) {
                mBinding.tvLoading.setVisibility(View.GONE);
            }
        });


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mBinding = FragmentReviewsBinding.inflate(inflater, container, false);

        RecyclerView recyclerView = mBinding.rvMovieReviews;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setAdapter(mReviewsAdapter);
        recyclerView.setLayoutManager(layoutManager);


        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), VERTICAL);
        recyclerView.addItemDecoration(decoration);

        if (mReviewsAdapter.getItemsRecord().isEmpty()) {
            loadReviews(1);
        }

        return mBinding.getRoot();
    }

    @SuppressWarnings("SameParameterValue")
    private void loadReviews(int page) {
        mReviewsLoader.load(mMovieId, page < 1 ? 1 : page);
    }
}
