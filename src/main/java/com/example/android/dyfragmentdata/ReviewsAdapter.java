package com.example.android.dyfragmentdata;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ReviewsAdapter extends ArrayAdapter<ProductRatingsData> {

    public ReviewsAdapter(Context context, ArrayList<ProductRatingsData> specificationData) {
        super(context, 0, specificationData);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {

        View itemListView = convertView;

        if (itemListView == null) {

            itemListView = LayoutInflater.from(getContext()).inflate(R.layout.rating_reviews_items, parent, false);
        }

        ProductRatingsData currentProductRatingsData = getItem(position);

        ImageView userImageView = (ImageView) itemListView.findViewById(R.id.image_reviews);
        String imageUserReviewUrl = currentProductRatingsData.getFeaturedImage();

        if (!imageUserReviewUrl.isEmpty()) {
        Glide.with(userImageView.getContext())
                .load(imageUserReviewUrl)
                .into(userImageView);
        } else {
            userImageView.setVisibility(View.INVISIBLE);
        }

        TextView userNameView = (TextView) itemListView.findViewById(R.id.name_user);
        userNameView.setText(currentProductRatingsData.getUserName());

        TextView ratingView = (TextView) itemListView.findViewById(R.id.rating_user);
        ratingView.setText(currentProductRatingsData.getReview());

        ImageView ratingImageView = (ImageView) itemListView.findViewById(R.id.image_rating_two);

        String reviewString = currentProductRatingsData.getRating();

        if (!reviewString.isEmpty()) {
            int reviewRating = Integer.parseInt(currentProductRatingsData.getRating());
            switch(reviewRating) {
                case 1:
                    ratingImageView.setImageResource(R.drawable.one_star_rating);
                    break;
                case 2:
                    ratingImageView.setImageResource(R.drawable.two_star_rating);
                    break;
                case 3:
                    ratingImageView.setImageResource(R.drawable.three_star_rating);
                    break;
                case 4:
                    ratingImageView.setImageResource(R.drawable.four_star_rating);
                    break;
                case 5:
                    ratingImageView.setImageResource(R.drawable.five_star_rating);
                    break;
            }
        }

        View textContainer = itemListView.findViewById(R.id.text_container);

        return itemListView;
    }
}
