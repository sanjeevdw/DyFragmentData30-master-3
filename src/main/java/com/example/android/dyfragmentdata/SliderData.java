package com.example.android.dyfragmentdata;

public class SliderData {

    // Variable declaration
    private String mSliderId;
    private String mSliderTitle;
    private String mSliderDescription;
    private String mImageUrl;

    // created constructor with four parameters

    public SliderData(String SliderId, String SliderTitle, String SliderDescription, String mImageUrl) {

        // Variable initial values
        mSliderId = SliderId;
        mSliderTitle = SliderTitle;
        mSliderDescription = SliderDescription;
        this.mImageUrl = mImageUrl;
        }

        // Get method to return data

    public String getSliderId() {
        return  mSliderId;
    }

    public String getSliderTitle() {
        return mSliderTitle;
    }

    // Get method to return data

    public String getSliderDescription() {
        return mSliderDescription;
    }

    public String getImageUrl() {
        return  mImageUrl;
    }
}

