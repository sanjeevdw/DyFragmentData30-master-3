package com.example.android.dyfragmentdata;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MostPopularFragment extends Fragment

    {
        private ArrayList<ArrivalNew> arrivalNew;
        private View rootView;
        private GuideAdapterNew adapter;
        private ExpandableHeightGridView gridView;
        private String sessionToken;
        private int childIndex;
        private static final int GUIDE_LOADER_ID = 1;

        // private String productID;

        /**
         * A simple {@link Fragment} subclass.
         */

    public MostPopularFragment() {
        // Required empty public constructor
    }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.guide_list_new, container, false);
        arrivalNew = new ArrayList<ArrivalNew>();
        categoryNetworkRequest();

        //  TextView productIdtv = (TextView) listView.findViewById(R.id.product_id);
        //   productIdtv.setVisibility(View.GONE);

        Session session = new Session(getActivity().getApplicationContext());
        sessionToken = session.getusertoken();

        return rootView;
    }

        public View getViewByPosition(int pos, GridView gridView) {
        final int firstListItemPosition = gridView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + gridView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return gridView.getAdapter().getView(pos, null, gridView);
        } else {
            childIndex = pos - firstListItemPosition;
            return gridView.getChildAt(childIndex);
        }
    }

        public void categoryNetworkRequest() {
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String url = "https://www.godprice.com/api/home_product.php?trending_product=yes&userid="+sessionToken;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Create an empty ArrayList that we can start adding earthquakes to
                        // List<Guide> guideList = new ArrayList<>();
                        //    temples = new ArrayList<Guide>();
                        // Try to parse the JSON response string. If there's a problem with the way the JSON
                        // is formatted, a JSONException exception object will be thrown.
                        // Catch the exception so the app doesn't crash, and print the error message to the logs.

                        try {

                            String trimResponse = response.substring(3);
                            String trimmedResponse = trimResponse.trim();
                            JSONObject jsonObject = new JSONObject(trimmedResponse);
                            JSONArray data = jsonObject.getJSONArray("data");
                            if (data.length() > 0) {
                                //Loop the Array
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject currentObject = data.getJSONObject(i);
                                    JSONArray currentProductDetail = currentObject.getJSONArray("product_detail");

                                    for (int j = 0; j < currentProductDetail.length(); j++) {
                                        //   JSONArray productDetail = new JSONArray("product_detail");
                                        Log.e("Message", "loop");
                                        HashMap<String, String> map = new HashMap<String, String>();
                                        JSONObject e = currentProductDetail.getJSONObject(j);

                                        String prodID = e.getString("product_id");
                                        String productName = e.getString("productsname");
                                        String productPrice = e.getString("price");
                                        String imageUrl = e.getString("feature_image");
                                        String productRating = e.getString("rating");
                                        String productWishlist = e.getString("is_whishlit");

                                        String PriceDollar = getResources().getString(R.string.singapore_price_dollar_detail) + productPrice;

                                        ArrivalNew currentArrivalNew = new ArrivalNew(prodID, productName, PriceDollar, imageUrl);
                                        arrivalNew.add(currentArrivalNew);

                                        adapter = new GuideAdapterNew(getActivity(), arrivalNew);
                                        //  adapter = new GuideAdapter(getContext(), temples, R.color.temples_category);
                                        gridView = (ExpandableHeightGridView) rootView.findViewById(R.id.grid);
                                        gridView.setChoiceMode(android.widget.GridView.CHOICE_MODE_MULTIPLE_MODAL);
                                        gridView.setAdapter(adapter);
                                        gridView.setExpanded(true);
                                        adapter.notifyDataSetChanged();
                                        gridView.setOnTouchListener(new View.OnTouchListener(){ @Override public boolean onTouch(View v, MotionEvent event) { if(event.getAction() == MotionEvent.ACTION_MOVE){ return true; } return false; } });
                                        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                long viewId = view.getId();
                                                getViewByPosition(position,gridView);

                                                if (viewId == R.id.gridImageView) {
                                                    String productId = gridView.getItemAtPosition(position).toString().trim();
                                                    //     TextView Pid = (TextView) parent.findViewById(R.id.product_id);
                                                    //    TextView PPid = (TextView) listView.getChildAt(position).findViewById(R.id.product_id);
                                                    TextView PPid = (TextView) gridView.getChildAt(childIndex).findViewById(R.id.gridTextViewId);
                                                    String productID = PPid.getText().toString().trim();

                                                    Intent intent = new Intent(getActivity().getApplicationContext(), DetailsActivity.class);
                                                    intent.putExtra("ProductId", productID);
                                                    startActivity(intent);
                                                }
                                            }
                                        });

                                    }
                                }
                            }
                        } catch (JSONException e) {
                            // If an error is thrown when executing any of the above statements in the "try" block,
                            // catch the exception here, so the app doesn't crash. Print a log message
                            // with the message from the exception.
                            //     Log.e("Volley", "Problem parsing the category JSON results", e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(getActivity().getApplicationContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }
    }

