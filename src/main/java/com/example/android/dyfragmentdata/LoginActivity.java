package com.example.android.dyfragmentdata;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private EditText userEmail;
    private EditText userPassword;
    private String userToken;
    private Session session;
    private static final int RC_SIGN_IN = 6;
    private static final String ANONYMOUS = "anonymous";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final String TAG = "tag";
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    private Button loginEmailButton;
    private String username;
    private String tokenFacebook;
    private String commonToken;
    private String mUsername;
    private String email;
    private String uidFirebase;
    private FirebaseAuth mAuth;
    private String firbaseUsername;
    private static final String EMAIL = "email";
    private String sessionToken;
    private NavigationView navigationView;
    private String usernameGoogle;
    private String sessionGoogleEmail;
    private String sessionUserName;
    private String sessionUserEmail;
    private String sessionUserImage;
    private String sessionUserWalletAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        // Set the content of the activity to use the activity_category.xml layout file
        setContentView(R.layout.login_activity);
        session = new Session(this);
        sessionToken = session.getusertoken();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            usernameGoogle = account.getDisplayName();
            sessionGoogleEmail = account.getEmail();
            session.setusename(usernameGoogle);
            session.setUserEmail(sessionGoogleEmail);
            registerNetworkRequest(usernameGoogle, sessionGoogleEmail);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#e53935"));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        ActionBar actionbar = getSupportActionBar();

        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        setNavigationViewListener();
        mDrawerLayout = findViewById(R.id.drawer_layout);

        toolbar.findViewById(R.id.toolbar_title);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, HomepageActivity.class);
                startActivity(intent);
            }
        });

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

        userEmail = (EditText) findViewById(R.id.et_enter_email);
        userPassword = (EditText) findViewById(R.id.et_enter_password);

        Button signinButton = (Button) findViewById(R.id.button_sign_in);
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginNetworkRequest();
                }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    showFullNavItem();
                    username = user.getDisplayName();
                    email = user.getEmail();
                    registerNetworkRequest(username, email);
                    Intent intentHomepage = new Intent(LoginActivity.this, CartActivity.class);
                    startActivity(intentHomepage);
                    onSignedInInitialize(user.getDisplayName());

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

                } else {
                    onSignedOutCleanUp();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.FacebookBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                }
            }
        });

        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess" + loginResult);
                GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("LoginActivity", response.toString());
                        String email = object.optString("email");
                        String name = object.optString("name");
                        session.setusename(name);
                        session.setUserEmail(email);
                        navigationView = findViewById(R.id.nav_view);
                        View header = navigationView.getHeaderView(0);
                        TextView loggedInUserName = header.findViewById(R.id.header_username_tv);
                        TextView loggedInUserEmail = header.findViewById(R.id.email_address_tv);
                        sessionUserName = session.getusename();
                        sessionUserEmail = session.getUserEmail();
                        loggedInUserName.setText(sessionUserName);
                        loggedInUserEmail.setText(sessionUserEmail);
                        Log.d("LoginActivity", email);
                        String id = object.optString("id");
                        registerFacebookNetworkRequest(name, email);
                                    Log.d("LoginActivity", name);
                                    }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email");
                request.setParameters(parameters);
                request.executeAsync();

                Intent intentHomepage = new Intent(LoginActivity.this, CartActivity.class);

                    startActivity(intentHomepage);
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });

        myAccountNetworkRequest();

        Button merchantButton = (Button) findViewById(R.id.nav_footer_merchant);
        merchantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMechantLogin = new Intent(LoginActivity.this, MerchantLoginActivity.class);
                startActivity(intentMechantLogin);
            }
        });

        Button deliveryButton = (Button) findViewById(R.id.nav_footer_delivery);
        deliveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDelivery = new Intent(LoginActivity.this, DeliveryActivity.class);
                startActivity(intentDelivery);            }
        });

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void showFullNavItem() {
        navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.drawer_view);
        View header = navigationView.getHeaderView(0);
        TextView loggedInUserName = header.findViewById(R.id.header_username_tv);
        TextView loggedInUserEmail = header.findViewById(R.id.email_address_tv);
        ImageView loggedInUserImage = header.findViewById(R.id.user_image_header);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            showFullNavItem();
            Intent intentCart = new Intent(LoginActivity.this, CartActivity.class);
            startActivity(intentCart);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            Toast.makeText(this, "Signed In.", Toast.LENGTH_SHORT).show();
        } else if (requestCode == RESULT_CANCELED) {
            Toast.makeText(this, "Sign in cancelled.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                usernameGoogle = account.getDisplayName();
                sessionGoogleEmail = account.getEmail();
                session.setusename(usernameGoogle);
                session.setUserEmail(sessionGoogleEmail);
                navigationView = findViewById(R.id.nav_view);
                View header = navigationView.getHeaderView(0);
                TextView loggedInUserName = header.findViewById(R.id.header_username_tv);
                TextView loggedInUserEmail = header.findViewById(R.id.email_address_tv);
                sessionUserName = session.getusename();
                sessionUserEmail = session.getUserEmail();
                loggedInUserName.setText(sessionUserName);
                loggedInUserEmail.setText(sessionUserEmail);
                registerNetworkRequest(username, email);
                updateUI(account);
            }
            else {
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
            }
        }catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String idToken;
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                          //  String usernameFacebook = user.getDisplayName();
                          //  String personFBEmail = user.getEmail();
                          //  String fbUserId = user.getUid();
                        //    Log.d(TAG, "usernameFacebook" + usernameFacebook);
                         //   Log.d(TAG, "personFBEmail" + personFBEmail);
                         //   Log.d(TAG, "fbUserId" + fbUserId);
                            user.getIdToken(true)
                                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                                            if (task.isSuccessful()) {
                                              //  sessionToken = task.getResult().getToken();
                                              //  session.setusertoken(sessionToken);
                                 //               Log.d(TAG, "tokenFacebook" + tokenFacebook);
                                            } else {
                                                Log.d(LOG_TAG, "Id token error message", task.getException());
                                            }
                                        }
                                    });
                         //   session.setusename(usernameFacebook);
                            //   String userSession = session.getusename();
                        //    String tokenSession = session.getusertoken();
                         //   Log.d(TAG, "tokenSession" + tokenSession);
                         //   Log.d(TAG, "userSession" + userSession);

                            updateUI(user);
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        }

    public void updateUI(Object o) {

        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            username = user.getDisplayName();
            email = user.getEmail();
            session.setusename(username);
            session.setUserEmail(email);
            navigationView = findViewById(R.id.nav_view);
            View header = navigationView.getHeaderView(0);
            TextView loggedInUserName = header.findViewById(R.id.header_username_tv);
            TextView loggedInUserEmail = header.findViewById(R.id.email_address_tv);
            sessionUserName = session.getusename();
            sessionUserEmail = session.getUserEmail();
            loggedInUserName.setText(sessionUserName);
            loggedInUserEmail.setText(sessionUserEmail);
            registerNetworkRequest(username, email);
            user.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        @Override
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                               // sessionToken = task.getResult().getToken();
                              //  session.setusertoken(sessionToken);
                             //   commonToken = session.getusertoken();
                             //   String userSession = session.getusename();
                           //     Log.d(TAG, "tokenSession" + commonToken);
                            //    Log.d(TAG, "userSession" + userSession);
                               // Intent intentHomepage = new Intent(LoginActivity.this, CartActivity.class);
                           //     startActivity(intentHomepage);
                                showFullNavItem();

                            } else {
                                Log.d(LOG_TAG, "Id token error message", task.getException());
                            }
                        }
                    });
        } else {
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
            }
    }

    private void onSignedInInitialize (String username){
        mUsername = username;
    }

    private void onSignedOutCleanUp () {
        mUsername = ANONYMOUS;
        }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
             usernameGoogle = account.getDisplayName();
         //   String personEmail = account.getEmail();
          //  String personId = account.getId();
          //  userToken = account.getIdToken();
          //  session.setusename(usernameGoogle);
          //  session.setusertoken(userToken);
         //   String sessionGoogleToken = session.getusertoken();
          //  String sessionGoogleUsername = session.getusename();
          //  String sessionGooglePersonId = account.getId();
            sessionGoogleEmail = account.getEmail();
            session.setusename(usernameGoogle);
            session.setUserEmail(sessionGoogleEmail);
            navigationView = findViewById(R.id.nav_view);
            View header = navigationView.getHeaderView(0);
            TextView loggedInUserName = header.findViewById(R.id.header_username_tv);
            TextView loggedInUserEmail = header.findViewById(R.id.email_address_tv);
            sessionUserName = session.getusename();
            sessionUserEmail = session.getUserEmail();
            loggedInUserName.setText(sessionUserName);
            loggedInUserEmail.setText(sessionUserEmail);

           registerNetworkRequest(usernameGoogle, sessionGoogleEmail);
            }
        updateUI(account);
                FirebaseUser currentFacebookUser = mFirebaseAuth.getCurrentUser();
        updateUI(currentFacebookUser);
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
                    View header = navigationView.getHeaderView(0);
                    TextView loggedInUserName = header.findViewById(R.id.header_username_tv);
                    TextView loggedInUserEmail = header.findViewById(R.id.email_address_tv);
                    loggedInUserName.setText(R.string.header_name);
                    loggedInUserEmail.setVisibility(View.GONE);
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
                Intent intentOrderHistory = new Intent(this, OrderHistoryActivity.class);
                startActivity(intentOrderHistory);
                break;
            case R.id.nav_merchant_login:
                Intent intentMechantLogin = new Intent(this, OrderHistoryActivity.class);
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

    private void loginNetworkRequest() {

        final String email = userEmail.getText().toString().trim();
        final String password = userPassword.getText().toString().trim();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.godprice.com/api/login.php";
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

                                String userToken = jsonObject.getString("userid");
                                session.setusertoken(userToken);
                                final String sessionToken = session.getusertoken();
                                defaultLoginNetworkRequest(sessionToken);
                                LinearLayout linearLayoutError = (LinearLayout) findViewById(R.id.response_message_linear);
                                linearLayoutError.setVisibility(View.INVISIBLE);
                                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.response_message_linear_success);
                                linearLayout.setVisibility(View.VISIBLE);
                                linearLayout.setBackgroundColor(Color.parseColor("#9f64dd17"));
                                TextView responseTextViewSuccess = (TextView) findViewById(R.id.response_message_two_success);
                                responseTextViewSuccess.setText(message);

                            //  Toast.makeText(getApplicationContext(), "Signed In", Toast.LENGTH_LONG).show();
                             //   session.setusertoken(userToken);
                             //   final String sessionToken = session.getusertoken();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intentProfile = new Intent(LoginActivity.this, ProfileActivity.class);
                                        intentProfile.putExtra("sessionToken", sessionToken);
                                        startActivity(intentProfile);
                                        Intent intentHomepage = new Intent(LoginActivity.this, CartActivity.class);
                                        startActivity(intentHomepage);
                                    }
                                }, 2000);

                            } else if (statusInt == 201) {
                                //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                LinearLayout linearLayoutError = (LinearLayout) findViewById(R.id.response_message_linear);
                                linearLayoutError.setVisibility(View.VISIBLE);
                                TextView responseTextViewTwo = (TextView) findViewById(R.id.response_message_two);
                                responseTextViewTwo.setText(message);
                            }
                            // Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                            }catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
                }
                })

        { @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("log_email", email);
            params.put("log_password", password);
            return params;
        }
        };

        queue.add(stringRequest);
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
                            String status = jsonObject.getString("status");
                            int statusInt = Integer.parseInt(status);
                              String message = jsonObject.getString("message");
                            if (statusInt == 200) {
                                sessionToken = jsonObject.getString("message");
                                session.setusertoken(sessionToken);
                                if (!sessionToken.isEmpty()) {
                                    showFullNavItem();
                                } else {
                                    navigationView = findViewById(R.id.nav_view);
                                    navigationView.getMenu().clear();
                                    navigationView.inflateMenu(R.menu.drawer_view_without_login);
                                }

                                Intent intentCart = new Intent(LoginActivity.this, CartActivity.class);
                                intentCart.putExtra("sessionTokenGmail", sessionToken);
                                startActivity(intentCart);
                                Intent intentProfile = new Intent(LoginActivity.this, ProfileActivity.class);
                                intentProfile.putExtra("sessionTokenGmail", sessionToken);
                                startActivity(intentProfile);
                             //   Toast.makeText(getApplicationContext(), sessionToken, Toast.LENGTH_LONG).show();
                            } else if (statusInt == 201) {

                            }

                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error Occurred", Toast.LENGTH_SHORT).show();

            }

        });
        queue.add(stringRequest);
    }

    private void registerFacebookNetworkRequest(String name, String email) {

        final String nameGoogle = name;
        final String emailGoogle = email;

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.godprice.com/api/fb.php";
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
                                sessionToken = jsonObject.getString("message");
                                session.setusertoken(sessionToken);
                                if (!sessionToken.isEmpty()) {
                                    showFullNavItem();
                                } else {
                                    navigationView = findViewById(R.id.nav_view);
                                    navigationView.getMenu().clear();
                                    navigationView.inflateMenu(R.menu.drawer_view_without_login);
                                }

                                Intent intentCart = new Intent(LoginActivity.this, CartActivity.class);
                                intentCart.putExtra("sessionToken", sessionToken);
                                startActivity(intentCart);
                            //    Toast.makeText(getApplicationContext(), sessionToken, Toast.LENGTH_LONG).show();
                            }
                            else if (statusInt == 201) {
                                }

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

    private void defaultLoginNetworkRequest(String UserID) {
        final String USERID = UserID;
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
                            JSONObject dataJsonObject = jsonObject.getJSONObject("data");
                            String userId = dataJsonObject.getString("id");
                            String userName = dataJsonObject.getString("name");
                            String userEmail = dataJsonObject.getString("email");
                            String userMobile = dataJsonObject.getString("mobile");
                            String userImage = dataJsonObject.getString("profileimage");

                            session.setusename(userName);
                            session.setUserEmail(userEmail);
                            session.setuserImage(userImage);
                            navigationView = findViewById(R.id.nav_view);
                            View header = navigationView.getHeaderView(0);
                            TextView loggedInUserName = header.findViewById(R.id.header_username_tv);
                            TextView loggedInUserEmail = header.findViewById(R.id.email_address_tv);

                            sessionUserName = session.getusename();
                            sessionUserEmail = session.getUserEmail();
                            sessionUserImage = session.getuserImage();
                            loggedInUserName.setText(sessionUserName);
                            loggedInUserEmail.setText(sessionUserEmail);

                            ImageView loggedInUserImage = header.findViewById(R.id.user_image_header);
                            Glide.with(loggedInUserImage.getContext())
                                    .load(sessionUserImage)
                                    .into(loggedInUserImage);

                            }
                        catch(Exception e) {
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
            params.put("userid", USERID);
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
                Toast.makeText(LoginActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
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





