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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

public class CartActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout mDrawerLayout;
    private Session session;
    private String sessionToken;
    private NavigationView navigationView;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final String LOG_TAG = CartActivity.class.getSimpleName();
    private String usernameGoogle;
    private String sessionGoogleEmail;
    private String android_id;
    private ListView listView;
    private ArrayList<CartData> cartItems;
    private CartAdapter cartAdapter;
    private String cartQuantityUpdated;
    private String cartIDUpdated;
    private String cartIDDelete;
    private int cartIDDeleteInt;
    private int childIndex;
    private String sessionUserName;
    private String sessionUserEmail;
    private String sessionUserImage;
    private String sessionUserWalletAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content of the activity to use the activity_category.xml layout file
        setContentView(R.layout.cart_activity);
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#e53935"));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        cartItems = new ArrayList<CartData>();

        ActionBar actionbar = getSupportActionBar();
        session = new Session(this);
        sessionToken = session.getusertoken();
        cartRequest();
        myAccountNetworkRequest();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            usernameGoogle = account.getDisplayName();
            sessionGoogleEmail = account.getEmail();
            registerNetworkRequest(usernameGoogle, sessionGoogleEmail);
            if (sessionToken.isEmpty()) {
                navigationView = findViewById(R.id.nav_view);
                navigationView.getMenu().clear();
                navigationView.inflateMenu(R.menu.drawer_view_without_login);
            }

            if (!sessionToken.isEmpty()) {

                showFullNavItem();
            }
            }

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //   email = user.getEmail();
                    //   uidFirebase = user.getUid();
                    user.getIdToken(true)
                            .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                @Override
                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                    if (task.isSuccessful()) {
                                        //   sessionToken = task.getResult().getToken();
                                        //      session.setusertoken(sessionToken);
                                    } else {
                                        Log.d(LOG_TAG, "Id token error message", task.getException());
                                    }
                                }
                            });
                }
            }
        };
        updateNavItems();

        // Intent userIdIntent = getIntent();
       // Bundle bundle = userIdIntent.getExtras();

       // if (bundle != null) {
       //     sessionToken = (String) bundle.get("sessionToken");
       // }

        if (actionbar !=null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        toolbar.findViewById(R.id.toolbar_title);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, HomepageActivity.class);
                startActivity(intent);
            }
        });

        mDrawerLayout = findViewById(R.id.drawer_layout);

        setNavigationViewListener();

        Button checkoutButton = (Button) findViewById(R.id.checkout_button);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sessionToken.isEmpty()) {
                    Intent intentCheckout = new Intent(CartActivity.this, CheckoutActivity.class);
                    startActivity(intentCheckout);
                } else if (sessionToken.isEmpty()) {
                    Toast.makeText(CartActivity.this, "Please login to continue", Toast.LENGTH_SHORT).show();
                    Intent intentLogin = new Intent(CartActivity.this, LoginActivity.class);
                    startActivity(intentLogin);
                }

            }
        });

        Button merchantButton = (Button) findViewById(R.id.nav_footer_merchant);
        merchantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMechantLogin = new Intent(CartActivity.this, MerchantLoginActivity.class);
                startActivity(intentMechantLogin);
            }
        });

        Button deliveryButton = (Button) findViewById(R.id.nav_footer_delivery);
        deliveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDelivery = new Intent(CartActivity.this, DeliveryActivity.class);
                startActivity(intentDelivery);            }
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

    private void updateNavItems() {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            if (!sessionToken.isEmpty()) {
                showFullNavItem();
            } else {
                navigationView = findViewById(R.id.nav_view);
                navigationView.getMenu().clear();
                navigationView.inflateMenu(R.menu.drawer_view_without_login);
            }
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

    private void registerNetworkRequest(String name, String emailG) {

        final String nameGoogle = name;
        final String emailGoogle = emailG;

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.godprice.com/api/gmail.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String jsonResponse = response.toString().trim();
                            jsonResponse = jsonResponse.substring(3);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            sessionToken = jsonObject.getString("message");
                            session.setusertoken(sessionToken);
                            if (!sessionToken.isEmpty()) {
                                showFullNavItem();
                            } else {
                                navigationView = findViewById(R.id.nav_view);
                                navigationView.getMenu().clear();
                                navigationView.inflateMenu(R.menu.drawer_view_without_login);
                            }

                          //  Toast.makeText(getApplicationContext(), "Signed In", Toast.LENGTH_SHORT).show();
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
            params.put("name", nameGoogle);
            params.put("email", emailGoogle);
            return params;
        }
            };
        queue.add(stringRequest);
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
                            String statusResponse = jsonObject.getString("status");
                            int statusInt = Integer.parseInt(statusResponse);
                          //  String message = jsonObject.getString("message");
                            if (statusInt == 200) {
                                JSONArray data = jsonObject.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject currentObject = data.getJSONObject(i);
                                    JSONArray currentCartDetail = currentObject.getJSONArray("product_detail");
                                    for (int j = 0; j < currentCartDetail.length(); j++) {
                                        JSONObject currentObjectCart = currentCartDetail.getJSONObject(j);
                                        String productCartId = currentObjectCart.getString("cart_id");
                                        String productImageCart = currentObjectCart.getString("pro_image");
                                        String productId = currentObjectCart.getString("pid");
                                        String productNameCart = currentObjectCart.getString("product_name");
                                        String productCartQuantity = currentObjectCart.getString("quantity");
                                        String productPriceCart = currentObjectCart.getString("price");
                                        String productPriceDollar = getResources().getString(R.string.price_dollar_detail) + productPriceCart;
                                        CartData currentData = new CartData(productId, productCartId, productNameCart, productCartQuantity, productPriceDollar, productImageCart);
                                        cartItems.add(currentData);
                                        cartAdapter = new CartAdapter(CartActivity.this, cartItems);
                                        //   Toast.makeText(CartActivity.this, "Cart response", LENGTH_SHORT).show();
                                    }

                                    listView = (ListView) findViewById(R.id.cart_list);
                                    listView.setNestedScrollingEnabled(true);
                                    JSONObject currentCartTotalDetail = currentObject.getJSONObject("cart");
                                    String no_of_productCart = currentCartTotalDetail.getString("no_of_product");
                                    String cartTotalAmount = currentCartTotalDetail.getString("total_amount");
                                    String productPriceDollar = getResources().getString(R.string.price_dollar_detail) + cartTotalAmount;
                                    TextView noOfItemsCart = (TextView) findViewById(R.id.header_no_cart_items);
                                    noOfItemsCart.setText(no_of_productCart + " " + getResources().getString(R.string.cart_items));

                                    TextView totalAmountCart = (TextView) findViewById(R.id.header_text_total_amount);
                                    totalAmountCart.setText(productPriceDollar + " " + getResources().getString(R.string.cart_total_amount));
                                    listView.setAdapter(cartAdapter);
                                    cartAdapter.notifyDataSetChanged();

                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            long viewId = view.getId();
                                            getViewByPosition(position,listView);
                                            if (viewId == R.id.button_update) {
                                                EditText quantityView = (EditText) listView.getChildAt(childIndex).findViewById(R.id.editText_Quantity);
                                                cartQuantityUpdated = quantityView.getText().toString().trim();

                                                TextView cardIdView = (TextView) listView.getChildAt(childIndex).findViewById(R.id.cart_id);
                                                cartIDUpdated = cardIdView.getText().toString().trim();

                                                cartUpdateRequest();

                                            } else if (viewId == R.id.button_delete) {
                                                TextView cardIdView = (TextView) listView.getChildAt(childIndex).findViewById(R.id.cart_id);
                                                cartIDDelete = cardIdView.getText().toString().trim();
                                                // cartIDDeleteInt = Integer.parseInt(cartIDDelete);
                                                cartDeleteRequest();
                                            }
                                        }
                                    });
                                    }
                            }
                            else if (statusInt == 201) {
                                String message = jsonObject.getString("message");
                                LinearLayout linearLayoutHeader = (LinearLayout) findViewById(R.id.header);
                                linearLayoutHeader.setVisibility(View.GONE);

                                //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.response_message_linear);
                                linearLayout.setVisibility(View.VISIBLE);
                                TextView responseTextViewTwo = (TextView) findViewById(R.id.response_message_two);
                                responseTextViewTwo.setText(getResources().getString(R.string.cart_empty));

                                Button checkoutButton = (Button) findViewById(R.id.checkout_button);
                                checkoutButton.setVisibility(View.GONE);
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

    private void cartUpdateRequest() {
        cartAdapter.clear();
      //  LinearLayout cartHeaderViews = (LinearLayout) findViewById(R.id.header);
      //  cartHeaderViews.removeAllViews();
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://www.godprice.com/api/cart_update.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                         /*   String jsonResponse = response.toString().trim();
                            jsonResponse = jsonResponse.substring(3);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            JSONArray data = jsonObject.getJSONArray("data");

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject currentObject = data.getJSONObject(i);
                                JSONArray currentCartDetail = currentObject.getJSONArray("product_detail");
                                for (int j = 0; j < currentCartDetail.length(); j++) {
                                    JSONObject currentObjectCart = currentCartDetail.getJSONObject(j);
                                    String productCartId = currentObjectCart.getString("cart_id");
                                    String productImageCart = currentObjectCart.getString("pro_image");
                                    String productId = currentObjectCart.getString("pid");
                                    String productNameCart = currentObjectCart.getString("product_name");
                                    String productCartQuantity = currentObjectCart.getString("quantity");
                                    String productPriceCart = currentObjectCart.getString("price");
                                    CartData currentData = new CartData(productId, productCartId, productNameCart, productCartQuantity, productPriceCart, productImageCart);
                                    cartItems.add(currentData);
                                    cartAdapter = new CartAdapter(CartActivity.this, cartItems);
                                    //   Toast.makeText(CartActivity.this, "Cart response", LENGTH_SHORT).show();
                                }

                                listView = (ListView) findViewById(R.id.cart_list);
                                listView.setNestedScrollingEnabled(true);
                                JSONObject currentCartTotalDetail = currentObject.getJSONObject("cart");
                                String no_of_productCart = currentCartTotalDetail.getString("no_of_product");
                                String cartTotalAmount = currentCartTotalDetail.getString("total_amount");
                                LinearLayout cartHeaderViews = (LinearLayout) findViewById(R.id.header);
                                cartHeaderViews.setVisibility(View.VISIBLE);
                                TextView noOfItemsCart = (TextView) findViewById(R.id.header_no_cart_items);
                                noOfItemsCart.setText(no_of_productCart + " " + getResources().getString(R.string.cart_items));

                                TextView totalAmountCart = (TextView) findViewById(R.id.header_text_total_amount);
                                totalAmountCart.setText(cartTotalAmount + " " + getResources().getString(R.string.cart_total_amount)); */
                              //  listView.setAdapter(cartAdapter);
                            //
                            cartRequest();
                          //  cartAdapter.notifyDataSetChanged();
                                Toast.makeText(CartActivity.this, "Cart updated successfully", LENGTH_SHORT).show();

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
            params.put("cart_id", cartIDUpdated);
            params.put("qty", cartQuantityUpdated);
            return params;
        }
        };
        queue.add(stringRequest);
    }

    private void cartDeleteRequest() {

        cartAdapter.clear();

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://www.godprice.com/api/cart_delete.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            cartRequest();

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
            params.put("cart_id", cartIDDelete);
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
                            String WalletPriceDollar = getResources().getString(R.string.wallet_amount_label) + " " + getResources().getString(R.string.price_dollar_detail) + walletAmount;
                            TextView loggedInUserWalletAmount = header.findViewById(R.id.wallet_amount_header);
                            if (!walletAmount.isEmpty()) {
                                loggedInUserWalletAmount.setText(WalletPriceDollar);
                            }
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CartActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
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
}
       