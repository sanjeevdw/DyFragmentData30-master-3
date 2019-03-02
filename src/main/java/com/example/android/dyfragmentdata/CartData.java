package com.example.android.dyfragmentdata;

public class CartData {

    // Variable declaration

    private String mProductId;
    private String mCartId;
    private String mProductName;
    private String mProductQuantity;
    private String mProductPrice;
    private String mProductImage;

    public CartData(String ProductId, String CartId, String ProductName, String ProductQuantity, String ProductPrice, String ProductImage) {

        // Variable initial values

        mProductId = ProductId;
        mCartId = CartId;
        mProductName = ProductName;
        mProductQuantity = ProductQuantity;
        mProductPrice = ProductPrice;
        mProductImage = ProductImage;
    }

    // Get method to return cart items

    public String getProductId() {
        return  mProductId;
    }

    public String getCartId() {
        return  mCartId;
    }

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

