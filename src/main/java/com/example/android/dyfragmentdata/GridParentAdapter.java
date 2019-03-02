package com.example.android.dyfragmentdata;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GridParentAdapter extends ArrayAdapter<ParentCategory> {

    private ImageView gridImageView;
    private Context mContext;

    public GridParentAdapter(Context context, ArrayList<ParentCategory> gridParentCategories) {
        super(context, 0, gridParentCategories);
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        View gridItemView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes

            gridItemView = LayoutInflater.from(getContext()).inflate(R.layout.parent_category_grid_items, parent, false);

        } else {
            gridItemView = (View) convertView;
        }

        ParentCategory currentParentCategory = getItem(position);

        TextView gridTextViewParentId = (TextView) gridItemView.findViewById(R.id.gridTextViewParentId);
        gridTextViewParentId.setText(currentParentCategory.getGridParentProductId());
        gridTextViewParentId.setVisibility(View.INVISIBLE);

        TextView gridTextViewCategoryId = (TextView) gridItemView.findViewById(R.id.gridTextViewCategoryId);
        gridTextViewCategoryId.setText(currentParentCategory.getGridParentCategoryId());
        gridTextViewCategoryId.setVisibility(View.INVISIBLE);

        TextView gridTextView = (TextView) gridItemView.findViewById(R.id.gridTextView);
        gridTextView.setText(currentParentCategory.getGridParentCategoryName());

        ImageView imageTempleView = (ImageView) gridItemView.findViewById(R.id.gridImageView);

        if (currentParentCategory != null) {

            Glide.with(imageTempleView.getContext())
                    .load(currentParentCategory.getGridParentCategoryImage())
                    .into(imageTempleView);
        }

        Button viewProductsButton = (Button) gridItemView.findViewById(R.id.parent_view_products);
        viewProductsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GridView) parent).performItemClick(v, position, 0);
                }
        });

        return gridItemView;
    }
}
