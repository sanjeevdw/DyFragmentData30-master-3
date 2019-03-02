package com.example.android.dyfragmentdata;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.net.Authenticator.RequestorType.SERVER;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private Session session;
    private String userSession;
    private String sessionToken;
    private String userId;
    private NavigationView navigationView;
    private String usernameGoogle;
    private String sessionGoogleEmil;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText mobileEditText;
    private String sessionUserName;
    private String sessionUserEmail;
    ImageView image;
    Button choose, upload, updateProfileButton;
    int PICK_IMAGE_REQUEST = 111;
    String URL ="https://www.godprice.com/api/update_image.php";
    // String URL ="https://www.godprice.com/uploads/";
    Bitmap bitmap;
    ProgressDialog progressDialog;
    private String picturePath;
    private Uri filePath;
    private String stringUri;
    private static final int MY_PERMISSIONS_REQUEST_MULTIPLE = 4;
    private ImageView userProfileImage;
    private String sessionUserImage;
    private String sessionUserWalletAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updated_profile);
        Intent userIdIntent = getIntent();
        Bundle bundle = userIdIntent.getExtras();

        if (bundle != null) {
            userId = (String) bundle.get("sessionToken");
        }

        setNavigationViewListener();

       nameEditText = (EditText) findViewById(R.id.first_name_et);
       mobileEditText = (EditText) findViewById(R.id.mobile_et);
       emailEditText = (EditText) findViewById(R.id.email_et);
        userProfileImage = (ImageView) findViewById(R.id.user_image);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#e53935"));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        ActionBar actionbar = getSupportActionBar();
        session = new Session(this);
        sessionToken = session.getusertoken();

        if (!sessionToken.isEmpty()) {
            loginNetworkRequest(sessionToken);
        }

        if (actionbar !=null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        mDrawerLayout = findViewById(R.id.drawer_layout);

        toolbar.findViewById(R.id.toolbar_title);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, HomepageActivity.class);
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

        if (sessionToken.isEmpty()) {
            navigationView = findViewById(R.id.nav_view);
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.drawer_view_without_login);
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

        choose = (Button)findViewById(R.id.choose);

        //opening image chooser option
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            }
        });


        updateProfileButton = (Button) findViewById(R.id.submit_button_update_profile);
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(ProfileActivity.this);
                progressDialog.setMessage("Uploading, please wait...");
                progressDialog.show();
                imageUpload(picturePath);
                updateProfileRequest(sessionToken);
                uploadMultipart();
            }
        });

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) + ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_PHONE_STATE) || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Please allow permissions to better serve you.", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_MULTIPLE);
            }
        } else {
        }

        Button merchantButton = (Button) findViewById(R.id.nav_footer_merchant);
        merchantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMechantLogin = new Intent(ProfileActivity.this, MerchantLoginActivity.class);
                startActivity(intentMechantLogin);
            }
        });

        Button deliveryButton = (Button) findViewById(R.id.nav_footer_delivery);
        deliveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDelivery = new Intent(ProfileActivity.this, DeliveryActivity.class);
                startActivity(intentDelivery);            }
        });


        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grandResults) {

        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_MULTIPLE: {
                if (grandResults.length > 0 && grandResults[0] == PackageManager.PERMISSION_GRANTED) {

                    try {
                        updateProfileButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                progressDialog = new ProgressDialog(ProfileActivity.this);
                                progressDialog.setMessage("Uploading, please wait...");
                                progressDialog.show();
                                 imageUpload(picturePath);
                                uploadMultipart();
                            }
                        });

                    } catch (SecurityException e)

                    {
                        e.printStackTrace();

                    }

                } else {

                    Toast.makeText(this, "User denied permissions", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            stringUri = filePath.toString();

            try {
                //getting image from gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(filePath,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                cursor.close();

                //Setting image to ImageView
                userProfileImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
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

  /*  private void profileNetworkRequest(String UserID) {
        final String USERID = UserID;
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.godprice.com/api/my-account.php?userid=29";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                        }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error Occurred" + error, Toast.LENGTH_SHORT).show();

            }

        }) { @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("userid", USERID);
            return params;
        }
        };
        queue.add(stringRequest);
    } */

    private void loginNetworkRequest(String UserID) {
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
                            session.setuserImage(userImage);
                            sessionUserImage = session.getuserImage();
                            EditText firstName = (EditText) findViewById(R.id.first_name_et);
                            firstName.setText(userName);

                            EditText mobileNumber = (EditText) findViewById(R.id.mobile_et);
                            mobileNumber.setText(userMobile);

                            EditText email = (EditText) findViewById(R.id.email_et);
                            email.setText(userEmail);

                            Glide.with(userProfileImage.getContext())
                                    .load(userImage)
                                    .into(userProfileImage);

                            navigationView = findViewById(R.id.nav_view);
                            navigationView.getMenu().clear();
                            navigationView.inflateMenu(R.menu.drawer_view);
                            View header = navigationView.getHeaderView(0);
                            ImageView loggedInUserImage = header.findViewById(R.id.user_image_header);
                            Glide.with(loggedInUserImage.getContext())
                                    .load(userImage)
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

    private void updateProfileRequest(String UserID) {

        final String nameUpdated = nameEditText.getText().toString().trim();
        final String emailUpdated = emailEditText.getText().toString().trim();
        final String mobileUpdated = mobileEditText.getText().toString().trim();

        final String USERID = UserID;
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.godprice.com/api/update_profile.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            String jsonResponse = response.toString().trim();
                            jsonResponse = jsonResponse.substring(3);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            String status = jsonObject.getString("status");
                            int statusInt = Integer.parseInt(status);
                            String message = jsonObject.getString("message");
                            if (statusInt == 200) {
                                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.response_message_linear);
                                linearLayout.setVisibility(View.VISIBLE);
                                linearLayout.setBackgroundColor(Color.parseColor("#9f64dd17"));
                                TextView responseTextViewTwo = (TextView) findViewById(R.id.response_message_two);
                                responseTextViewTwo.setText(message);

                                JSONObject dataJsonObject = jsonObject.getJSONObject("data");
                                String userId = dataJsonObject.getString("id");
                                String userName = dataJsonObject.getString("name");
                                String userEmail = dataJsonObject.getString("email");
                                String userMobile = dataJsonObject.getString("mobile");
                                String userImage = dataJsonObject.getString("profileimage");

                                EditText firstName = (EditText) findViewById(R.id.first_name_et);
                                firstName.setText(userName);

                                EditText mobileNumber = (EditText) findViewById(R.id.mobile_et);
                                mobileNumber.setText(userMobile);

                                EditText email = (EditText) findViewById(R.id.email_et);
                                email.setText(userEmail);

                                Glide.with(userProfileImage.getContext())
                                        .load(userImage)
                                        .into(userProfileImage);

                            } else if (statusInt == 201) {
                                //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.response_message_linear);
                                linearLayout.setVisibility(View.VISIBLE);
                                TextView responseTextViewTwo = (TextView) findViewById(R.id.response_message_two);
                                responseTextViewTwo.setText(message);
                            }

                            }catch(Exception e) {
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
            params.put("name", nameUpdated);
            params.put("email", emailUpdated);
            params.put("mobile", mobileUpdated);
          //  params.put("image", imageString);
            return params;
        }

        };
        queue.add(stringRequest);
    }

    private void imageUpload(final String imagePath) {

        /* SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            String jsonResponse = response.toString().trim();
                            jsonResponse = jsonResponse.substring(3);
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            String statusResponse = jsonObject.getString("status");
                            int statusInt = Integer.parseInt(statusResponse);
                            //  String message = jsonObject.getString("message");
                            if (statusInt == 200) {

                                Toast.makeText(ProfileActivity.this, "Uploaded Successful", Toast.LENGTH_LONG).show();
                            } else if (statusInt == 201) {
                                //   String message = jsonObject.getString("message");
                                Toast.makeText(ProfileActivity.this, "Some error occurred!", Toast.LENGTH_LONG).show();
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
                Toast.makeText(ProfileActivity.this, "Some error occurred", Toast.LENGTH_LONG).show();;
            }
        });
        // smr.addStringParam("param string", " data text");
        smr.addFile("profileimage", imagePath);
        //  smr.addStringParam("userid", "7");
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        mRequestQueue.add(smr); */
    }

    /*
     * This is the method responsible for image upload
     * We need the full image path and the name for the image in this method
     * */
    public void uploadMultipart() {
        //getting name for the image
        //  String name = editText.getText().toString().trim();

        //getting the actual path of the image
        //  String path = getPath(filePath);

        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request
            new MultipartUploadRequest(this, uploadId, URL)
                    .addFileToUpload(picturePath, "profileimage") //Adding file
                    .addParameter("userid", sessionToken) //Adding text parameter to the request
                  //  .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload

        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}


