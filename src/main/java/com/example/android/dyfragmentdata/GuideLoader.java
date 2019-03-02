package com.example.android.dyfragmentdata;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class GuideLoader extends Loader<List<Guide>> {

    public GuideLoader(Context context) {
        super(context);
        // TODO: Finish implementing this constructor
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
    //    NewArrivalFragment templesFragment = new NewArrivalFragment();
     //  templesFragment.categoryNetworkRequest();
     //   RequestQueue queue = Volley.newRequestQueue(this);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

   // @Override
  // public List<Guide> loadInBackground() {
        // TODO: Implement this method
  //  }


    @Override
    public void deliverResult(@Nullable List<Guide> data) {
        super.deliverResult(data);


    }
}
