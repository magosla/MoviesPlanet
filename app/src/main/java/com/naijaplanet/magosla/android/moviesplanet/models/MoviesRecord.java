package com.naijaplanet.magosla.android.moviesplanet.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.naijaplanet.magosla.android.moviesplanet.data.MoviesResult;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class MoviesRecord implements Parcelable {
    // This is to de-serialize the object
    public static final Parcelable.Creator<MoviesRecord> CREATOR = new Parcelable.Creator<MoviesRecord>() {
        public MoviesRecord createFromParcel(Parcel in) {
            return new MoviesRecord(in);
        }

        public MoviesRecord[] newArray(int size) {
            return new MoviesRecord[size];
        }
    };
    private int mCurrentPage;
    private int mPagesAvailable;
    private int mTotalMoviesAvailable;
    private int mTotalMoviesFetched;
    private List<Movie> mMovies;
    private String mMovieDbFilter = "";

    public MoviesRecord() {
        mMovies = new ArrayList<>();
        // mMovieDbFilter="";
    }

    private MoviesRecord(Parcel in) {
        // call the default constructor to initialize list
        this();
        mCurrentPage = in.readInt();
        mPagesAvailable = in.readInt();
        mTotalMoviesAvailable = in.readInt();
        mTotalMoviesFetched = in.readInt();
        mMovieDbFilter = in.readString();
        in.readTypedList(mMovies, Movie.CREATOR);
    }

    public void reset() {
        mCurrentPage = 0;
        mPagesAvailable = 0;
        mTotalMoviesAvailable = 0;
        mTotalMoviesFetched = 0;
        mMovies = new ArrayList<>();
        mMovieDbFilter = "";
    }

    /**
     * Return a {{@link Movie}} from the list
     *
     * @param index the position in the list
     * @return {{@link Movie}} or null when index is unavailable
     */
    public Movie getMovieAt(int index) {
        if (index < mTotalMoviesFetched) {
            return mMovies.get(index);
        }
        return null;
    }

    /**
     * The most recent page number from Movie database used to retrieve last result
     *
     * @return the page number
     */
    public int getCurrentPage() {
        return mCurrentPage;
    }

    public void setCurrentPage(int v) {
        mCurrentPage = v;
    }

    /**
     * Total number of pages Available with search criteria in Movies Database
     *
     * @return total pages available on the server
     */
    public int getPagesAvailable() {
        return mPagesAvailable;
    }

    public void setPagesAvailable(int mPagesAvailable) {
        this.mPagesAvailable = mPagesAvailable;
    }

    public int getTotalMoviesAvailable() {
        return mTotalMoviesAvailable;
    }

    public void setTotalMoviesAvailable(int mTotalMoviesAvailable) {
        this.mTotalMoviesAvailable = mTotalMoviesAvailable;
    }

    public int getTotalMoviesFetched() {
        return mTotalMoviesFetched;
    }

    public void setTotalMoviesFetched(int mTotalMoviesFetched) {
        this.mTotalMoviesFetched = mTotalMoviesFetched;
    }

    public String getMovieDbFilter() {
        return mMovieDbFilter;
    }

    public void setMovieDbFilter(String movieDbFilter) {
        this.mMovieDbFilter = movieDbFilter;
    }

    public void addMovies(MoviesResult moviesResult){
        addMovies(moviesResult, false);
    }

    public void addMovies(MoviesResult moviesResult, boolean forceReset) {
        boolean filterChanged = mMovieDbFilter != null && !mMovieDbFilter.equals(moviesResult.getResultFilter());
        if (filterChanged || forceReset) {
            // we are using a different filter, so empty any previous data
            reset();
        }
        if (forceReset || ((!moviesResult.getResults().isEmpty() || filterChanged) &&
                // this below expression prevent duplicate addition
                !(mCurrentPage == moviesResult.getPage() && moviesResult.getResultFilter().equals(mMovieDbFilter))
        )) {

            add(moviesResult);
        }
    }

    private void add(MoviesResult moviesResult) {
        mMovies.addAll(moviesResult.getResults());
        mTotalMoviesFetched = mMovies.size();
        mMovieDbFilter = moviesResult.getResultFilter();
        mTotalMoviesAvailable = moviesResult.getTotalResults();
        mPagesAvailable = moviesResult.getTotalPages();
        mCurrentPage = moviesResult.getPage();
    }

    /**
     * Set the movie in the movies record. this deletes any previous record
     *
     * @param moviesResult a {@link MoviesResult} instance
     */
    public void setMovies(MoviesResult moviesResult) {
        reset();
        add(moviesResult);
    }

    /**
     * If the movie record is empty
     *
     * @return boolean
     */
    public boolean isEmpty() {
        return mMovies.isEmpty();
    }

    public void appendMovies(List<Movie> movies) {
        mMovies.addAll(movies);
        mTotalMoviesFetched = mMovies.size();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mCurrentPage);
        dest.writeInt(mPagesAvailable);
        dest.writeInt(mTotalMoviesAvailable);
        dest.writeInt(mTotalMoviesFetched);
        dest.writeString(mMovieDbFilter);
        dest.writeTypedList(mMovies);
    }

    @Override
    public int describeContents() {
        return hashCode();
    }


}
