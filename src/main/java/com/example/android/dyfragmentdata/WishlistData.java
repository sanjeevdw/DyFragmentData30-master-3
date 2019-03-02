package com.example.android.dyfragmentdata;

public class WishlistData {

    // Variable declaration

    private String mProductId;
    private String mProductName;
    private String mProductPrice;
    private String mImageUrl;


    // created constructor with two parameters templeName and templeLocation

    public WishlistData(String ProductId, String ProductName, String ProductPrice, String ImageURL) {

        // Variable initial values

        mProductId = ProductId;
        mProductName = ProductName;
        mProductPrice = ProductPrice;
        mImageUrl = ImageURL;
        }

        // Get method to return temple name

    public String getProductId() {
        return  mProductId;
    }

    public String getProductName() {
        return mProductName;
    }

    // Get method to return temple location

    public String getProductPrice() {
        return mProductPrice;
    }

    public String getImageURL() {
        return  mImageUrl;
    }
}


