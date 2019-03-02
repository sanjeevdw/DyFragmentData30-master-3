package com.example.android.dyfragmentdata;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class WishlistAdapter extends ArrayAdapter<WishlistData> {

    // Resource ID for the Background color for this list of words.

    //private static final String LOG_TAG = GuideAdapter.class.getSimpleName();

    public WishlistAdapter(Activity activity, ArrayList<WishlistData> wishlistProducts) {
        super(activity, 0, wishlistProducts);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {

        View itemListView = convertView;

        if (itemListView == null) {

            itemListView = LayoutInflater.from(getContext()).inflate(R.layout.wishlist_list_item, parent, false);
        }

        WishlistData currentWishlistDetails = getItem(position);

        TextView productIdView = (TextView) itemListView.findViewById(R.id.textView_product_id);
        productIdView.setText(currentWishlistDetails.getProductId());
        productIdView.setVisibility(View.INVISIBLE);

        TextView productNameView = (TextView) itemListView.findViewById(R.id.textView_product_title);
        productNameView.setText(currentWishlistDetails.getProductName());
        productNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v, position, 0);

            }
        });

        TextView discountPriceView = (TextView) itemListView.findViewById(R.id.textView_product_price);
        discountPriceView.setText(currentWishlistDetails.getProductPrice());

            ImageView productImageView = (ImageView) itemListView.findViewById(R.id.imageView_product);

            if (currentWishlistDetails != null) {

             Glide.with(productImageView.getContext())
                     .load(currentWishlistDetails.getImageURL())
                     .into(productImageView);
          }

          Button removeWishlistButton = (Button) itemListView.findViewById(R.id.remove_product_icon);
        removeWishlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v, position, 0);

            }
        });

          return itemListView;
        // return super.getView(position, convertView, parent);
    }
}

