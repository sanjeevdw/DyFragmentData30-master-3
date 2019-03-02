package com.example.android.dyfragmentdata;

public class DealsOfTheDay {

    // Variable declaration
    private String mProductId;
    private String mTempleName;
    private String mTempleLocation;
    private String mRating;
    private int mImageResourceId = NO_IMAGE_PROVIDED;
    private static final int NO_IMAGE_PROVIDED = 1;
    private String mImageUrl;
    private String mWishlist;
    // created constructor with two parameters templeName and templeLocation


    public DealsOfTheDay(String productId, String templeName, String templeLocation, String mImageUrl, String rating, String wishlist) {

        // Variable initial values
        mProductId = productId;
        mTempleName = templeName;
        mTempleLocation = templeLocation;
        this.mImageUrl = mImageUrl;
        mRating = rating;
        mWishlist = wishlist;
    }

    // Get method to return temple name

    public String getProductId() {
        return  mProductId;
    }

    public String getTempleName() {
        return mTempleName;
    }

    // Get method to return temple location

    public String getTempleLocation() {
        return mTempleLocation;
    }

    public String getImageUrl() {
        return  mImageUrl;
    }

    public String getRating() {
        return  mRating;
    }

    public String getWishlist() {
        return  mWishlist;
    }
}

