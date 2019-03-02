package com.example.android.dyfragmentdata;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ReviewAdapter extends ArrayAdapter<ReviewOrderData>  {

    public ReviewAdapter(Context context, ArrayList<ReviewOrderData> reviewOrderItems) {
        super(context, 0, reviewOrderItems);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {

        View itemListView = convertView;

        if (itemListView == null) {

            itemListView = LayoutInflater.from(getContext()).inflate(R.layout.review_order_items, parent, false);
        }

        ReviewOrderData currentReviewOrderData = getItem(position);

        TextView productNameView = (TextView) itemListView.findViewById(R.id.textView_product_title);
        productNameView.setText(currentReviewOrderData.getProductName());

        TextView priceView = (TextView) itemListView.findViewById(R.id.textView_product_price);
        priceView.setText(currentReviewOrderData.getProductPrice());

        TextView quantityView = (TextView) itemListView.findViewById(R.id.TextView_Quantity);
        quantityView.setText(getContext().getString(R.string.quantity_text) + currentReviewOrderData.getProductQuantity());

        ImageView productImageView = (ImageView) itemListView.findViewById(R.id.imageView_product);

        if (currentReviewOrderData != null) {
            Glide.with(productImageView.getContext())
                    .load(currentReviewOrderData.getProductImage())
                    .into(productImageView);
        }

        View textContainer = itemListView.findViewById(R.id.text_container);

        return itemListView;
    }
}
