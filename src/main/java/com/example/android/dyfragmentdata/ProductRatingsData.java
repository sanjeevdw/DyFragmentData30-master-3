package com.example.android.dyfragmentdata;

public class ProductRatingsData {

    // Variable declaration

    private String mUserName;
    private String mRating;
    private String mReview;
    private String mFeaturedImage;

    public ProductRatingsData(String FeaturedImage, String UserName, String Rating, String Review) {

        // Variable initial values
        mFeaturedImage = FeaturedImage;
        mUserName = UserName;
        mRating = Rating;
        mReview = Review;

    }

    // Get method to return reviews items

    public String getFeaturedImage() {
        return mFeaturedImage;
    }

    public String getUserName() {
        return  mUserName;
    }

    public String getRating() {
        return mRating;
    }

    public String getReview() {
        return mReview;
    }

}

