package com.example.android.dyfragmentdata;

import android.app.Activity;
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

public class CartAdapter extends ArrayAdapter<CartData> {

    public CartAdapter(Context context, ArrayList<CartData> cartItems) {
        super(context, 0, cartItems);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {

        View itemListView = convertView;

        if (itemListView == null) {

            itemListView = LayoutInflater.from(getContext()).inflate(R.layout.cart_list_items, parent, false);
        }

        CartData currentCartData = getItem(position);

        TextView productNameView = (TextView) itemListView.findViewById(R.id.textView_product_title);
        productNameView.setText(currentCartData.getProductName());

        // TextView productDescriptionView = (TextView) itemListView.findViewById(R.id.description_tv);
        //   productDescriptionView.setText(currentProductDetails.getProductDescription());

        TextView priceView = (TextView) itemListView.findViewById(R.id.textView_product_price);
        priceView.setText(currentCartData.getProductPrice());

        TextView cartId = (TextView) itemListView.findViewById(R.id.cart_id);
        cartId.setText(currentCartData.getCartId());
        cartId.setVisibility(View.INVISIBLE);

        EditText quantityView = (EditText) itemListView.findViewById(R.id.editText_Quantity);
        quantityView.setText(currentCartData.getProductQuantity());

        ImageView productImageView = (ImageView) itemListView.findViewById(R.id.imageView_product);

            if (currentCartData != null) {
                Glide.with(productImageView.getContext())
                     .load(currentCartData.getProductImage())
                     .into(productImageView);
          }

        Button updateButton = (Button) itemListView.findViewById(R.id.button_update);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v, position, 0);
               // int position = getPosition();
              //   long id = getItemId(position);
                //  ((CatalogActivity) context).onButtonClick(id);
            }
        });

        Button deleteButton = (Button) itemListView.findViewById(R.id.button_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v, position, 0);
                // int position = getPosition();
                //   long id = getItemId(position);
                //  ((CatalogActivity) context).onButtonClick(id);
            }
        });

          View textContainer = itemListView.findViewById(R.id.text_container);

          return itemListView;
        }
}
