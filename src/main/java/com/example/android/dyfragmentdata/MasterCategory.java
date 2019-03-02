package com.example.android.dyfragmentdata;

public class MasterCategory {

    private String mGridMasterCategoryId;
    private String mGridMasterCategoryName;
    private String mGridMasterCategoryImage;

    public MasterCategory(String GridMasterCategoryId, String GridMasterCategoryName, String GridMasterCategoryImage) {

        mGridMasterCategoryId = GridMasterCategoryId;
        mGridMasterCategoryName = GridMasterCategoryName;
        mGridMasterCategoryImage = GridMasterCategoryImage;
    }

    public String getGridMasterCategoryId() {
        return mGridMasterCategoryId;
    }

    public String getGridMasterCategoryName() {
        return mGridMasterCategoryName;
    }

    public String getGridMasterCategoryImage() {
        return mGridMasterCategoryImage;
    }
}

