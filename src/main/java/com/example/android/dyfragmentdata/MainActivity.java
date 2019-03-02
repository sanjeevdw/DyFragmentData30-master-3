package com.example.android.dyfragmentdata;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
public Bundle bundle;
    private DrawerLayout mDrawerLayout;
    private Session session;
    private String sessionToken;
    private NavigationView navigationView;
    private String usernameGoogle;
    private String sessionGoogleEmil;
    private String sessionUserName;
    private String sessionUserEmail;
    private String sessionUserImage;
    private String sessionUserWalletAmount;
    private SliderData sliderData;
    private ArrayList<SliderData> sliderDataItems;
    CustomViewPager imageViewPager;
    int currentPage = 0;
    private ExpandableHeightGridView gridView;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content of the activity to use the activity_category.xml layout file
        setContentView(R.layout.activity_main);
        sendSliderRequest();
        sliderDataItems = new ArrayList<SliderData>();

        // Find the view pager that will allow the user to swipe between fragments.

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        // Create an adapter that knows which fragment should be shown on each page.

        SimpleFragmentPageAdapter adapter = new SimpleFragmentPageAdapter(this, getSupportFragmentManager());

        // Set the adapter onto the view pager.

        viewPager.setAdapter(adapter);

     /*   viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                viewPager.getParent().requestDisallowInterceptTouchEvent(true);
            }
        }); */

        // Find the tab layout that shows the tab
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        setNavigationViewListener();
        // Connect the tab layout with the view page. This will
        // 1. Update the tab layout when the view pager is swiped.
        // 2. Update the view pager when a tab is selected.
        // 3. Set the tab layout's tab names with the view pager's adapter's titles
        tabLayout.setupWithViewPager(viewPager);
        categoryNetworkRequest();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        toolbar.setBackgroundColor(Color.parseColor("#e53935"));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        session = new Session(this);
        sessionToken = session.getusertoken();

        if (actionbar !=null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        toolbar.findViewById(R.id.toolbar_title);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomepageActivity.class);
                startActivity(intent);
            }
        });

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            usernameGoogle = account.getDisplayName();
            sessionToken = usernameGoogle;
            sessionGoogleEmil = account.getEmail();
            if (sessionToken.isEmpty()) {
                navigationView = findViewById(R.id.nav_view);
                navigationView.getMenu().clear();
                navigationView.inflateMenu(R.menu.drawer_view_without_login);
            }

            if (!sessionToken.isEmpty()) {
                showFullNavItem();
            }
        }

        mDrawerLayout = findViewById(R.id.drawer_layout);

        if (sessionToken.isEmpty()) {
            navigationView = findViewById(R.id.nav_view);
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.drawer_view_without_login);
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

        Button merchantButton = (Button) findViewById(R.id.nav_footer_merchant);
        merchantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMechantLogin = new Intent(MainActivity.this, MerchantLoginActivity.class);
                startActivity(intentMechantLogin);
            }
        });

        Button deliveryButton = (Button) findViewById(R.id.nav_footer_delivery);
        deliveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDelivery = new Intent(MainActivity.this, DeliveryActivity.class);
                startActivity(intentDelivery);            }
        });

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

    private void categoryNetworkRequest() {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.godprice.com/api/product_list.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Create an empty ArrayList that we can start adding earthquakes to
                       // List<Guide> guideList = new ArrayList<>();
                        ArrayList<Guide> temples = new ArrayList<Guide>();
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

                        //    for (int j = 0; j < productDetail.length(); j++) {
                                //   JSONArray productDetail = new JSONArray("product_detail");
                                Log.e("Message", "loop");
                                HashMap<String, String> map = new HashMap<String, String>();
                                JSONObject e = currentProductDetail.getJSONObject(i);
                                map.put("cid", "cid :" + e.getString("product_id"));
                                map.put("Category name", "Category name : " + e.getString("productsname"));

                                String productId = e.getString("product_id");
                                String productName = e.getString("productsname");
                                String productPrice = e.getString("price");
                                String imageUrl = e.getString("feature_image");
                                String productRating = e.getString("rating");
                                String productWishlist = e.getString("is_whishlit");

                                Guide currentGuide = new Guide(productId, productName, productPrice, imageUrl, productRating, productWishlist);
                                temples.add(currentGuide);

                                //  temples.add(new Guide("Manua Bhan Ki Tekri", "Sun City, Lalghati"));
                                //temples.add(new Guide("Laxminarayan Temple", "Arera Hills"));
                                //temples.add(new Guide("Gayatri Mandir", "Zone-1, Maharana Pratap Nagar"));
                                // temples.add(new Guide("Gufa Mandir", "Lalghati"));
                                // temples.add(new Guide("Balaji Mandir", "Bankhera, BHEL"));
                                // temples.add(new Guide("Birla Mandir", "Arera Hills"));
                                // temples.add(new Guide("Maa Kali Bijasen Temple", "Kolar Road, Chunna Bhatti"));
                                // temples.add(new Guide("Mahalakshmi Temple", "Karunadham Ashram, Gomti Colony"));
                                // temples.add(new Guide("Bhojpur Shiva Temple", "Bhojpur"));
                                // temples.add(new Guide("ISKCON Bhopal Center", "Sector A, Indrapuri"));

                                // addTab(categoryName);
                                //   Toast.makeText(getApplicationContext(), "Post successful.", Toast.LENGTH_SHORT).show();
                                //  Log.d("tag", String.valueOf(map));
                            }
}


                      //      bundle = new Bundle();
                    //        bundle.putParcelableArrayList("temples", temples);
                      //      Log.d("tag",String.valueOf(bundle));
                        //    NewArrivalFragment templesFragment = new NewArrivalFragment();
                          //  templesFragment.setArguments(bundle);

                        } catch (JSONException e) {
                            // If an error is thrown when executing any of the above statements in the "try" block,
                            // catch the exception here, so the app doesn't crash. Print a log message
                            // with the message from the exception.
                       //     Log.e("Volley", "Problem parsing the category JSON results", e);
                        }
                        // Return the list of earthquakes
                        // return categories;

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
             //   Toast.makeText(getApplicationContext(), "Error Occurred", Toast.LENGTH_SHORT).show();

            }

        });
        queue.add(stringRequest);
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
                                    ViewPagerApdater viewPagerAdapter = new ViewPagerApdater(MainActivity.this, sliderDataItems);
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
                Toast.makeText(MainActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();

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

