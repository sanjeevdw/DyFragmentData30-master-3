package com.example.android.dyfragmentdata;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SpecAdapter  extends ArrayAdapter<ProductSpecificationData> {

    public SpecAdapter(Context context, ArrayList<ProductSpecificationData> specificationData) {
        super(context, 0, specificationData);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {

        View itemListView = convertView;

        if (itemListView == null) {

            itemListView = LayoutInflater.from(getContext()).inflate(R.layout.details_spec_items, parent, false);
        }

        ProductSpecificationData currentSpecData = getItem(position);

        TextView specHeadingView = (TextView) itemListView.findViewById(R.id.spec_heading);
        specHeadingView.setText(currentSpecData.getSpecHeading());

        TextView specValueView = (TextView) itemListView.findViewById(R.id.spec_value);
        specValueView.setText(currentSpecData.getSpecValue());

        View textContainer = itemListView.findViewById(R.id.text_container);

        return itemListView;
    }
}

