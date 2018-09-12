package com.naijaplanet.magosla.android.moviesplanet.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.stetho.Stetho;
import com.naijaplanet.magosla.android.moviesplanet.Config;
import com.naijaplanet.magosla.android.moviesplanet.EndlessRecyclerOnScrollListener;
import com.naijaplanet.magosla.android.moviesplanet.R;
import com.naijaplanet.magosla.android.moviesplanet.adapters.MoviesAdapter;
import com.naijaplanet.magosla.android.moviesplanet.databinding.ActivityMainBinding;
import com.naijaplanet.magosla.android.moviesplanet.fragments.SettingsFragment;
import com.naijaplanet.magosla.android.moviesplanet.models.Movie;
import com.naijaplanet.magosla.android.moviesplanet.models.MoviesRecord;
import com.naijaplanet.magosla.android.moviesplanet.util.ActivityUtil;
import com.naijaplanet.magosla.android.moviesplanet.util.GridItemSpacingDecoration;
import com.naijaplanet.magosla.android.moviesplanet.util.GridItemsSpanSpacing;
import com.naijaplanet.magosla.android.moviesplanet.viewmodels.MoviesViewModel;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, MoviesAdapter.OnEventListener {
    private MoviesAdapter mMoviesAdapter;
    private ActivityMainBinding mActivityMainBinding;
    private EndlessRecyclerOnScrollListener mEndlessRecyclerOnScrollListener;
    private MoviesViewModel mMoviesViewModel;
    private int mSettingsFragmentCommitId;
    private boolean mMoviesLoading; // to keep track of when movie loading is in process

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// try to enable activity change transitions if supported
        ActivityUtil.enableTransition(this);
        // used to inspect network request in chrome inspector
        Stetho.initializeWithDefaults(this);
        // register the sharedpreference change listener
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        setupView();

        if (!mMoviesViewModel.hasRecord()) {
            loadMovies(1, null);
        }
    }

    private void initializeViewModel() {
        mMoviesViewModel = ViewModelProviders.of(this).get(MoviesViewModel.class);
        mMoviesViewModel.isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean loading) {
                //noinspection ConstantConditions
                mMoviesLoading = loading;
                if (loading)
                    removePlaceHolders();
                mActivityMainBinding.pbLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
            }
        });
        mMoviesViewModel.getErrors().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                // showMessage(s);
                if (mMoviesViewModel.hadFetchError()) {
                    mActivityMainBinding.actionReload.setVisibility(View.VISIBLE);
                }
            }
        });

        mMoviesViewModel.getMoviesRecord().observe(this, new Observer<MoviesRecord>() {
            @Override
            public void onChanged(@Nullable MoviesRecord moviesRecord) {
                Log.d(this.getClass().getSimpleName(), "Items found is null: " + String.valueOf(moviesRecord == null));
                mMoviesAdapter.setMoviesRecord(moviesRecord);
                if ((moviesRecord == null || moviesRecord.isEmpty())&& !mMoviesLoading)
                    mActivityMainBinding.tvNoItem.setVisibility(View.VISIBLE);

            }
        });
    }

    private void removePlaceHolders() {
        mActivityMainBinding.tvNoItem.setVisibility(View.GONE);
        mActivityMainBinding.actionReload.setVisibility(View.GONE);
    }

    private void setupView() {
        mActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        layoutManager.setSmoothScrollbarEnabled(true);

        mMoviesAdapter = new MoviesAdapter(this, this);
        mActivityMainBinding.rvMovieList.setLayoutManager(layoutManager);
        mActivityMainBinding.rvMovieList.setAdapter(mMoviesAdapter);

        GridItemsSpanSpacing gridItemsSpanSpacing = new GridItemsSpanSpacing(mActivityMainBinding.rvMovieList,
                R.dimen.movie_item_width, 0, layoutManager.getOrientation(), true);

        layoutManager.setSpanCount(gridItemsSpanSpacing.getSpan());

        mActivityMainBinding.rvMovieList.addItemDecoration(new GridItemSpacingDecoration(gridItemsSpanSpacing));

        initializeViewModel();
        mActivityMainBinding.actionReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMoviesViewModel.reLoad();
            }
        });
    }

    private void updateTitle(CharSequence title) {
        setTitle(getString(R.string.title_movies, title)
                .toUpperCase().replace("_", " "));
    }


    /**
     * Show or hide the setting to filter movies
     */
    private void toggleMovieFilterSetting() {
        int fragmentId = mActivityMainBinding.flSettingsHolder.getId();

        FragmentManager fm = getSupportFragmentManager();
        SettingsFragment settingsFragment = (SettingsFragment) fm.findFragmentById(fragmentId);
        if (settingsFragment == null) {
            mSettingsFragmentCommitId =
                    fm.beginTransaction()
                            .replace(fragmentId, SettingsFragment.create())
                            .addToBackStack(null).commit();
        } else {
            fm.popBackStack(mSettingsFragmentCommitId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        unInitializeEndlessScroll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeEndlessScroll();
    }

    /**
     * Initialize the endless scroll facility
     */
    private void initializeEndlessScroll() {
        mEndlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                if (!mMoviesLoading) {
                    MoviesRecord moviesRecord = mMoviesAdapter.getMoviesRecord();
                    loadMovies(moviesRecord.getCurrentPage() + 1, moviesRecord.getMovieDbFilter());
                }
            }
        };
        mActivityMainBinding.rvMovieList.addOnScrollListener(mEndlessRecyclerOnScrollListener);
    }

    /**
     * Un-initialize the endless scroll
     */
    private void unInitializeEndlessScroll() {
        mActivityMainBinding.rvMovieList.removeOnScrollListener(mEndlessRecyclerOnScrollListener);
        mEndlessRecyclerOnScrollListener = null;
    }

    /**
     * Method to load movies
     *
     * @param page   {{int}} page numbers
     * @param filter {{String}} filter for the movies to load
     */
    private void loadMovies(int page, String filter) {
        if (filter == null || TextUtils.isEmpty(filter)) {

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

            filter = sharedPreferences.getString(getString(R.string.pref_filter_key), getString(R.string.pref_filter_default));

        }
        updateTitle(filter);
        mMoviesViewModel.loadMovies(filter, page);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                toggleMovieFilterSetting();
                /*
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                 */
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_filter_key))) {
            loadMovies(1, null);
        }
    }


    @Override
    public void onItemClick(Movie movie) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Config.EXTRA_MOVIE_KEY, movie);
        ActivityUtil.lunchActivityWithTransition(this, DetailsActivity.class, bundle);
    }

}

