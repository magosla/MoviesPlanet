package com.naijaplanet.magosla.android.moviesplanet.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.naijaplanet.magosla.android.moviesplanet.R;
import com.naijaplanet.magosla.android.moviesplanet.adapters.VideosAdapter;
import com.naijaplanet.magosla.android.moviesplanet.data.VideosResult;
import com.naijaplanet.magosla.android.moviesplanet.databinding.FragmentVideosBinding;
import com.naijaplanet.magosla.android.moviesplanet.loaders.AppLoader;
import com.naijaplanet.magosla.android.moviesplanet.loaders.VideosLoader;
import com.naijaplanet.magosla.android.moviesplanet.models.Video;
import com.naijaplanet.magosla.android.moviesplanet.util.MovieDbUtil;

@SuppressWarnings("ConstantConditions")
public class VideosFragment extends Fragment implements VideosAdapter.OnEventListener {


    private VideosLoader mVideosLoader;
    private VideosAdapter mVideosAdapter;

    private FragmentVideosBinding mBinding;
    private int mMovieId;
    private static final String BUNDLE_MOVIE_ID = "movie_id";

    public static VideosFragment newInstance(int movieId) {
        VideosFragment f = new VideosFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_MOVIE_ID, movieId);
        f.setArguments(args);
        return f;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Bundle arg = getArguments();
        mMovieId = arg!=null? arg.getInt(BUNDLE_MOVIE_ID, 0):0;

        mVideosAdapter = new VideosAdapter(getContext(), this);

        mVideosLoader = new VideosLoader(getContext(), getActivity().getSupportLoaderManager(), new AppLoader.AppLoaderCallback<VideosResult>() {

            @Override
            public void loadingItems() {
                mBinding.tvLoading.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadFinished(VideosResult result) {

                if (result != null) {
                    mVideosAdapter.setItems(result.getResults());
                }

                if (result != null && result.getResults().isEmpty() && mVideosAdapter.getItems().isEmpty()) {
                    mBinding.tvNoVideo.setVisibility(View.VISIBLE);
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

        mBinding = FragmentVideosBinding.inflate(inflater, container, false);

        RecyclerView recyclerView = mBinding.rvMovieVideos;

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mVideosAdapter);


        if (mVideosAdapter.getItems().isEmpty()) {
            mVideosLoader.load(mMovieId);
        }

        return mBinding.getRoot();
    }

    @Override
    public void onItemClick(View view, Video item) {
        Log.d("Clicking", "yes");
        if (view.getTag(R.string.action_play) != null) {
            Log.d("Entering", "yes");
            Uri uri = Uri.parse(MovieDbUtil.getYoutubeVideoUrl(item.getKey()));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }else if (view.getTag(R.string.action_share) != null){
            Uri uri = Uri.parse(MovieDbUtil.getYoutubeVideoUrl(item.getKey()));
            ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(getActivity());
            intentBuilder.setType("text/plain").setText(uri.toString());
            startActivity(Intent.createChooser(intentBuilder.getIntent(), getString(R.string.label_share_with)));
        }
    }
}
