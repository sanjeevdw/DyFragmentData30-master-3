package com.example.android.dyfragmentdata;

public class ParentCategory {

    private String mGridParentProductId;
    private String mGridParentCategoryId;
    private String mGridParentCategoryName;
    private String mGridParentCategoryImage;

    public ParentCategory(String GridParentProductId, String GridParentCategoryId, String GridParentCategoryName, String GridParentCategoryImage) {

        mGridParentProductId = GridParentProductId;
        mGridParentCategoryId = GridParentCategoryId;
        mGridParentCategoryName = GridParentCategoryName;
        mGridParentCategoryImage = GridParentCategoryImage;
    }

    public String getGridParentProductId() {
        return mGridParentProductId;
    }

    public String getGridParentCategoryId() {
        return mGridParentCategoryId;
    }

    public String getGridParentCategoryName() {
        return mGridParentCategoryName;
    }

    public String getGridParentCategoryImage() {
        return mGridParentCategoryImage;
    }
}


