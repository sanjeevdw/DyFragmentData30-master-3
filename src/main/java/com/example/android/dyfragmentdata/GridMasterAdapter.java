package com.example.android.dyfragmentdata;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GridMasterAdapter extends ArrayAdapter<MasterCategory> {

    private ImageView gridImageView;
    private Context mContext;

    public GridMasterAdapter(Context context, ArrayList<MasterCategory> gridMasterCategories) {
        super(context, 0, gridMasterCategories);
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        View gridItemView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes

            gridItemView = LayoutInflater.from(getContext()).inflate(R.layout.master_category_grid_items, parent, false);

        } else {
            gridItemView = (View) convertView;
        }

        MasterCategory currentMasterCategory = getItem(position);

        gridImageView = (ImageView) gridItemView.findViewById(R.id.gridImageView);
        Glide.with(gridImageView.getContext())
                .load(currentMasterCategory.getGridMasterCategoryImage())
                .into(gridImageView);


        TextView gridTextViewId = (TextView) gridItemView.findViewById(R.id.gridTextViewId);
        gridTextViewId.setText(currentMasterCategory.getGridMasterCategoryId());
        gridTextViewId.setVisibility(View.INVISIBLE);

        TextView gridTextView = (TextView) gridItemView.findViewById(R.id.gridTextView);
        gridTextView.setText(currentMasterCategory.getGridMasterCategoryName());

        Button viewProductsButton = (Button) gridItemView.findViewById(R.id.view_products_button);
        viewProductsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GridView) parent).performItemClick(v, position, 0);

            }
        });

        return gridItemView;
    }
}
