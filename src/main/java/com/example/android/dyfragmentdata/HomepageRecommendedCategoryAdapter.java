package com.example.android.dyfragmentdata;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class HomepageRecommendedCategoryAdapter extends ArrayAdapter<HomepageRecommendedCategoryData> {
    private ImageView gridImageView;
    private Context mContext;

    public HomepageRecommendedCategoryAdapter(Context context, ArrayList<HomepageRecommendedCategoryData> gridCategoriesTwo) {
        super(context, 0, gridCategoriesTwo);
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        View gridItemView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes

            gridItemView = LayoutInflater.from(getContext()).inflate(R.layout.grid_items, parent, false);

        } else {
            gridItemView = (View) convertView;
        }

        HomepageRecommendedCategoryData currentHomepageRecommendedCategoryData = getItem(position);

        gridImageView = (ImageView) gridItemView.findViewById(R.id.gridImageView);
        Glide.with(gridImageView.getContext())
                .load(currentHomepageRecommendedCategoryData.getGridCategoryImage())
                .into(gridImageView);
        gridImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GridView) parent).performItemClick(v, position, 0);
            }
        });

        TextView gridTextViewId = (TextView) gridItemView.findViewById(R.id.gridTextViewId);
        gridTextViewId.setText(currentHomepageRecommendedCategoryData.getGridCategoryId());
        gridTextViewId.setVisibility(View.INVISIBLE);

        TextView gridTextView = (TextView) gridItemView.findViewById(R.id.gridTextView);
        gridTextView.setText(currentHomepageRecommendedCategoryData.getGridCategoryName());
        gridTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GridView) parent).performItemClick(v, position, 0);

            }
        });

        return gridItemView;
    }
}
