package com.example.android.dyfragmentdata;

public class ReviewOrderData {

    // Variable declaration

    private String mProductName;
    private String mProductQuantity;
    private String mProductPrice;
    private String mProductImage;

    public ReviewOrderData(String ProductName, String ProductQuantity, String ProductPrice, String ProductImage) {

        // Variable initial values
        mProductName = ProductName;
        mProductQuantity = ProductQuantity;
        mProductPrice = ProductPrice;
        mProductImage = ProductImage;
    }

    // Get method to return cart items
    public String getProductName() {
        return mProductName;
    }

    // Get method to return cart items

    public String getProductQuantity() {
        return mProductQuantity;
    }

    public String getProductPrice() {
        return  mProductPrice;
    }

    public String getProductImage() {
        return  mProductImage;
    }
}
