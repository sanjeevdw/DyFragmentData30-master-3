package com.example.android.dyfragmentdata;

public class ArrivalNew {

    // Variable declaration
    private String mProductId;
    private String mProductName;
    private String mProductPrice;
  //  private String mRating;
  //  private int mImageResourceId = NO_IMAGE_PROVIDED;
  //  private static final int NO_IMAGE_PROVIDED = 1;
    private String mImageUrl;
   // private String mWishlist;

    public ArrivalNew(String productId, String productName, String productPrice, String mImageUrl) {

        // Variable initial values

        mProductId = productId;
        mProductName = productName;
        mProductPrice = productPrice;
        this.mImageUrl = mImageUrl;

    }

    // Get method to return product details

    public String getProductId() {
        return  mProductId;
    }

    public String getProductName() {
        return mProductName;
    }

    public String getProductPrice() {
        return mProductPrice;
    }

    public String getImageUrl() {
        return  mImageUrl;
    }

}
