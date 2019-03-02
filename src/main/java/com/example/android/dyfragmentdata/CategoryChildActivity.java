package com.example.android.dyfragmentdata;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoryChildActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private Session session;
    private String sessionToken;
    private ArrayList<ParentCategory> gridParentCategories;
    private ParentCategory parentCategory;
    private GridParentAdapter gridParentAdapter;
    private String sessionUserName;
    private String sessionUserEmail;
    private String mCid;
    private String mPid;
    private int childIndex;
    private ListView listView;
    private ArrayList<Guide> temples;
    private GuideAdapter adapter;
    private String homepageUserSearchQuery;
    private int index;
    private int top;
    private String sessionUserImage;
    private String sessionUserWalletAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_category);

        Intent masterCatetoryIdIntent = getIntent();
        Intent parentCategoryIdIntent = getIntent();
        Bundle bundle = masterCatetoryIdIntent.getExtras();
        Bundle bundleParent = parentCategoryIdIntent.getExtras();

        if (bundle != null) {
            mCid = (String) bundle.get("masterCategoryId");
            }

        if (bundleParent != null) {
            mPid = (String) bundleParent.get("parentCategoryId");
        }

        Intent homepageUserSearchIdIntent = getIntent();
        Bundle homepageUserSearch = homepageUserSearchIdIntent.getExtras();

        if (homepageUserSearch != null) {
            homepageUserSearchQuery = (String) homepageUserSearch.get("userSearchQuery");
        }

        session = new Session(this);
        sessionToken = session.getusertoken();

        if (mPid == null) {
            setHomepageUserSearchQueryNetworkRequest();
        } else {
            categoryChildNetworkRequest();
        }

        setNavigationViewListener();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#e53935"));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        ActionBar actionbar = getSupportActionBar();

        if (actionbar !=null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        toolbar.findViewById(R.id.toolbar_title);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoryChildActivity.this, HomepageActivity.class);
                startActivity(intent);
            }
        });

        mDrawerLayout = findViewById(R.id.drawer_layout);

        if (sessionToken.isEmpty()) {
            navigationView = findViewById(R.id.nav_view);
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.drawer_view_without_login);
            View header = navigationView.getHeaderView(0);
            TextView loggedInUserName = header.findViewById(R.id.header_username_tv);
            TextView loggedInUserEmail = header.findViewById(R.id.email_address_tv);
            loggedInUserName.setText(R.string.header_name);
            loggedInUserEmail.setVisibility(View.GONE);
        }

        if (!sessionToken.isEmpty()) {
            sessionUserImage = session.getuserImage();
            if (!sessionUserImage.isEmpty()) {
                navigationView = findViewById(R.id.nav_view);
                navigationView.inflateMenu(R.menu.drawer_view);
                View header = navigationView.getHeaderView(0);
                ImageView loggedInUserImage = header.findViewById(R.id.user_image_header);
                Glide.with(loggedInUserImage.getContext())
                        .load(sessionUserImage)
                        .into(loggedInUserImage);
            }
            showFullNavItem();
        }

        temples = new ArrayList<Guide>();

      /*  Button chooseFilterButton = (Button) findViewById(R.id.choose_filter);
        chooseFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoryChildActivity.this, FilterActivity.class);
                startActivity(intent);
            }
        }); */
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    private void showFullNavItem() {
        navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.drawer_view);
        View header = navigationView.getHeaderView(0);
        TextView loggedInUserName = header.findViewById(R.id.header_username_tv);
        TextView loggedInUserEmail = header.findViewById(R.id.email_address_tv);
        sessionUserName = session.getusename();
        sessionUserEmail = session.getUserEmail();
        loggedInUserName.setText(sessionUserName);
        loggedInUserEmail.setText(sessionUserEmail);
        sessionUserWalletAmount = session.getuserWalletAmount();
        navigationView = findViewById(R.id.nav_view);
        String WalletPriceDollar = getResources().getString(R.string.wallet_amount_label) + " " + getResources().getString(R.string.price_dollar_detail) + sessionUserWalletAmount;
        TextView loggedInUserWalletAmount = header.findViewById(R.id.wallet_amount_header);
        if (!sessionUserWalletAmount.isEmpty()) {
            loggedInUserWalletAmount.setText(WalletPriceDollar);
        }


    }

    // NavigationView click events
    private void setNavigationViewListener() {
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
    /*        case_tab R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true; */
            case R.id.action_drawer_signin:
                if (!sessionToken.isEmpty()) {
                    Intent intentUpdateProfile = new Intent(this, ProfileActivity.class);
                    startActivity(intentUpdateProfile);

                } else {
                    Intent intent = new Intent(this, SignupActivity.class);
                    startActivity(intent);
                }
                return true;
            case R.id.action_drawer_cart:
                Intent intentCart = new Intent(this, CartActivity.class);
                startActivity(intentCart);
                return true;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                if (sessionToken.isEmpty()) {
                    navigationView = findViewById(R.id.nav_view);
                    navigationView.getMenu().clear();
                    navigationView.inflateMenu(R.menu.drawer_view_without_login);
                } else {
                    showFullNavItem();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        // set item as selected to persist highlight
        item.setChecked(true);

        // close drawer when item is tapped
        mDrawerLayout.closeDrawers();

        switch(id) {

            case R.id.nav_home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_master_category:
                Intent intentMasterCategory = new Intent(this, MasterCategoryActivity.class);
                startActivity(intentMasterCategory);
                break;
                case R.id.nav_login:
                Intent intentLogin = new Intent(this, LoginActivity.class);
                startActivity(intentLogin);
                break;
            case R.id.nav_register:
                Intent intentRegister = new Intent(this, SignupActivity.class);
                startActivity(intentRegister);
                break;

            case R.id.nav_profile:
                Intent intentProfile = new Intent(this, ProfileActivity.class);
                startActivity(intentProfile);
                break;
            case R.id.nav_forgot_password:
                Intent intentForgotPassword = new Intent(this, ForgotPasswordActivity.class);
                startActivity(intentForgotPassword);
                break;
                case R.id.nav_change_password:
                Intent intentChangePassword = new Intent(this, ChangePasswordActivity.class);
                startActivity(intentChangePassword);
                break;
            case R.id.nav_wishlist:
                Intent intentWishlist = new Intent(this, WishlistActivity.class);
                startActivity(intentWishlist);
                break;
                case R.id.nav_checkout:
                Intent intentCheckout = new Intent(this, CheckoutActivity.class);
                startActivity(intentCheckout);
                break;
            case R.id.nav_order_history:
                Intent intentOrderHistory = new Intent(this, OrderHistoryListingActivity.class);
                startActivity(intentOrderHistory);
                break;

            case R.id.nav_merchant_login:
                Intent intentMechantLogin = new Intent(this, MerchantLoginActivity.class);
                startActivity(intentMechantLogin);
                break;
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show();
                sessionToken = "";
                session.setusertoken("");
                session.setUserEmail("");
                session.setusename("");
                session.setuserImage("");
                if (sessionToken.isEmpty()) {
                    navigationView = findViewById(R.id.nav_view);
                    navigationView.getMenu().clear();
                    navigationView.inflateMenu(R.menu.drawer_view_without_login);
                    View header = navigationView.getHeaderView(0);
                    TextView loggedInUserName = header.findViewById(R.id.header_username_tv);
                    TextView loggedInUserEmail = header.findViewById(R.id.email_address_tv);
                    loggedInUserName.setText(R.string.header_name);
                    loggedInUserEmail.setVisibility(View.GONE);
                }
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                Intent intentCart = new Intent(this, LoginActivity.class);
                startActivity(intentCart);
                break;
        }
        return false;
    }

    public void categoryChildNetworkRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.godprice.com/api/product_list.php?m_cid="+mCid+"&p_cid="+mPid+"&userid="+sessionToken;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String trimResponse = response.substring(3);
                            String trimmedResponse = trimResponse.trim();
                            JSONObject jsonObject = new JSONObject(trimmedResponse);
                            String status = jsonObject.getString("status");
                            int statusInt = Integer.parseInt(status);
                            if (statusInt == 200) {
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
                                            String productPriceDollar = getResources().getString(R.string.price_dollar_detail) + productPrice;
                                            String imageUrl = e.getString("feature_image");
                                            String productRating = e.getString("rating");
                                            String productWishlist = e.getString("is_whishlit");

                                            Guide currentGuide = new Guide(prodID, productName, productPriceDollar, imageUrl, productRating, productWishlist);
                                            temples.add(currentGuide);

                                            adapter = new GuideAdapter(CategoryChildActivity.this, temples, R.color.temples_category);
                                            listView = (ListView) findViewById(R.id.child_category_list);
                                            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
                                            listView.setAdapter(adapter);
                                            adapter.notifyDataSetChanged();

                                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    long viewId = view.getId();
                                                    getViewByPosition(position, listView);

                                                    if (viewId == R.id.button_details_two) {
                                                        String productId = listView.getItemAtPosition(position).toString().trim();
                                                        //     TextView Pid = (TextView) parent.findViewById(R.id.product_id);
                                                        //    TextView PPid = (TextView) listView.getChildAt(position).findViewById(R.id.product_id);
                                                        TextView PPid = (TextView) listView.getChildAt(childIndex).findViewById(R.id.product_id);
                                                        String productID = PPid.getText().toString().trim();

                                                        Intent intent = new Intent(CategoryChildActivity.this, DetailsActivity.class);
                                                        intent.putExtra("ProductId", productID);
                                                        intent.putExtra("masterCategoryId", mCid);
                                                        intent.putExtra("parentCategoryId", mPid);
                                                        startActivity(intent);
                                                    } else if (viewId == R.id.image_favorite) {
                                                        if (sessionToken.isEmpty()) {
                                                            Intent intentLoginforWishlist = new Intent(CategoryChildActivity.this, LoginActivity.class);
                                                            startActivity(intentLoginforWishlist);
                                                        } else {
                                                            String productId = listView.getItemAtPosition(position).toString().trim();
                                                            TextView PPid = (TextView) listView.getChildAt(childIndex).findViewById(R.id.product_id);
                                                            String productID = PPid.getText().toString().trim();
                                                            TextView wishlistId = (TextView) listView.getChildAt(childIndex).findViewById(R.id.wishlist_number);
                                                            String wishlistID = wishlistId.getText().toString().trim();
                                                            int wishlistInt = Integer.parseInt(wishlistID);
                                                            if (wishlistInt == 0) {
                                                                sendWishlistRequest(productID, sessionToken);
                                                            } else {
                                                                wishlistProductRemoveRequest(productID, sessionToken);
                                                            }

                                                           index = listView.getFirstVisiblePosition();
                                                            View v = listView.getChildAt(0);
                                                           top = (v == null) ? 0 : (v.getTop() - listView.getPaddingTop());

                                                        }
                                                        }
                                                }
                                            });

                                        }
                                    }
                                }
                            }
                            else if (statusInt == 201) {
                                String message = jsonObject.getString("message");
                                LinearLayout linearLayoutGrid = (LinearLayout) findViewById(R.id.listview_layout);
                                linearLayoutGrid.setVisibility(View.GONE);
                                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.response_message_linear);
                                linearLayout.setVisibility(View.VISIBLE);
                                TextView responseTextViewTwo = (TextView) findViewById(R.id.response_message_two);
                                responseTextViewTwo.setText(message);
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

    public void setHomepageUserSearchQueryNetworkRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.godprice.com/api/product_list.php?q="+homepageUserSearchQuery+"&userid="+sessionToken;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String trimResponse = response.substring(3);
                            String trimmedResponse = trimResponse.trim();
                            JSONObject jsonObject = new JSONObject(trimmedResponse);
                            String status = jsonObject.getString("status");
                            int statusInt = Integer.parseInt(status);
                            if (statusInt == 200) {
                                JSONArray data = jsonObject.getJSONArray("data");
                                if (data.length() > 0) {
                                    //Loop the Array
                                    for (int i = 0; i < data.length(); i++) {
                                        JSONObject currentObject = data.getJSONObject(i);
                                        JSONArray currentProductDetail = currentObject.getJSONArray("product_detail");

                                        for (int j = 0; j < currentProductDetail.length(); j++) {
                                            //   JSONArray productDetail = new JSONArray("product_detail");
                                            JSONObject e = currentProductDetail.getJSONObject(j);

                                            String prodID = e.getString("product_id");
                                            String productName = e.getString("productsname");
                                            String productPrice = e.getString("price");
                                            String productPriceDollar = getResources().getString(R.string.price_dollar_detail) + productPrice;
                                            String imageUrl = e.getString("feature_image");
                                            String productRating = e.getString("rating");
                                            String productWishlist = e.getString("is_whishlit");

                                            Guide currentGuide = new Guide(prodID, productName, productPriceDollar, imageUrl, productRating, productWishlist);
                                            temples.add(currentGuide);

                                            adapter = new GuideAdapter(CategoryChildActivity.this, temples, R.color.temples_category);
                                            listView = (ListView) findViewById(R.id.child_category_list);
                                            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
                                            listView.setAdapter(adapter);
                                            adapter.notifyDataSetChanged();

                                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    long viewId = view.getId();
                                                    getViewByPosition(position, listView);

                                                    if (viewId == R.id.button_details_two) {
                                                        String productId = listView.getItemAtPosition(position).toString().trim();
                                                        //     TextView Pid = (TextView) parent.findViewById(R.id.product_id);
                                                        //    TextView PPid = (TextView) listView.getChildAt(position).findViewById(R.id.product_id);
                                                        TextView PPid = (TextView) listView.getChildAt(childIndex).findViewById(R.id.product_id);
                                                        String productID = PPid.getText().toString().trim();

                                                        Intent intent = new Intent(CategoryChildActivity.this, DetailsActivity.class);
                                                        intent.putExtra("ProductId", productID);
                                                        intent.putExtra("masterCategoryId", mCid);
                                                        intent.putExtra("parentCategoryId", mPid);
                                                        startActivity(intent);
                                                    } else if (viewId == R.id.image_favorite) {
                                                        if (sessionToken.isEmpty()) {
                                                            Intent intentLoginforWishlist = new Intent(CategoryChildActivity.this, LoginActivity.class);
                                                            startActivity(intentLoginforWishlist);
                                                        } else {
                                                            String productId = listView.getItemAtPosition(position).toString().trim();
                                                            TextView PPid = (TextView) listView.getChildAt(childIndex).findViewById(R.id.product_id);
                                                            String productID = PPid.getText().toString().trim();
                                                            TextView wishlistId = (TextView) listView.getChildAt(childIndex).findViewById(R.id.wishlist_number);
                                                            String wishlistID = wishlistId.getText().toString().trim();
                                                            int wishlistInt = Integer.parseInt(wishlistID);
                                                            if (wishlistInt == 0) {
                                                                sendWishlistRequest(productID, sessionToken);
                                                            } else {
                                                                wishlistProductRemoveRequest(productID, sessionToken);
                                                            }

                                                            index = listView.getFirstVisiblePosition();
                                                            View v = listView.getChildAt(0);
                                                            top = (v == null) ? 0 : (v.getTop() - listView.getPaddingTop());
                                                        }
                                                    }
                                                }
                                            });

                                        }
                                    }
                                }
                            }
                            else if (statusInt == 201) {
                                String message = jsonObject.getString("message");
                                LinearLayout linearLayoutGrid = (LinearLayout) findViewById(R.id.listview_layout);
                                linearLayoutGrid.setVisibility(View.GONE);
                                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.response_message_linear);
                                linearLayout.setVisibility(View.VISIBLE);
                                TextView responseTextViewTwo = (TextView) findViewById(R.id.response_message_two);
                                responseTextViewTwo.setText(message);
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

    private void sendWishlistRequest(String pid, String userId) {

        // final String userId = String.valueOf(uid);
        final String productId = String.valueOf(pid);
        final String UserID = String.valueOf(userId);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.godprice.com/api/whishlist_add.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (mPid == null) {
                            adapter.clear();
                            setHomepageUserSearchQueryNetworkRequest();
                            listView.setSelectionFromTop(index, top);
                            //  adapter.notifyDataSetChanged();
                         //   listView.smoothScrollToPosition(adapter.getCount() -1);

                        } else {
                            adapter.clear();
                            categoryChildNetworkRequest();
                            listView.setSelectionFromTop(index, top);
                          //  adapter.clear();
                          //  adapter.notifyDataSetChanged();
                         //   listView.smoothScrollToPosition(adapter.getCount() -1);
                        }
                        Toast.makeText(CategoryChildActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CategoryChildActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();

            }

        }) { @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("userid", UserID);
            params.put("pid", productId);
            return params;
        }
        };
        queue.add(stringRequest);
    }

    private void wishlistProductRemoveRequest(String productID, String userId) {
        final String ProductID = productID;
        final String UserId = userId;

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.godprice.com/api/whishlist_delete.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String jsonResponse = response.toString().trim();
                            jsonResponse = jsonResponse.substring(3);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            if (mPid == null) {
                                setHomepageUserSearchQueryNetworkRequest();
                              //  adapter.clear();
                                adapter.clear();
                                listView.setSelectionFromTop(index, top);
                                } else {
                                categoryChildNetworkRequest();
                               adapter.clear();
                                listView.setSelectionFromTop(index, top);
                                }
                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error Occurred" + error, Toast.LENGTH_SHORT).show();

            }
        }) { @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("userid", UserId);
            params.put("pid", ProductID);
            return params;
        }
        };
        queue.add(stringRequest);
    }
}



