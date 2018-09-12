package com.naijaplanet.magosla.android.moviesplanet.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.naijaplanet.magosla.android.moviesplanet.data.ReviewsResult;

import java.util.ArrayList;
import java.util.List;

public class ReviewsRecord implements Parcelable{
    // This is to de-serialize the object
    public static final Parcelable.Creator<ReviewsRecord> CREATOR = new Parcelable.Creator<ReviewsRecord>() {
        public ReviewsRecord createFromParcel(Parcel in) {
            return new ReviewsRecord(in);
        }

        public ReviewsRecord[] newArray(int size) {
            return new ReviewsRecord[size];
        }
    };
    private int mCurrentPage;
    private int mPagesAvailable;
    private int mTotalReviewsAvailable;
    private int mTotalReviewsFetched;
    private final List<Review> mReviews;

    public ReviewsRecord() {
        mReviews = new ArrayList<>();
    }

    private ReviewsRecord(Parcel in) {
        // call the default constructor to initialize list
        this();
        mCurrentPage = in.readInt();
        mPagesAvailable = in.readInt();
        mTotalReviewsAvailable = in.readInt();
        mTotalReviewsFetched = in.readInt();
        in.readTypedList(mReviews, Review.CREATOR);
    }

// --Commented out by Inspection START (09/09/2018 16:12):
//    private void reset() {
//        mCurrentPage = 0;
//        mPagesAvailable = 0;
//        mTotalReviewsAvailable = 0;
//        mTotalReviewsFetched = 0;
//        mReviews = new ArrayList<>();
//    }
// --Commented out by Inspection STOP (09/09/2018 16:12)

    /**
     * Return a {{@link Review}} from the list
     *
     * @param index the position in the list
     * @return {{@link Review}} or null when index is unavailable
     */
    public Review getReviewAt(int index) {
        if (index < mTotalReviewsFetched) {
            return mReviews.get(index);
        }
        return null;
    }

    /**
     * The most recent page number from Review database used to retrieve last result
     *
     * @return the page number
     */
    public int getCurrentPage() {
        return mCurrentPage;
    }

    @SuppressWarnings("unused")
    public void setCurrentPage(int v) {
        mCurrentPage = v;
    }

    /**
     * Total number of pages Available with search criteria in Reviews Database
     *
     * @return total pages available on the server
     */
    @SuppressWarnings("unused")
    public int getPagesAvailable() {
        return mPagesAvailable;
    }

    @SuppressWarnings("unused")
    public void setPagesAvailable(int mPagesAvailable) {
        this.mPagesAvailable = mPagesAvailable;
    }

    @SuppressWarnings("unused")
    public int getTotalReviewsAvailable() {
        return mTotalReviewsAvailable;
    }

    @SuppressWarnings("unused")
    public void setTotalReviewsAvailable(int mTotalReviewsAvailable) {
        this.mTotalReviewsAvailable = mTotalReviewsAvailable;
    }

    public int getTotalReviewsFetched() {
        return mTotalReviewsFetched;
    }

    @SuppressWarnings("unused")
    public void setTotalReviewsFetched(int mTotalReviewsFetched) {
        this.mTotalReviewsFetched = mTotalReviewsFetched;
    }

    @SuppressWarnings("unused")
    public void addReviews(ReviewsResult reviewsResult) {
        if ((!reviewsResult.getResults().isEmpty()) &&
                // this below expression prevent duplicate addition
                !(mCurrentPage == reviewsResult.getPage())
                ) {

            mReviews.addAll(reviewsResult.getResults());
            mTotalReviewsFetched = mReviews.size();
            mTotalReviewsAvailable = reviewsResult.getTotalResults();
            mPagesAvailable = reviewsResult.getTotalPages();
            mCurrentPage = reviewsResult.getPage();
        }
    }

    /**
     * If the review record is empty
     *
     * @return boolean
     */
    public boolean isEmpty() {
        return mReviews.isEmpty();
    }

    public void appendReviews(List<Review> reviews) {
        mReviews.addAll(reviews);
        mTotalReviewsFetched = mReviews.size();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mCurrentPage);
        dest.writeInt(mPagesAvailable);
        dest.writeInt(mTotalReviewsAvailable);
        dest.writeInt(mTotalReviewsFetched);
        dest.writeTypedList(mReviews);
    }

    @Override
    public int describeContents() {
        return hashCode();
    }


}
