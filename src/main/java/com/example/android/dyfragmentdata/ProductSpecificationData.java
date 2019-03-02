package com.example.android.dyfragmentdata;

public class ProductSpecificationData {

    // Variable declaration

    private String mSpecHeading;
    private String mSpecValue;

    public ProductSpecificationData(String SpecHeading, String SpecValue) {

        // Variable initial values

        mSpecHeading = SpecHeading;
        mSpecValue = SpecValue;
        }

    // Get method to return cart items

    public String getSpecHeading() {
        return  mSpecHeading;
    }

    public String getSpecValue() {
        return mSpecValue;
    }
}

