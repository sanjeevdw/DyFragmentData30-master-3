package com.example.android.dyfragmentdata;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ViewPagerApdater extends PagerAdapter {

    SliderData sliderData;
    ArrayList<SliderData> sliderDataItems = new ArrayList<>();
    private Context context;
    private Integer [] images = {R.drawable.banner_five, R.drawable.banner_two,
            R.drawable.banner_seven};

    public ViewPagerApdater(Context context, ArrayList<SliderData> sliderDataItems) {
        this.context = context;
        this.sliderDataItems = sliderDataItems;
    }

    @Override
    public int getCount() {
        return sliderDataItems.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

  /*  @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    } */

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_layout, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
      //  int itemPosition = getItemPosition(position);
        sliderData = sliderDataItems.get(position);
        // imageView.setImageResource(images[position]);
     //   imageView.setImageResource(images[position]);
        Glide.with(imageView.getContext())
                     .load(sliderData.getImageUrl())
                     .into(imageView);
        CustomViewPager viewPager = (CustomViewPager) container;
        viewPager.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        CustomViewPager viewPager = (CustomViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}

