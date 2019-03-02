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

public class ProductAdapter extends ArrayAdapter<ProductDetails> {

    // Resource ID for the Background color for this list of words.

    //private static final String LOG_TAG = GuideAdapter.class.getSimpleName();

    public ProductAdapter(Activity activity, ArrayList<ProductDetails> products) {
        super(activity, 0, products);
        }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {

        View itemListView = convertView;

        if (itemListView == null) {

            itemListView = LayoutInflater.from(getContext()).inflate(R.layout.details_activity, parent, false);
        }

        ProductDetails currentProductDetails = getItem(position);

        TextView productIdView = (TextView) itemListView.findViewById(R.id.sku);
        productIdView.setText(currentProductDetails.getProductId());

        TextView productNameView = (TextView) itemListView.findViewById(R.id.product_name_view);
        productNameView.setText(currentProductDetails.getProductName());

       // TextView productDescriptionView = (TextView) itemListView.findViewById(R.id.description_tv);
     //   productDescriptionView.setText(currentProductDetails.getProductDescription());

        TextView discountPriceView = (TextView) itemListView.findViewById(R.id.product_price_view);
        discountPriceView.setText(currentProductDetails.getDiscountPrice());

    //    ImageView productImageView = (ImageView) itemListView.findViewById(R.id.main_image);

        //   if (currentProduct.getImageUrl()) {
      //  if (currentProductDetails != null) {

       //     Glide.with(productImageView.getContext())
       //             .load(currentProductDetails.getImageUrl())
       //             .into(productImageView);
      //  }

        Button detailsButton = (Button) itemListView.findViewById(R.id.button_details_two);
        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v, position, 0);
                }
        });

        View textContainer = itemListView.findViewById(R.id.text_container);
        // FInd the color the resource ID maps to

        return itemListView;
        // return super.getView(position, convertView, parent);
    }
}
