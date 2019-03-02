package com.example.android.dyfragmentdata;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class ParentCategoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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
    private GridView gridView;
    private int childIndex;
    private String sessionUserImage;
    private String sessionUserWalletAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_category);

        Intent masterCatetoryIdIntent = getIntent();
        Bundle bundle = masterCatetoryIdIntent.getExtras();

        if (bundle != null) {
            mCid = (String) bundle.get("masterCategoryId");
        }

        session = new Session(this);
        sessionToken = session.getusertoken();
        sendGridParentCategoryRequest();
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
                Intent intent = new Intent(ParentCategoryActivity.this, HomepageActivity.class);
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
            navigationView = findViewById(R.id.nav_view);
            navigationView.inflateMenu(R.menu.drawer_view);
            View header = navigationView.getHeaderView(0);
            ImageView loggedInUserImage = header.findViewById(R.id.user_image_header);
            sessionUserImage = session.getuserImage();
            if (!sessionUserImage.isEmpty()) {
                Glide.with(loggedInUserImage.getContext())
                        .load(sessionUserImage)
                        .into(loggedInUserImage);
            }
            showFullNavItem();
        }

        // Create an ArrayList of badgujar objects
        gridParentCategories = new ArrayList<ParentCategory>();
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
                //  setNavigationViewListener();
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

    private void sendGridParentCategoryRequest() {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.godprice.com/api/category_parent.php?m_cid="+mCid;
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
                            if (statusInt == 1) {
                                JSONArray data = jsonObject.getJSONArray("data");
                                if (data.length() > 0) {
                                    //Loop the Array
                                    for (int i = 0; i < data.length(); i++) {
                                        JSONObject currentObject = data.getJSONObject(i);
                                        String parentCategoryId = currentObject.getString("p_cid");
                                        String categoryId = currentObject.getString("m_cid");
                                        String categoryName = currentObject.getString("categoryname");
                                        String categoryImage = currentObject.getString("categoryimage");
                                        parentCategory = new ParentCategory(parentCategoryId, categoryId, categoryName, categoryImage);
                                        gridParentCategories.add(parentCategory);
                                        gridParentAdapter = new GridParentAdapter(ParentCategoryActivity.this, gridParentCategories);
                                        // Get a reference to the GridView, and attach the adapter to the gridView.
                                        gridView = (GridView) findViewById(R.id.gridView);
                                        gridView.setAdapter(gridParentAdapter);
                                        gridParentAdapter.notifyDataSetChanged();
                                        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                long viewId = view.getId();
                                                getViewByPosition(position,gridView);
                                                String productId = gridView.getItemAtPosition(position).toString().trim();
                                                TextView parentCategoryIdClicked = (TextView) gridView.getChildAt(childIndex).findViewById(R.id.gridTextViewParentId);
                                                String parentCategoryId = parentCategoryIdClicked.getText().toString().trim();
                                                TextView mCid = (TextView) gridView.getChildAt(childIndex).findViewById(R.id.gridTextViewCategoryId);
                                                String masterCategoryId = mCid.getText().toString().trim();

                                                Intent intent = new Intent(ParentCategoryActivity.this, CategoryChildActivity.class);
                                                intent.putExtra("parentCategoryId", parentCategoryId);
                                                intent.putExtra("masterCategoryId", masterCategoryId);
                                                startActivity(intent);
                                            }
                                        });
                                    }
                                }
                                } else if (statusInt == 0) {
                                String message = jsonObject.getString("message");
                                LinearLayout linearLayoutGrid = (LinearLayout) findViewById(R.id.gridView_layout);
                                linearLayoutGrid.setVisibility(View.GONE);
                                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.response_message_linear);
                                linearLayout.setVisibility(View.VISIBLE);
                                TextView responseTextViewTwo = (TextView) findViewById(R.id.response_message_two);
                                responseTextViewTwo.setText(message);
                                }
                            } catch (JSONException e) {
                            //     Log.e("Volley", "Problem parsing the category JSON results", e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ParentCategoryActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();

            }

        }) { @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();
            return params;
        }
        };
        queue.add(stringRequest);
    }
}


