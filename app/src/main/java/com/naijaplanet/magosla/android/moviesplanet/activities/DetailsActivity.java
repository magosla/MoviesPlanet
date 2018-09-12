package com.naijaplanet.magosla.android.moviesplanet.activities;

import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.databinding.library.baseAdapters.BR;
import com.facebook.stetho.Stetho;
import com.naijaplanet.magosla.android.moviesplanet.Config;
import com.naijaplanet.magosla.android.moviesplanet.R;
import com.naijaplanet.magosla.android.moviesplanet.databinding.ActivityDetailsBinding;
import com.naijaplanet.magosla.android.moviesplanet.fragments.FavoriteFragment;
import com.naijaplanet.magosla.android.moviesplanet.fragments.ReviewsFragment;
import com.naijaplanet.magosla.android.moviesplanet.fragments.VideosFragment;
import com.naijaplanet.magosla.android.moviesplanet.models.Movie;
import com.naijaplanet.magosla.android.moviesplanet.util.ActivityUtil;
import com.naijaplanet.magosla.android.moviesplanet.util.MovieDbUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DetailsActivity extends AppCompatActivity {
    private static final String BUNDLE_FULL_PHOTO = "full_photo_open";
    private static final String TAG_REVIEWS_FRAGMENT = "reviews";
    private static final String TAG_VIDEOS_FRAGMENT = "videos";
    private static final String TAG_FAVORITE_FRAGMENT = "favorite";
    private ActivityDetailsBinding mActivityDetailsBinding;
    private boolean fullPhotoOpen;
    private int mMovieId;
    private Movie mMovie;

    @SuppressWarnings("unused")
    @BindingAdapter("android:text")
    public static void setText(TextView view, Double value) {
        view.setText(String.valueOf(value));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // try to enable activity change transitions if supported
        ActivityUtil.enableTransition(this);
        // used to inspect network request in chrome inspector
        Stetho.initializeWithDefaults(this);

        mActivityDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
            return;
        }
        Bundle extras = intent.getExtras();

        if (extras != null) {
            mMovie = extras.getParcelable(Config.EXTRA_MOVIE_KEY);
        }
        if (mMovie == null) {
            closeOnError();
            return;
        }
        mMovieId = mMovie.getId();

        setSupportActionBar(mActivityDetailsBinding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(mMovie.getTitle());
        }

        mActivityDetailsBinding.setVariable(BR.movie, mMovie);
        String photo_url = MovieDbUtil.getPhotoUrl(mMovie.getPosterPath(), Config.MOVIEDB_FULL_SIZE);
        mActivityDetailsBinding.setVariable(BR.photo_url, photo_url);
        mActivityDetailsBinding.ivFullPostal.setTag(mMovie.getId());

        setupFullPostalView(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupFragments();
    }

    private void setupFullPostalView(Bundle savedInstanceState) {
        mActivityDetailsBinding.postalIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFullPhoto(true);
            }
        });

        Toolbar postalToolbar = mActivityDetailsBinding.toolbarPostal;

        postalToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        postalToolbar.inflateMenu(R.menu.menu_full_postal_toolbar);
        postalToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // implement the photo sharing intent
                Drawable drawable = mActivityDetailsBinding.ivFullPostal.getDrawable();


                if (drawable instanceof BitmapDrawable) {
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

                    File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                            "full_postal_" + mActivityDetailsBinding.ivFullPostal.getTag().toString() + ".png");

                    try {
                        FileOutputStream outputStream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
                        outputStream.close();
                        Uri bitmapUri = Uri.fromFile(file);

                        ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(DetailsActivity.this);
                        intentBuilder.setType("image/*").setStream(bitmapUri);
                        startActivity(Intent.createChooser(intentBuilder.getIntent(), getString(R.string.label_share_with)));
                        return true;

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
        tryRestoreFullPhoto(savedInstanceState);
    }

    private void tryRestoreFullPhoto(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_FULL_PHOTO)) {
            fullPhotoOpen = savedInstanceState.getBoolean(BUNDLE_FULL_PHOTO);
            if (fullPhotoOpen) {
                toggleFullPhoto(true);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (fullPhotoOpen) {
            toggleFullPhoto(false);
            return;
        }
        super.onBackPressed();
    }

    private void toggleFullPhoto(boolean open) {
        if (open) {
            // mActivityDetailsBinding.appbar.setVisibility(View.GONE);
            mActivityDetailsBinding.flFullPostal.setVisibility(View.VISIBLE);
        } else {
            //  mActivityDetailsBinding.appbar.setVisibility(View.VISIBLE);
            mActivityDetailsBinding.flFullPostal.setVisibility(View.GONE);
        }
        fullPhotoOpen = open;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (fullPhotoOpen) {
            outState.putBoolean(BUNDLE_FULL_PHOTO, fullPhotoOpen);
        }
        super.onSaveInstanceState(outState);
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.message_movie_detail_error, Toast.LENGTH_SHORT).show();
    }

    private void setupFragments() {
        FragmentManager fm = getSupportFragmentManager();
        VideosFragment videosFragment = (VideosFragment) fm.findFragmentByTag(TAG_VIDEOS_FRAGMENT);
        ReviewsFragment reviewsFragment = (ReviewsFragment) fm.findFragmentByTag(TAG_REVIEWS_FRAGMENT);
        FavoriteFragment favoriteFragment = (FavoriteFragment) fm.findFragmentByTag(TAG_FAVORITE_FRAGMENT);


        if (videosFragment == null || reviewsFragment == null || favoriteFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();

            if (videosFragment == null) {
                ft.add(mActivityDetailsBinding.videosFragmentContainer.getId(), VideosFragment.newInstance(mMovieId), TAG_VIDEOS_FRAGMENT);
            }
            if (reviewsFragment == null) {
                ft.add(mActivityDetailsBinding.reviewsFragmentContainer.getId(), ReviewsFragment.newInstance(mMovieId), TAG_REVIEWS_FRAGMENT);
            }
            if (favoriteFragment == null) {

                ft.add(mActivityDetailsBinding.favoriteFragmentContainer.getId(), FavoriteFragment.newInstance(mMovie), TAG_FAVORITE_FRAGMENT);
            }

            ft.commit();
        }


    }
}
