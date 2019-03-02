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

public class GuideAdapterNew extends ArrayAdapter<ArrivalNew> {
    private ImageView gridImageView;
    private Context mContext;

    public GuideAdapterNew(Context context, ArrayList<ArrivalNew> arrivalNewProducts) {
        super(context, 0, arrivalNewProducts);
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        View gridItemView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes

            gridItemView = LayoutInflater.from(getContext()).inflate(R.layout.grid_items_new, parent, false);

        } else {
            gridItemView = (View) convertView;
        }

        ArrivalNew arrivalNewProducts = getItem(position);

        gridImageView = (ImageView) gridItemView.findViewById(R.id.gridImageView);
        Glide.with(gridImageView.getContext())
                .load(arrivalNewProducts.getImageUrl())
                .into(gridImageView);
        gridImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GridView) parent).performItemClick(v, position, 0);
            }
        });

        TextView gridTextViewId = (TextView) gridItemView.findViewById(R.id.gridTextViewId);
        gridTextViewId.setText(arrivalNewProducts.getProductId());
        gridTextViewId.setVisibility(View.INVISIBLE);

        TextView gridTextView = (TextView) gridItemView.findViewById(R.id.gridTextView);
        gridTextView.setText(arrivalNewProducts.getProductName());
        gridTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GridView) parent).performItemClick(v, position, 0);

            }
        });

        TextView gridTextViewPrice = (TextView) gridItemView.findViewById(R.id.gridTextViewPrice);
        gridTextViewPrice.setText(arrivalNewProducts.getProductPrice());

        return gridItemView;
    }
}
