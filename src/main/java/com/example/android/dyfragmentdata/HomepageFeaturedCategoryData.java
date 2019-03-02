package com.example.android.dyfragmentdata;

public class HomepageFeaturedCategoryData {

    private String mGridCategoryId;
    private String mGridCategoryName;
    private String mGridCategoryImage;

    public HomepageFeaturedCategoryData(String GridCategoryId, String GridCategoryName, String GridCategoryImage) {

        mGridCategoryId = GridCategoryId;
        mGridCategoryName = GridCategoryName;
        mGridCategoryImage = GridCategoryImage;
    }

    public String getGridCategoryId() {
        return mGridCategoryId;
    }

    public String getGridCategoryName() {
        return mGridCategoryName;
    }

    public String getGridCategoryImage() {
        return mGridCategoryImage;
    }
}
