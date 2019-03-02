package com.example.android.dyfragmentdata;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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

public class DealsOfTheDayAdapter  extends ArrayAdapter<DealsOfTheDay> {

// Resource ID for the Background color for this list of words.

        private int mColorResourceId;
        //private static final String LOG_TAG = GuideAdapter.class.getSimpleName();

    public DealsOfTheDayAdapter(Context context, ArrayList<DealsOfTheDay> dealsOfTheDay, int colorResourceId) {
        super(context, 0, dealsOfTheDay);
        mColorResourceId = colorResourceId;
    }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {

        View itemListView = convertView;

        if (itemListView == null) {

            itemListView = LayoutInflater.from(getContext()).inflate(R.layout.child_category_list_items, parent, false);
        }

            DealsOfTheDay currentDealsOfTheDay = getItem(position);

        TextView productIdView = (TextView) itemListView.findViewById(R.id.product_id);
        productIdView.setText(currentDealsOfTheDay.getProductId());
        productIdView.setVisibility(View.INVISIBLE);

        TextView nameTempleView = (TextView) itemListView.findViewById(R.id.name_view);
        nameTempleView.setText(currentDealsOfTheDay.getTempleName());

        TextView locationTempleView = (TextView) itemListView.findViewById(R.id.location_view);
        locationTempleView.setText(currentDealsOfTheDay.getTempleLocation());

        ImageView imageTempleView = (ImageView) itemListView.findViewById(R.id.image);

              if (currentDealsOfTheDay != null) {

            Glide.with(imageTempleView.getContext())
                    .load(currentDealsOfTheDay.getImageUrl())
                    .into(imageTempleView);
        }

        Button detailsButton = (Button) itemListView.findViewById(R.id.button_details_two);
        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v, position, 0);

                //   int position = getPosition();
                //  long id = getItemId(position);
                //  ((CatalogActivity) context).onButtonClick(id);
            }
        });

        TextView wishlistIdView = (TextView) itemListView.findViewById(R.id.wishlist_number);
        wishlistIdView.setText(currentDealsOfTheDay.getWishlist());
        wishlistIdView.setVisibility(View.INVISIBLE);

        ImageView wishlistImage = (ImageView) itemListView.findViewById(R.id.image_favorite);

        int userWishlist = Integer.parseInt(currentDealsOfTheDay.getWishlist());
        if (userWishlist == 1) {
            wishlistImage.setImageResource(R.drawable.red_wishlist);
        } else if (userWishlist == 0) {
            wishlistImage.setImageResource(R.drawable.favorite_icon);
        }

        wishlistImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v, position, 0);

                //   int position = getPosition();
                //  long id = getItemId(position);
                //  ((CatalogActivity) context).onButtonClick(id);
            }
        });

        ImageView ratingView = (ImageView) itemListView.findViewById(R.id.image_rating);
        int reviewRating = Integer.parseInt(currentDealsOfTheDay.getRating());
        switch(reviewRating) {
            case 1:
                ratingView.setImageResource(R.drawable.one_star_rating);
                break;
            case 2:
                ratingView.setImageResource(R.drawable.two_star_rating);
                break;
            case 3:
                ratingView.setImageResource(R.drawable.three_star_rating);
                break;
            case 4:
                ratingView.setImageResource(R.drawable.four_star_rating);
                break;
            case 5:
                ratingView.setImageResource(R.drawable.four_star_rating);
                break;
        }
        // Set the theme color for the list items.

        View textContainer = itemListView.findViewById(R.id.text_container);
        // FInd the color the resource ID maps to
        int color = ContextCompat.getColor(getContext(), mColorResourceId);
        // Set the background color of the text container view.
        textContainer.setBackgroundColor(color);

        return itemListView;
        // return super.getView(position, convertView, parent);
    }
}
