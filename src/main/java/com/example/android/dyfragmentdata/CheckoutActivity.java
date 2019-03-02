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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.nio.file.StandardWatchEventKinds;
import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

public class CheckoutActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private Session session;
    private String sessionToken;
    private NavigationView navigationView;
    private String usernameGoogle;
    private String sessionGoogleEmil;
    private RadioButton radioButton;
    private LinearLayout addressLayout;
    private String addressId;
    private String fullname;
    private String phoneno;
    private String address;
    private String city;
    private String country;
    private String zipcode;
    private TextView addressTextView;
    private RadioGroup radioGroup;
    private int addressIdInt;
    private int checkedAddressId;
    private String android_id;
    private String cartTotalAmount;
    private int cartAmountInt;
    private TextView totalAmountCart;
    private String sessionUserName;
    private String sessionUserEmail;
    private String sessionUserImage;
    private String checkedAddressIdString;
    private String checkedAddress;
    private String sessionUserWalletAmount;
    private LinearLayout addressButton;
    private Button addressRemoveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content of the activity to use the activity_category.xml layout file
        setContentView(R.layout.activity_checkout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#e53935"));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        ActionBar actionbar = getSupportActionBar();
        session = new Session(this);
        sessionToken = session.getusertoken();
        getAddressesNetworkRequest();
        cartRequest();

        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        if (actionbar !=null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            usernameGoogle = account.getDisplayName();
           // sessionToken = usernameGoogle;
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

        toolbar.findViewById(R.id.toolbar_title);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckoutActivity.this, HomepageActivity.class);
                startActivity(intent);
            }
        });

        mDrawerLayout = findViewById(R.id.drawer_layout);

        if (sessionToken.isEmpty()) {
            navigationView = findViewById(R.id.nav_view);
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.drawer_view_without_login);
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

        setNavigationViewListener();

       /* Button addNewAddressButton = (Button) findViewById(R.id.add_new_address);
        addNewAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentCart = new Intent(CheckoutActivity.this, AddAddressActivity.class);
                startActivity(intentCart);
            }
        }); */


        Button reviewOrderTopButton = (Button) findViewById(R.id.forward_arrow_button);
        reviewOrderTopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CheckoutActivity.this, "Please select address.", Toast.LENGTH_SHORT).show();
               // Intent intentCart = new Intent(CheckoutActivity.this, ReviewOrderActivity.class);
              //  startActivity(intentCart);
            }
        });

     Button cartButton = (Button) findViewById(R.id.back_arrow_button);
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCart = new Intent(CheckoutActivity.this, CartActivity.class);
                startActivity(intentCart);
            }
        });

        Button AddAddressButton = (Button) findViewById(R.id.add_new_address);
        AddAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCart = new Intent(CheckoutActivity.this, AddAddressActivity.class);
                startActivity(intentCart);
            }
        });

        Button merchantButton = (Button) findViewById(R.id.nav_footer_merchant);
        merchantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMechantLogin = new Intent(CheckoutActivity.this, MerchantLoginActivity.class);
                startActivity(intentMechantLogin);
            }
        });

        Button deliveryButton = (Button) findViewById(R.id.nav_footer_delivery);
        deliveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDelivery = new Intent(CheckoutActivity.this, DeliveryActivity.class);
                startActivity(intentDelivery);            }
        });
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.checkbox_new_address:
                if (checked) {
                    Intent intentAddAddress = new Intent(this, AddAddressActivity.class);
                startActivity(intentAddAddress);
                }
              //  Toast.makeText(this, "Please select address.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.radio_button:
                if (checked) {
                    Button reviewOrderButton = (Button) findViewById(R.id.forward_arrow_button);
                    reviewOrderButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                          //  checkoutNetworkRequest();
                            Intent intentCart = new Intent(CheckoutActivity.this, ReviewOrderActivity.class);
                            intentCart.putExtra("checkedAddressId",checkedAddressIdString);
                            startActivity(intentCart);
                        }
                        });
                }
                else {
                Toast.makeText(this, "Please select address.", Toast.LENGTH_SHORT).show();
                }
                break;
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

    private void getAddressesNetworkRequest() {

        radioGroup = (RadioGroup) findViewById(R.id.radio_button);
        radioGroup.removeAllViews();

        addressButton = (LinearLayout) findViewById(R.id.address_linear_layout_remove);
        addressButton.removeAllViews();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.godprice.com/api/address_list.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String jsonResponse = response.toString().trim();
                            jsonResponse = jsonResponse.substring(3);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            JSONArray data = jsonObject.getJSONArray("data");
                            if (data.length() > 0) {
                                //Loop the Array
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject currentAddress = data.getJSONObject(i);
                                    addressId = currentAddress.getString("addressid");
                                    addressIdInt = Integer.parseInt(addressId);
                                    fullname = currentAddress.getString("fullname");
                                    phoneno = currentAddress.getString("phoneno");
                                    address = currentAddress.getString("address");
                                    city = currentAddress.getString("city");
                                    country = currentAddress.getString("country");
                                    zipcode = currentAddress.getString("zipcode");
                                   // addressLayout = (LinearLayout) findViewById(R.id.radio_button_address);
                                 //   RadioGroup radioGroup = (Ra
                                    //
                                    // dioGroup) findViewById(R.id.radio_button);
                                    //  radioGroup.addView(addressTextView);

                                    addressButton = (LinearLayout) findViewById(R.id.address_linear_layout_remove);
                                    addressRemoveButton = new Button(CheckoutActivity.this);
                                    addressRemoveButton.setId(i+1);
                                    addressRemoveButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    LinearLayout.LayoutParams buttonMarginAddressRemove = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    buttonMarginAddressRemove.setMargins(65, 50, 0, 0);
                                    LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams)addressRemoveButton.getLayoutParams();
                                    ll.gravity = Gravity.CENTER;
                                    addressRemoveButton.setBackground(getDrawable(R.drawable.button_circle_address_remove));
                                    addressRemoveButton.setLayoutParams(ll);
                                    addressRemoveButton.setText(getResources().getString(R.string.wishlist_remove_product_icon));
                                    addressRemoveButton.setLayoutParams(buttonMarginAddressRemove);
                                    addressRemoveButton.setTextColor(Color.WHITE);
                                    addressButton.addView(addressRemoveButton);
                                    addressRemoveButton.setTextSize(15);
                                    addressRemoveButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            long removeAddressId = v.getId();
                                            addressDeleteRequest();
                                        }
                                    });

                                    radioGroup = (RadioGroup) findViewById(R.id.radio_button);
                                    addressTextView = new TextView(CheckoutActivity.this);

                                    addressTextView.setId(i+1);
                                    addressTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    LinearLayout.LayoutParams buttonMargin = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    buttonMargin.setMargins(15, 0, 0, 0);
                                    addressTextView.setText(addressId);
                                    addressTextView.setLayoutParams(buttonMargin);
                                    addressTextView.setTextSize(13);
                                    addressTextView.setVisibility(View.INVISIBLE);
                                    radioGroup.addView(addressTextView);

                                    // RadioGroup.LayoutParams rpms;
                                    radioButton = new RadioButton (CheckoutActivity.this);
                                    //  radioButton.append("Id: " + addressId);
                                    radioButton.append(fullname + " ");
                                    radioButton.append(phoneno);
                                    radioButton.append("\n");
                                    radioButton.append(address + "," + " ");
                                    radioButton.append(city + "," + " ");
                                    radioButton.append(country);
                                    radioButton.append("\n");
                                    radioButton.append(zipcode);
                                    radioButton.setId(addressIdInt);
                                  //  rpms = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                  //  RadioGroup.LayoutParams radioMargin = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                  //  radioMargin.setMargins(15, 0, 0, 0);
                                   // radioButton.setLayoutParams(radioMargin);
                                   // radioGroup.addView(radioButton, rpms);
                                    radioButton.setLayoutParams(new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    RadioGroup.LayoutParams radioMargin = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    radioMargin.setMargins(10, 0, 0, 0);
                                    radioButton.setLayoutParams(radioMargin);
                                    radioGroup.addView(radioButton);

                                    // final int indexTwo = j;
                                    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(RadioGroup radioGroup, int i) {                                              // for (int k=0; k < radioGroup.getChildCount(); k++) {
                                            //    radioButton = (RadioButton) radioGroup.getChildAt(k);
                                            //  if (radioButton.getId() == j) {

                                            checkedAddressId = radioGroup.getCheckedRadioButtonId();
                                            checkedAddressIdString = String.valueOf(checkedAddressId);
                                            checkedAddress = radioButton.getText().toString().trim();

                                            Button reviewOrderButton = (Button) findViewById(R.id.forward_arrow_button);
                                            reviewOrderButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intentCart = new Intent(CheckoutActivity.this, ReviewOrderActivity.class);
                                                    intentCart.putExtra("checkedAddressId",checkedAddressIdString);
                                                    startActivity(intentCart);
                                                    //  checkoutNetworkRequest();
                                                }
                                            });

                                            //  int checkedAddressId = addressTextView.getId();
                                            //  String subString = checkedAddress.substring(4,6);
                                           // String addressId = addressTextView.getText().toString().trim();
                                          //  Toast.makeText(CheckoutActivity.this, "Index " + i, LENGTH_SHORT).show();
                                            //  return;
                                        }
                                    });



                                }
                                    }
                            //Toast.makeText(getApplicationContext(), "Address registered successfully.", Toast.LENGTH_SHORT).show();
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error Occurred", Toast.LENGTH_SHORT).show();

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

   /* private void checkoutNetworkRequest() {

        final String checkedIdAddress = String.valueOf(checkedAddressId);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.godprice.com/api/checkout.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Toast.makeText(CheckoutActivity.this, "Checkout response.", Toast.LENGTH_SHORT).show();
                            Intent intentReviewOrder = new Intent(CheckoutActivity.this, ReviewOrderActivity.class);
                            startActivity(intentReviewOrder);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CheckoutActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();

            }

        }) { @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("userid", sessionToken);
            params.put("device_id", android_id);
            params.put("addressid", checkedIdAddress);
            return params;
        }
        };
        queue.add(stringRequest);
    } */

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
                                String statusResponse = jsonObject.getString("status");
                                int statusInt = Integer.parseInt(statusResponse);
                                if (statusInt == 200) {
                                JSONArray data = jsonObject.getJSONArray("data");

                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject currentObject = data.getJSONObject(i);
                                    // JSONArray currentCartDetail = currentObject.getJSONArray("product_detail");
                                    JSONObject currentCartTotalDetail = currentObject.getJSONObject("cart");
                                    String no_of_productCart = currentCartTotalDetail.getString("no_of_product");
                                    cartTotalAmount = currentCartTotalDetail.getString("total_amount");
                                    cartAmountInt = Integer.parseInt(cartTotalAmount);
                                    if (!cartTotalAmount.isEmpty()) {
                                        myAccountNetworkRequest();
                                    }
                                    String productPriceDollar = getResources().getString(R.string.price_dollar_detail) + cartTotalAmount;
                                    totalAmountCart = (TextView) findViewById(R.id.cart_price);
                                    totalAmountCart.setText(productPriceDollar);

                                }
                                }
                            else if (statusInt == 201) {
                                String message = jsonObject.getString("message");
                                //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                Intent intentCart = new Intent(CheckoutActivity.this, CartActivity.class);
                                startActivity(intentCart);
                            }
                            } catch (Exception e) {
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
                            session.setuserWalletAmount(walletAmount);
                            sessionUserWalletAmount = session.getuserWalletAmount();
                            navigationView = findViewById(R.id.nav_view);
                            View header = navigationView.getHeaderView(0);
                            String WalletPriceDollarHeader = getResources().getString(R.string.wallet_amount_label) + " " + getResources().getString(R.string.price_dollar_detail) + walletAmount;
                            String WalletPriceDollar = getResources().getString(R.string.price_dollar_detail) + walletAmount;
                            TextView loggedInUserWalletAmount = header.findViewById(R.id.wallet_amount_header);
                            if (!walletAmount.isEmpty()) {
                               loggedInUserWalletAmount.setText(WalletPriceDollarHeader);
                                }

                                int walletAmountInt = Integer.parseInt(walletAmount);

                            if (walletAmountInt <= 0) {
                                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.response_message_linear);
                                linearLayout.setVisibility(View.VISIBLE);
                                TextView responseTextViewTwo = (TextView) findViewById(R.id.response_message_two);
                                responseTextViewTwo.setText(getResources().getString(R.string.wallet_not_sufficient));

                                Button nextButton = (Button) findViewById(R.id.forward_arrow_button);
                                nextButton.setVisibility(View.GONE);
                            }

                          //  cartRequest();
                            TextView walletAmountTextView = (TextView) findViewById(R.id.wallet_amount_price);
                            walletAmountTextView.setText(WalletPriceDollar);
                           // String cartAmount = cartTotalAmount;
                         //   if (!cartTotalAmount.isEmpty()) {
                                // int cartAmountInteger = Integer.parseInt(cartTotalAmount);

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
                         //   }

                            } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CheckoutActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
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

    private void addressDeleteRequest() {

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://www.godprice.com/api/address_delete.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String jsonResponse = response.toString().trim();
                            jsonResponse = jsonResponse.substring(3);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            String statusResponse = jsonObject.getString("status");
                            int statusInt = Integer.parseInt(statusResponse);
                            if (statusInt == 200) {
                                getAddressesNetworkRequest();
                                Toast.makeText(CheckoutActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                } else if (statusInt == 201) {
                                String statusErrorResponse = jsonObject.getString("message");
                                Toast.makeText(CheckoutActivity.this, statusErrorResponse, Toast.LENGTH_SHORT).show();
                            }
                            } catch (Exception e) {
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
            params.put("address_id", checkedAddressIdString);
            return params;
        }
        };
        queue.add(stringRequest);
    }
}

