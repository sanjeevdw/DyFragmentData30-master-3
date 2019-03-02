package com.example.android.dyfragmentdata;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReviewOrderActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private Session session;
    private String sessionToken;
    private NavigationView navigationView;
    private String usernameGoogle;
    private String sessionGoogleEmil;
    private ArrayList<ReviewOrderData> reviewOrderItems;
    private ReviewAdapter reviewAdapter;
    private ListView listView;
    private String android_id;
    private TextView totalAmountCartSummary;
    private int cartTotalAmountInt;
    private int walletAmountInt;
    private int cartTotalAmountIntButton;
    private int cartAmountInt;
    private String cartTotalAmount;
    private String sessionUserName;
    private String sessionUserEmail;
    private String sessionUserImage;
    private String addressIdString;
    private String sessionUserWalletAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content of the activity to use the activity_category.xml layout file
        setContentView(R.layout.activity_review_order);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#e53935"));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        ActionBar actionbar = getSupportActionBar();
        cartRequest();

        Intent addressIdIntent = getIntent();
        Bundle bundle = addressIdIntent.getExtras();

        if (bundle != null) {
            addressIdString = (String) bundle.get("checkedAddressId");
        }

        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        reviewOrderItems = new ArrayList<ReviewOrderData>();

        session = new Session(this);
        sessionToken = session.getusertoken();

        if (actionbar !=null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        mDrawerLayout = findViewById(R.id.drawer_layout);

        toolbar.findViewById(R.id.toolbar_title);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewOrderActivity.this, HomepageActivity.class);
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
                sessionUserImage = session.getuserImage();
                if (!sessionUserImage.isEmpty()) {
                    navigationView = findViewById(R.id.nav_view);
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

        if (sessionToken.isEmpty()) {
            navigationView = findViewById(R.id.nav_view);
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.drawer_view_without_login);
        }

      //  if (!sessionToken.isEmpty()) {
        //    showFullNavItem();
      //  }

        setNavigationViewListener();

        Button billingAddressPrevButton = (Button) findViewById(R.id.back_arrow_button);
        billingAddressPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPayment = new Intent(ReviewOrderActivity.this, CheckoutActivity.class);
                startActivity(intentPayment);
            }
        });

       /* Button billingAddressButton = (Button) findViewById(R.id.billing_address_button);
        billingAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPayment = new Intent(ReviewOrderActivity.this, CheckoutActivity.class);
                startActivity(intentPayment);
            }
        }); */

        Button reviewOrderButton = (Button) findViewById(R.id.forward_arrow_button);
        reviewOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView cartPriceTextView = (TextView) findViewById(R.id.cart_price);

                String cartAmount = cartPriceTextView.getText().toString().trim();

                TextView walletTextView = (TextView) findViewById(R.id.wallet_amount_price);
                String walletAmount = walletTextView.getText().toString().trim();
              //  int walletTotalAmountIntButton = Integer.parseInt(walletAmount);

                TextView amountToPayTextView = (TextView) findViewById(R.id.amount_to_pay_price);
                String amountToPay = amountToPayTextView.getText().toString().trim();

                if (!amountToPay.isEmpty()) {
                    if (cartAmountInt >=0) {
                    Intent intentPayment = new Intent(ReviewOrderActivity.this, PaymentActivity.class);
                    intentPayment.putExtra("amountToPay", amountToPay);
                    startActivity(intentPayment);
                        checkoutNetworkRequest();
                        }

                    else  {
                    Intent intentPayment = new Intent(ReviewOrderActivity.this, PaymentActivity.class);
                    startActivity(intentPayment);
                    }
                }
                 }
        });

       /* Button paymentButton = (Button) findViewById(R.id.payment_button);
        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCart = new Intent(ReviewOrderActivity.this, PaymentActivity.class);
                startActivity(intentCart);
            }
        }); */
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
        String WalletPriceDollar = getResources().getString(R.string.wallet_amount_label) + " " + getResources().getString(R.string.price_dollar_detail) + sessionUserWalletAmount;
        TextView loggedInUserWalletAmount = header.findViewById(R.id.wallet_amount_header);
        if (!sessionUserWalletAmount.isEmpty()) {
            loggedInUserWalletAmount.setText(WalletPriceDollar);
        }
        ImageView loggedInUserImage = header.findViewById(R.id.user_image_header);
        sessionUserImage = session.getuserImage();
        if (!sessionUserImage.isEmpty()) {
            Glide.with(loggedInUserImage.getContext())
                    .load(sessionUserImage)
                    .into(loggedInUserImage);
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
                setNavigationViewListener();
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

    private void cartRequest() {

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://www.godprice.com/api/cart.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String jsonResponse = response.toString().trim();
                            jsonResponse = jsonResponse.substring(3);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            JSONArray data = jsonObject.getJSONArray("data");

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject currentObject = data.getJSONObject(i);
                                JSONArray currentCartDetail = currentObject.getJSONArray("product_detail");
                                for (int j = 0; j < currentCartDetail.length(); j++) {
                                    JSONObject currentObjectCart = currentCartDetail.getJSONObject(j);
                                    String productImageCart = currentObjectCart.getString("pro_image");
                                    String productNameCart = currentObjectCart.getString("product_name");
                                    String productCartQuantity = currentObjectCart.getString("quantity");
                                    String productPriceCart = currentObjectCart.getString("price");
                                    String productPriceDollar = getResources().getString(R.string.price_dollar_detail) + " "+ productPriceCart;
                                    if (!productPriceCart.isEmpty()) {
                                        myAccountNetworkRequest();
                                    }
                                    ReviewOrderData currentReviewOrderData = new ReviewOrderData(productNameCart, productCartQuantity, productPriceDollar, productImageCart);
                                    reviewOrderItems.add(currentReviewOrderData);
                                    reviewAdapter = new ReviewAdapter(ReviewOrderActivity.this, reviewOrderItems);
                                    //   Toast.makeText(CartActivity.this, "Cart response", LENGTH_SHORT).show();
                                    listView = (ListView) findViewById(R.id.review_order_list);
                                    listView.setNestedScrollingEnabled(true);
                                    listView.setAdapter(reviewAdapter);
                                    reviewAdapter.notifyDataSetChanged();
                                }

                                JSONObject currentCartTotalDetail = currentObject.getJSONObject("cart");
                                String no_of_productCart = currentCartTotalDetail.getString("no_of_product");
                                cartTotalAmount = currentCartTotalDetail.getString("total_amount");
                                String productPriceDollar = getResources().getString(R.string.price_dollar_detail) + cartTotalAmount;
                                cartTotalAmountInt = Integer.parseInt(cartTotalAmount);
                                totalAmountCartSummary = (TextView) findViewById(R.id.cart_price);
                                totalAmountCartSummary.setText(productPriceDollar);

                                TextView noOfItemsCart = (TextView) findViewById(R.id.header_no_cart_items);
                                noOfItemsCart.setText(no_of_productCart + " " + getResources().getString(R.string.cart_items));

                                TextView totalAmountCart = (TextView) findViewById(R.id.header_text_total_amount);
                                totalAmountCart.setText(getResources().getString(R.string.price_dollar_detail) + cartTotalAmount + " " + getResources().getString(R.string.cart_total_amount));

                                } } catch (Exception e) {
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
        })
        { @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("device_id", android_id);
            return params;
        }
        };
        queue.add(stringRequest);
    }

    private void myAccountNetworkRequest() {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.godprice.com/api/my-account.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String jsonResponse = response.toString().trim();
                            jsonResponse = jsonResponse.substring(3);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            JSONObject data = jsonObject.getJSONObject("data");
                            String walletAmount = data.getString("wallet_amount");
                            String WalletPriceDollar = getResources().getString(R.string.price_dollar_detail) + walletAmount;
                            walletAmountInt = Integer.parseInt(walletAmount);
                          //  Toast.makeText(ReviewOrderActivity.this, "My account response.", Toast.LENGTH_SHORT).show();
                            TextView walletAmountTextView = (TextView) findViewById(R.id.wallet_amount_price);
                            walletAmountTextView.setText(WalletPriceDollar);

                       /*     String cartAmount = totalAmountCartSummary.getText().toString();
                            int cartAmountInt = Integer.parseInt(cartAmount);
                            int amountToPay = cartAmountInt - walletAmountInt;
                            String amountToPayString = String.valueOf(amountToPay);
                            if (cartAmountInt <= walletAmountInt) {
                                TextView amountToPayTextView = (TextView) findViewById(R.id.amount_to_pay_price);
                                amountToPayTextView.setText(amountToPayString);
                            } else {
                                String noWalletAmount = "NA";
                                TextView amountToPayTextView = (TextView) findViewById(R.id.amount_to_pay_price);
                                amountToPayTextView.setText(noWalletAmount);
                            } */

                            int amountToPay = cartAmountInt - walletAmountInt;
                            String amountToPayString = String.valueOf(amountToPay);
                            String productPriceDollar = getResources().getString(R.string.price_dollar_detail) + "0";
                            if (cartAmountInt <= walletAmountInt) {
                                TextView amountToPayTextView = (TextView) findViewById(R.id.amount_to_pay_price);
                                amountToPayTextView.setText(productPriceDollar);
                            } else {
                                String noWalletAmount = "NA";
                                TextView amountToPayTextView = (TextView) findViewById(R.id.amount_to_pay_price);
                                amountToPayTextView.setText(noWalletAmount);
                            }
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ReviewOrderActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();

            }

        }) { @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("userid", sessionToken);
            return params;
        }
        };
        queue.add(stringRequest);
    }

    private void checkoutNetworkRequest() {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.godprice.com/api/checkout.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                          //  Toast.makeText(ReviewOrderActivity.this, "Checkout response.", Toast.LENGTH_SHORT).show();
                        //    Intent intentReviewOrder = new Intent(ReviewOrderActivity.this, PaymentActivity.class);
                        //    startActivity(intentReviewOrder);

                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ReviewOrderActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();

            }

        }) { @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("userid", sessionToken);
            params.put("device_id", android_id);
            params.put("addressid", addressIdString);
            return params;
        }
        };
        queue.add(stringRequest);
    }
}
