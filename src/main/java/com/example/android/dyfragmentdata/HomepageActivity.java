package com.example.android.dyfragmentdata;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import com.onesignal.OneSignal;

public class HomepageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private ArrayList<Guide> temples;
    private ArrayList<HomepageRecommendedCategoryData> gridCategoriesTwo;
    private GuideAdapter adapter;
    private HomepageRecommendedCategoryAdapter adapterRecommendedCategory;
    private ArrayList<HomepageFeaturedCategoryData> gridCategoriesThree;
    private HomepageFeaturedCategoryAdapter adapterFeaturedCategory;
    private ListView listView;
    private ListView listViewTwo;
    private ListView listViewThree;
    private GridAdapter gridAdapter;
    private ArrayList<GridCategory> gridCategories;
    private int[] tabIcons = {R.drawable.shoes, R.drawable.mensjeans, R.drawable.casecase, R.drawable.accessories, R.drawable.wallet};
    private TabLayout tabLayout;
    CustomViewPager imageViewPager;
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;
    int currentPage = 0;
    private Session session;
    private String sessionToken;
    private NavigationView navigationView;
    private String usernameGoogle;
    private String sessionGoogleEmil;
    private int childIndex;
    private String android_id;
    private SliderData sliderData;
    private ArrayList<SliderData> sliderDataItems;
    private String sessionUserName;
    private String sessionUserEmail;
    private GridView gridView;
    private GridView gridViewTwo;
    private GridView gridViewThree;
    private ArrayList<DealsOfTheDay> dealsOfTheDay;
    private DealsOfTheDayAdapter dealsOfTheDayAdapter;
    private ArrayList<HomepageTrendingProductData> homepageTrendingProductData;
    private HomepageTrendingProductAdapter homepageTrendingProductAdapter;
    private String sessionUserImage;
    private String sessionUserWalletAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        sendSliderRequest();
        session = new Session(this);
        sessionToken = session.getusertoken();
        sessionUserName = session.getusename();
        sessionUserEmail = session.getUserEmail();
        dealsProductsNetworkRequest(sessionToken);
        recommendedProductsNetworkRequest();
        trendingProductsNetworkRequest();
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        listView = (ListView) findViewById(R.id.list);
        listViewTwo = (ListView) findViewById(R.id.list_two);
        listViewThree = (ListView) findViewById(R.id.list_three);
        dealsOfTheDay = new ArrayList<DealsOfTheDay>();
        temples = new ArrayList<Guide>();
        homepageTrendingProductData = new ArrayList<HomepageTrendingProductData>();
        sliderDataItems = new ArrayList<SliderData>();
        // listView.setNestedScrollingEnabled(true);
        setNavigationViewListener();
        sendGridTopCategoryRequest();
        sendGridRecommendedCategoryRequest();
        sendGridFeaturedCategoryRequest();
        sendHeaderCategoriesRequest();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            usernameGoogle = account.getDisplayName();
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
                    navigationView.getMenu().clear();
                    navigationView.inflateMenu(R.menu.drawer_view_without_login);
                    View header = navigationView.getHeaderView(0);
                    ImageView loggedInUserImage = header.findViewById(R.id.user_image_header);
                    Glide.with(loggedInUserImage.getContext())
                            .load(sessionUserImage)
                            .into(loggedInUserImage);
                }
                               showFullNavItem();
                }
        }

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
                 Intent intent = new Intent(HomepageActivity.this, HomepageActivity.class);
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
            sessionUserImage = session.getuserImage();
            if (!sessionUserImage.isEmpty()) {
                ImageView loggedInUserImage = header.findViewById(R.id.user_image_header);
                Glide.with(loggedInUserImage.getContext())
                        .load(sessionUserImage)
                        .into(loggedInUserImage);
            }
            showFullNavItem();
            }

        // Create an ArrayList of objects
        gridCategories = new ArrayList<GridCategory>();
        gridCategoriesTwo = new ArrayList<HomepageRecommendedCategoryData>();
        gridCategoriesThree = new ArrayList<HomepageFeaturedCategoryData>();

     /*   {
            gridCategories.add(new GridCategory(R.drawable.grid_image_one, "Voice control your world"));
            gridCategories.add(new GridCategory(R.drawable.grid_image_two, "Stream November 16"));
            gridCategories.add(new GridCategory(R.drawable.grid_image_three, "Thinner, Lighter"));
            gridCategories.add(new GridCategory(R.drawable.grid_image_four, "With Voice Remote"));
            } */

     //  gridAdapter = new GridAdapter(HomepageActivity.this, gridCategories);

        // Get a reference to the GridView, and attach the adapter to the gridView.
      /*  GridView gridView = (GridView) findViewById(R.id.gridView);
        GridView gridViewTwo = (GridView) findViewById(R.id.gridView_two);
        GridView gridViewThree = (GridView) findViewById(R.id.gridView_three);
        gridView.setAdapter(gridAdapter);
        gridViewTwo.setAdapter(gridAdapter);
        gridViewThree.setAdapter(gridAdapter); */

      // ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        // Create an adapter that knows which fragment should be shown on each page.

      //  SimpleFragmentPageAdapter adapter = new SimpleFragmentPageAdapter(this, getSupportFragmentManager());

        // Set the adapter onto the view pager.

     //   viewPager.setAdapter(adapter);

        // Find the tab layout that shows the tab
     //   tabLayout = (TabLayout) findViewById(R.id.tabs);

      //  tabLayout.setupWithViewPager(viewPager);
     //   setupTabIcons();

        imageViewPager = (CustomViewPager) findViewById(R.id.viewPager);
       // ViewPagerApdater viewPagerAdapter = new ViewPagerApdater(this);
       // imageViewPager.setAdapter(viewPagerAdapter);

        // Auto start of viewpager

        final Handler handler = new Handler();
        final Runnable update = new Runnable() {

            public void run() {

                if (currentPage == 5 ) {
                    currentPage = 0;
                }
                imageViewPager.setCurrentItem(currentPage++, true);

            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, 2000, 2000);

        final EditText editText = (EditText) findViewById(R.id.search_box);

        ImageView imageView = (ImageView) findViewById(R.id.search_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String userSearchQuery = editText.getText().toString().trim();
                Intent intent = new Intent(HomepageActivity.this, CategoryChildActivity.class);
                intent.putExtra("userSearchQuery", userSearchQuery);
                startActivity(intent);
            }
        });
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

    public View getViewByPositionGrid(int pos, GridView gridView) {
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

     /*   private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        tabLayout.getTabAt(4).setIcon(tabIcons[4]);

        } */

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

    private void dealsProductsNetworkRequest(String userId) {

        final String userID = userId;

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.godprice.com/api/home_product.php?deal_of_day=yes&userid="+sessionToken;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

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
                                        //    for (int j = 0; j < productDetail.length(); j++) {
                                        //   JSONArray productDetail = new JSONArray("product_detail");
                                        JSONObject e = currentProductDetail.getJSONObject(j);
                                        String productId = e.getString("product_id");
                                        String productName = e.getString("productsname");
                                        String productPrice = e.getString("price");
                                        String productPriceDollar = getResources().getString(R.string.price_dollar_detail) + productPrice;
                                        String imageUrl = e.getString("feature_image");
                                        String productRating = e.getString("rating");
                                        String productWishlist = e.getString("is_whishlit");

                                        // Guide currentGuide = new Guide(productId, productName, productPriceDollar, imageUrl, productRating, productWishlist);
                                        DealsOfTheDay currentDealsOfTheDay = new DealsOfTheDay(productId, productName, productPriceDollar, imageUrl, productRating, productWishlist);
                                        dealsOfTheDay.add(currentDealsOfTheDay);
                                        dealsOfTheDayAdapter = new DealsOfTheDayAdapter(HomepageActivity.this, dealsOfTheDay, R.color.temples_category);
                                        listView.setAdapter(dealsOfTheDayAdapter);
                                        dealsOfTheDayAdapter.notifyDataSetChanged();

                                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                long viewId = view.getId();
                                                getViewByPosition(position,listView);

                                                if (viewId == R.id.button_details_two) {
                                                    String productId = listView.getItemAtPosition(position).toString().trim();
                                                    TextView PPid = (TextView) listView.getChildAt(childIndex).findViewById(R.id.product_id);
                                                    String productID = PPid.getText().toString().trim();
                                                    String homepageToDetail = "homepageToDetail";
                                                    Intent intent = new Intent(HomepageActivity.this, DetailsActivity.class);
                                                    intent.putExtra("ProductId", productID);
                                                    intent.putExtra("homepageToDetail", homepageToDetail);
                                                    startActivity(intent);
                                                    } else if(viewId == R.id.image_favorite) {
                                                    if (sessionToken.isEmpty()) {
                                                        Intent intentLoginforWishlist = new Intent(HomepageActivity.this, LoginActivity.class);
                                                        startActivity(intentLoginforWishlist);
                                                    } else {
                                                    String productId = listView.getItemAtPosition(position).toString().trim();
                                                    TextView PPid = (TextView) listView.getChildAt(childIndex).findViewById(R.id.product_id);
                                                    String productID = PPid.getText().toString().trim();
                                                    TextView wishlistId = (TextView) listView.getChildAt(childIndex).findViewById(R.id.wishlist_number);
                                                    String wishlistID = wishlistId.getText().toString().trim();
                                                    int wishlistInt = Integer.parseInt(wishlistID);
                                                    if (wishlistInt == 0) {
                                                        sendWishlistRequest(productID);
                                                    } else  {
                                                        sendWishlistDeleteRequest(productID);
                                                    }
                                                    }
                                                }
                                            }
                                        });
                                        }
                                        }
                            }
                        } catch (JSONException e) {
                            //     Log.e("Volley", "Problem parsing the category JSON results", e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                }
                });
        queue.add(stringRequest);
    }

    private void recommendedProductsNetworkRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.godprice.com/api/home_product.php?recommended_product=yes&userid="+sessionToken;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String trimResponse = response.substring(3);
                            String trimmedResponse = trimResponse.trim();
                            JSONObject jsonObject = new JSONObject(trimmedResponse);
                            //  Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                JSONArray data = jsonObject.getJSONArray("data");
                                if (data.length() > 0) {
                                    //Loop the Array
                                    for (int i = 0; i < data.length(); i++) {
                                        JSONObject currentObject = data.getJSONObject(i);
                                        JSONArray currentProductDetail = currentObject.getJSONArray("product_detail");
                                        for (int j = 0; j < currentProductDetail.length(); j++) {
                                            JSONObject e = currentProductDetail.getJSONObject(j);
                                            String productId = e.getString("product_id");
                                            String productName = e.getString("productsname");
                                            String productPrice = e.getString("price");
                                            String productPriceDollar = getResources().getString(R.string.price_dollar_detail) + productPrice;
                                            String imageUrl = e.getString("feature_image");
                                            String productRating = e.getString("rating");
                                            String productWishlist = e.getString("is_whishlit");

                                            Guide currentGuide = new Guide(productId, productName, productPriceDollar, imageUrl, productRating, productWishlist);
                                            temples.add(currentGuide);
                                            adapter = new GuideAdapter(HomepageActivity.this, temples, R.color.temples_category);
                                            listViewTwo.setAdapter(adapter);
                                            adapter.notifyDataSetChanged();
                                            listViewTwo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    long viewId = view.getId();
                                                    getViewByPosition(position,listViewTwo);

                                                    if (viewId == R.id.button_details_two) {
                                                        String productId = listViewTwo.getItemAtPosition(position).toString().trim();
                                                        TextView PPid = (TextView) listViewTwo.getChildAt(childIndex).findViewById(R.id.product_id);
                                                        String productID = PPid.getText().toString().trim();
                                                        String homepageToDetail = "homepageToDetail";
                                                        Intent intent = new Intent(HomepageActivity.this, DetailsActivity.class);
                                                        intent.putExtra("ProductId", productID);
                                                        intent.putExtra("homepageToDetail", homepageToDetail);
                                                        startActivity(intent);
                                                    } else if(viewId == R.id.image_favorite) {
                                                        if (sessionToken.isEmpty()) {
                                                            Intent intentLoginforWishlist = new Intent(HomepageActivity.this, LoginActivity.class);
                                                            startActivity(intentLoginforWishlist);
                                                        } else {
                                                            String productId = listViewTwo.getItemAtPosition(position).toString().trim();
                                                            TextView PPid = (TextView) listViewTwo.getChildAt(childIndex).findViewById(R.id.product_id);
                                                            String productID = PPid.getText().toString().trim();
                                                            TextView wishlistId = (TextView) listViewTwo.getChildAt(childIndex).findViewById(R.id.wishlist_number);
                                                            String wishlistID = wishlistId.getText().toString().trim();
                                                            int wishlistInt = Integer.parseInt(wishlistID);
                                                            if (wishlistInt == 0) {
                                                                sendWishlistRequest(productID);
                                                            } else {
                                                                sendWishlistDeleteRequest(productID);
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                                } catch (JSONException e) {
                            //     Log.e("Volley", "Problem parsing the category JSON results", e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                }

        });
        queue.add(stringRequest);
    }

    private void trendingProductsNetworkRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.godprice.com/api/home_product.php?trending_product=yes&userid="+sessionToken;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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
                                            JSONObject e = currentProductDetail.getJSONObject(j);
                                            String parentCategoryId = e.getString("p_cid");
                                            String categoryId = e.getString("m_cid");
                                            String productId = e.getString("product_id");
                                            String productName = e.getString("productsname");
                                            String productPrice = e.getString("price");
                                            String productPriceDollar = getResources().getString(R.string.price_dollar_detail) + productPrice;
                                            String imageUrl = e.getString("feature_image");
                                            String productRating = e.getString("rating");
                                            String productWishlist = e.getString("is_whishlit");
                                            HomepageTrendingProductData currentHomepageTrendingProductData = new HomepageTrendingProductData(productId, productName, productPriceDollar, imageUrl, productRating, productWishlist);
                                            homepageTrendingProductData.add(currentHomepageTrendingProductData);
                                            homepageTrendingProductAdapter = new HomepageTrendingProductAdapter(HomepageActivity.this, homepageTrendingProductData, R.color.temples_category);
                                            listViewThree.setAdapter(homepageTrendingProductAdapter);
                                            homepageTrendingProductAdapter.notifyDataSetChanged();
                                            listViewThree.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    long viewId = view.getId();

                                                    getViewByPosition(position,listViewThree);

                                                    if (viewId == R.id.button_details_two) {
                                                        String productId = listViewThree.getItemAtPosition(position).toString().trim();
                                                        TextView PPid = (TextView) listViewThree.getChildAt(childIndex).findViewById(R.id.product_id);
                                                        String productID = PPid.getText().toString().trim();
                                                        String homepageToDetail = "homepageToDetail";
                                                        Intent intent = new Intent(HomepageActivity.this, DetailsActivity.class);
                                                        intent.putExtra("ProductId", productID);
                                                        intent.putExtra("homepageToDetail", homepageToDetail);
                                                        startActivity(intent);
                                                    } else if(viewId == R.id.image_favorite) {
                                                        if (sessionToken.isEmpty()) {
                                                            Intent intentLoginforWishlist = new Intent(HomepageActivity.this, LoginActivity.class);
                                                            startActivity(intentLoginforWishlist);
                                                        } else {
                                                            String productId = listViewThree.getItemAtPosition(position).toString().trim();
                                                            TextView PPid = (TextView) listViewThree.getChildAt(childIndex).findViewById(R.id.product_id);
                                                            String productID = PPid.getText().toString().trim();
                                                            TextView wishlistId = (TextView) listViewThree.getChildAt(childIndex).findViewById(R.id.wishlist_number);
                                                            String wishlistID = wishlistId.getText().toString().trim();
                                                            int wishlistInt = Integer.parseInt(wishlistID);
                                                            if (wishlistInt == 0) {
                                                                sendWishlistRequest(productID);
                                                            } else {
                                                                sendWishlistDeleteRequest(productID);
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                                } catch (JSONException e) {
                            //     Log.e("Volley", "Problem parsing the category JSON results", e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

        });
        queue.add(stringRequest);
    }

    private void sendWishlistRequest(String pid) {

        final String productId = String.valueOf(pid);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.godprice.com/api/whishlist_add.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String jsonResponse = response.toString().trim();
                            jsonResponse = jsonResponse.substring(3);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            String status = jsonObject.getString("status");
                            int statusInt = Integer.parseInt(status);
                            String message = jsonObject.getString("message");
                            if (statusInt == 200) {
                                dealsProductsNetworkRequest(sessionToken);
                                dealsOfTheDayAdapter.clear();
                                recommendedProductsNetworkRequest();
                                adapter.clear();
                                trendingProductsNetworkRequest();
                                homepageTrendingProductAdapter.clear();
                                } else if (statusInt == 201) {
                                Toast.makeText(HomepageActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                         catch(Exception e) {
                                e.printStackTrace();
                            }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomepageActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                }

        }) { @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("userid", sessionToken);
            params.put("pid", productId);
            return params;
        }
        };
        queue.add(stringRequest);
    }

    private void sendWishlistDeleteRequest(String pid) {

        final String productId = String.valueOf(pid);

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
                            String status = jsonObject.getString("status");
                            int statusInt = Integer.parseInt(status);
                            String message = jsonObject.getString("message");
                            if (statusInt == 200) {
                                dealsProductsNetworkRequest(sessionToken);
                                dealsOfTheDayAdapter.clear();
                                recommendedProductsNetworkRequest();
                                adapter.clear();
                                trendingProductsNetworkRequest();
                                homepageTrendingProductAdapter.clear();
                                } else if (statusInt == 201) {
                                Toast.makeText(HomepageActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomepageActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
            }

        }) { @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("userid", sessionToken);
            params.put("pid", productId);
            return params;
        }
        };
        queue.add(stringRequest);
    }

    private void sendSliderRequest() {

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://www.godprice.com/api/slider.php";
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                String trimResponse = response.substring(3);
                                String trimmedResponse = trimResponse.trim();
                                JSONObject jsonObject = new JSONObject(trimmedResponse);
                                JSONArray data = jsonObject.getJSONArray("data");
                                if (data.length() > 0) {
                                    //Loop the Array
                                    for (int i = 0; i < data.length(); i++) {
                                        JSONObject currentObject = data.getJSONObject(i);
                                        String sliderId = currentObject.getString("id");
                                        String sliderTitle = currentObject.getString("title");
                                        String sliderDescription = currentObject.getString("description");
                                        String sliderImage = currentObject.getString("image");
                                        SliderData sliderData = new SliderData(sliderId, sliderTitle, sliderDescription, sliderImage);
                                        sliderDataItems.add(sliderData);
                                        imageViewPager = (CustomViewPager) findViewById(R.id.viewPager);
                                        ViewPagerApdater viewPagerAdapter = new ViewPagerApdater(HomepageActivity.this, sliderDataItems);
                                        imageViewPager.setAdapter(viewPagerAdapter);
                                        viewPagerAdapter.notifyDataSetChanged();
                                    }
                                }
                            } catch (JSONException e) {
                                //     Log.e("Volley", "Problem parsing the category JSON results", e);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(HomepageActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();

                }

            }) { @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
            };
            queue.add(stringRequest);
    }

    private void sendGridTopCategoryRequest() {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.godprice.com/api/home_category.php?top_category=yes";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String trimResponse = response.substring(3);
                            String trimmedResponse = trimResponse.trim();
                            JSONObject jsonObject = new JSONObject(trimmedResponse);
                            JSONArray data = jsonObject.getJSONArray("data");
                            if (data.length() > 0) {
                                //Loop the Array
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject currentObject = data.getJSONObject(i);
                                    String categoryId = currentObject.getString("m_cid");
                                    String categoryName = currentObject.getString("categoryname");
                                    String categoryImage = currentObject.getString("categoryimage");
                                    GridCategory gridCategory = new GridCategory(categoryId, categoryName, categoryImage);
                                    gridCategories.add(gridCategory);
                                    gridAdapter = new GridAdapter(HomepageActivity.this, gridCategories);
                                    // Get a reference to the GridView, and attach the adapter to the gridView.
                                    gridView = (GridView) findViewById(R.id.gridView);
                                    gridView.setAdapter(gridAdapter);
                                  //  gridViewTwo = (GridView) findViewById(R.id.gridView_two);
                                 //   gridViewTwo.setAdapter(gridAdapter);
                                  //  gridViewThree = (GridView) findViewById(R.id.gridView_three);
                                  //  gridViewThree.setAdapter(gridAdapter);
                                    gridAdapter.notifyDataSetChanged();
                                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            long viewId = view.getId();
                                            getViewByPositionGrid(position,gridView);
                                            String productId = gridView.getItemAtPosition(position).toString().trim();
                                            TextView mCid = (TextView) gridView.getChildAt(childIndex).findViewById(R.id.gridTextViewId);
                                            String masterCategoryId = mCid.getText().toString().trim();

                                            Intent intent = new Intent(HomepageActivity.this, ParentCategoryActivity.class);
                                            intent.putExtra("masterCategoryId", masterCategoryId);
                                            startActivity(intent);
                                        }
                                    });
                                    }
                            }
                        } catch (JSONException e) {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomepageActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();

            }

        }) { @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();
            return params;
        }
        };
        queue.add(stringRequest);
    }

    private void sendGridRecommendedCategoryRequest() {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.godprice.com/api/home_category.php?recommended_category=yes";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String trimResponse = response.substring(3);
                            String trimmedResponse = trimResponse.trim();
                            JSONObject jsonObject = new JSONObject(trimmedResponse);
                            JSONArray data = jsonObject.getJSONArray("data");
                            if (data.length() > 0) {
                                //Loop the Array
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject currentObject = data.getJSONObject(i);
                                    String categoryId = currentObject.getString("m_cid");
                                    String categoryName = currentObject.getString("categoryname");
                                    String categoryImage = currentObject.getString("categoryimage");
                                    HomepageRecommendedCategoryData currentGridCategoriesTwo = new HomepageRecommendedCategoryData(categoryId, categoryName, categoryImage);
                                    gridCategoriesTwo.add(currentGridCategoriesTwo);
                                    adapterRecommendedCategory = new HomepageRecommendedCategoryAdapter(HomepageActivity.this, gridCategoriesTwo);
                                    // Get a reference to the GridView, and attach the adapter to the gridView.
                                    gridViewTwo = (GridView) findViewById(R.id.gridView_two);
                                    gridViewTwo.setAdapter(adapterRecommendedCategory);
                                    adapterRecommendedCategory.notifyDataSetChanged();
                                    gridViewTwo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            long viewId = view.getId();
                                            getViewByPositionGrid(position,gridView);
                                            String productId = gridView.getItemAtPosition(position).toString().trim();
                                            TextView mCid = (TextView) gridView.getChildAt(childIndex).findViewById(R.id.gridTextViewId);
                                            String masterCategoryId = mCid.getText().toString().trim();

                                            Intent intent = new Intent(HomepageActivity.this, ParentCategoryActivity.class);
                                            intent.putExtra("masterCategoryId", masterCategoryId);
                                            startActivity(intent);
                                        }
                                    });
                                    }
                            }
                        } catch (JSONException e) {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomepageActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();

            }

        }) { @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();
            return params;
        }
        };
        queue.add(stringRequest);
    }

    private void sendGridFeaturedCategoryRequest() {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.godprice.com/api/home_category.php?featured_category=yes";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String trimResponse = response.substring(3);
                            String trimmedResponse = trimResponse.trim();
                            JSONObject jsonObject = new JSONObject(trimmedResponse);
                            JSONArray data = jsonObject.getJSONArray("data");
                            if (data.length() > 0) {
                                //Loop the Array
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject currentObject = data.getJSONObject(i);
                                    String categoryId = currentObject.getString("m_cid");
                                    String categoryName = currentObject.getString("categoryname");
                                    String categoryImage = currentObject.getString("categoryimage");
                                    HomepageFeaturedCategoryData currentGridCategoriesThree = new HomepageFeaturedCategoryData(categoryId, categoryName, categoryImage);
                                    gridCategoriesThree.add(currentGridCategoriesThree);
                                    adapterFeaturedCategory = new HomepageFeaturedCategoryAdapter(HomepageActivity.this, gridCategoriesThree);
                                    // Get a reference to the GridView, and attach the adapter to the gridView.
                                    gridViewThree = (GridView) findViewById(R.id.gridView_three);
                                    gridViewThree.setAdapter(adapterFeaturedCategory);
                                    adapterFeaturedCategory.notifyDataSetChanged();
                                    gridViewThree.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            long viewId = view.getId();
                                            getViewByPositionGrid(position,gridView);
                                            String productId = gridView.getItemAtPosition(position).toString().trim();
                                            TextView mCid = (TextView) gridView.getChildAt(childIndex).findViewById(R.id.gridTextViewId);
                                            String masterCategoryId = mCid.getText().toString().trim();

                                            Intent intent = new Intent(HomepageActivity.this, ParentCategoryActivity.class);
                                            intent.putExtra("masterCategoryId", masterCategoryId);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }
                        } catch (JSONException e) {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomepageActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();

            }

        }) { @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();
            return params;
        }
        };
        queue.add(stringRequest);
    }

    private void sendHeaderCategoriesRequest() {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.godprice.com/api/home_category.php?header_category=yes";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String trimResponse = response.substring(3);
                            String trimmedResponse = trimResponse.trim();
                            JSONObject jsonObject = new JSONObject(trimmedResponse);
                            JSONArray data = jsonObject.getJSONArray("data");
                            if (data.length() > 0) {
                                //Loop the Array
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject currentObject = data.getJSONObject(i);
                                    String categoryId = currentObject.getString("m_cid");
                                    String categoryName = currentObject.getString("categoryname");
                                    String categoryImage = currentObject.getString("categoryimage");

                                    LinearLayout headerLayout = (LinearLayout) findViewById(R.id.header_categories_image);
                                    LinearLayout.LayoutParams imageMargin = new LinearLayout.LayoutParams(150, 100);
                                    imageMargin.setMargins(15, 0, 0, 0);
                                    final ImageView imageView = new ImageView(HomepageActivity.this);
                                    int catId = Integer.parseInt(categoryId);
                                    imageView.setId(catId);
                                    imageView.setLayoutParams(new LinearLayout.LayoutParams(150, 100));
                                    imageView.setLayoutParams(imageMargin);
                                    imageView.setMaxHeight(40);
                                    imageView.setMaxWidth(40);
                                    imageView.setLayoutParams(imageMargin);
                                    final Context context = getApplicationContext();
                                    if (isValidContextForGlide(context)) {
                                        // Load image via Glide lib using context
                                        Glide.with(context)
                                                .load(categoryImage)
                                                .into(imageView);
                                    }

                                    headerLayout.addView(imageView);
                                    imageView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            int categoryId = v.getId();
                                            String masterCategoryId = String.valueOf(categoryId);
                                            Intent intent = new Intent(HomepageActivity.this, ParentCategoryActivity.class);
                                            intent.putExtra("masterCategoryId", masterCategoryId);
                                            startActivity(intent);
                                            }
                                    });

                                    LinearLayout headerLayoutText = (LinearLayout) findViewById(R.id.header_categories_text);
                                    LinearLayout.LayoutParams textMargin = new LinearLayout.LayoutParams(150, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    textMargin.setMargins(15, 0, 0, 0);
                                    final TextView textView = new TextView(HomepageActivity.this);
                                    textView.setId(catId);
                                    textView.setLayoutParams(new LinearLayout.LayoutParams(150, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    textView.setText(categoryName);
                                    textView.setTextSize(12);
                                    textView.setMaxLines(2);
                                    textView.setGravity(Gravity.CENTER);
                                    textView.setTextColor(Color.WHITE);
                                    textView.setLayoutParams(textMargin);
                                    textView.setTypeface(Typeface.DEFAULT_BOLD);
                                    headerLayoutText.addView(textView);
                                }
                            }
                        } catch (JSONException e) {
                            //     Log.e("Volley", "Problem parsing the category JSON results", e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomepageActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();

            }

        }) { @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();
            return params;
        }
        };
        queue.add(stringRequest);
    }

    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }
}





