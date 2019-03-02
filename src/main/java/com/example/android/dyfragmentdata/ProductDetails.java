package com.example.android.dyfragmentdata;

public class ProductDetails {

    // Variable declaration

    private String mProductId;
    private String mProductName;
    private String mProductDescription;
    private String mDiscountPrice;
    private String mImageUrl;
    private String mRating;
    private String mWishlist;
    private String mSku;
    private String mAttributeName;
    private String mAttributeValue;

    // created constructor with two parameters templeName and templeLocation

    public ProductDetails(String productId, String productName, String productDescription, String discountPrice, String mImageUrl, String wishlist, String sku, String attributeName, String attributeValue) {

        // Variable initial values

        mProductId = productId;
        mProductName = productName;
        mProductDescription = productDescription;
        mDiscountPrice = discountPrice;
        this.mImageUrl = mImageUrl;
        mSku = sku;
        mAttributeName = attributeName;
        mAttributeValue = attributeValue;
        mWishlist = wishlist;
        }

    // Get method to return temple name

    public String getProductId() {
        return  mProductId;
    }

    public String getProductName() {
        return mProductName;
    }

    // Get method to return temple location

    public String getProductDescription() {
       return mProductDescription;
    }

    public String getDiscountPrice() {
        return mDiscountPrice;
    }

    public String getWishlist() {
        return  mWishlist;
    }

    public String getImageUrl() {
        return  mImageUrl;
    }

}

