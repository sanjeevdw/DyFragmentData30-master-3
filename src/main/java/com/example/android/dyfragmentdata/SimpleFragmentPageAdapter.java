package com.example.android.dyfragmentdata;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SimpleFragmentPageAdapter extends FragmentPagerAdapter {

    /**
     * {@link SimpleFragmentPageAdapter} is a {@link FragmentPagerAdapter} that can provide layout for each list item based on a data source which is a list of (@link temples) objects.
     */
    // context of the app

        private Context mContext;
        private Drawable myDrawable;

        /* Create a new {@link SimpleFragmentPageAdapter} object.
         * @param context is the context of the app
         * @param fm is the fragment manager that will keep each fragment's
         * state in the adapter across swipes.
         */

        public SimpleFragmentPageAdapter(Context context, FragmentManager fm) {
            // Required empty public constructor
            super(fm);
            mContext = context;
        }

        /* Return the {@link Fragment} that should be displayed for the given page number.

         */
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new NewArrivalFragment();
            }

            else if(position == 1) {
                return new MostPopularFragment();
            }

            else  {
                return new NewArrivalFragment();
            }

        }

        @Override
        public int getCount() {
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return mContext.getString(R.string.new_arrivals);
            }

            else if(position == 1) {
                return mContext.getString(R.string.most_popular);
            }

            else  {
                return mContext.getString(R.string.top_merchant);
            }
        }
    }

