package com.example.android.dyfragmentdata;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddAddressActivity extends AppCompatActivity {
    private Session session;
    private String sessionToken;
    private DrawerLayout mDrawerLayout;
    private EditText fullNameEditText;
    private EditText emailEditText;
    private EditText phoneNumberEditText;
    private EditText countryEditText;
    private EditText cityEditText;
    private EditText zipCodeEditText;
    private EditText addressEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content of the activity to use the activity_category.xml layout file
        setContentView(R.layout.activity_add_address);

       fullNameEditText = (EditText) findViewById(R.id.full_name_et_checkout);
       emailEditText = (EditText) findViewById(R.id.email_et_checkout);
       phoneNumberEditText = (EditText) findViewById(R.id.phone_number_et_checkout);
       countryEditText = (EditText) findViewById(R.id.country_checkout_et);
       cityEditText = (EditText) findViewById(R.id.city_checkout_et);
       zipCodeEditText = (EditText) findViewById(R.id.zip_code_checkout_et);
       addressEditText = (EditText) findViewById(R.id.address_two_checkout_et);

        session = new Session(this);
        sessionToken = session.getusertoken();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#e53935"));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        ActionBar actionbar = getSupportActionBar();

        if (actionbar !=null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.back_arrow);
        }

        toolbar.findViewById(R.id.toolbar_title);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddAddressActivity.this, HomepageActivity.class);
                startActivity(intent);
            }
        });

        Button addAddressButton = (Button) findViewById(R.id.save_deliver_here_checkout);
        addAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String fullName = fullNameEditText.getText().toString();
              String email = emailEditText.getText().toString();
              String phoneNumber = phoneNumberEditText.getText().toString();
              String country = countryEditText.getText().toString();
              String city = cityEditText.getText().toString();
              String zipCode = zipCodeEditText.getText().toString();
              String address = addressEditText.getText().toString();
                addAddressNetworkRequest();

            /*  if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(country) || TextUtils.isEmpty(city) || TextUtils.isEmpty(zipCode) || TextUtils.isEmpty(address))
              {
              Toast.makeText(AddAddressActivity.this, "Please enter all the required details", Toast.LENGTH_SHORT).show();

              } else {
                  addAddressNetworkRequest();

              } */
              }
        });
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
            case android.R.id.home:
                Intent checkoutIntent = new Intent(AddAddressActivity.this, CheckoutActivity.class);
                startActivity(checkoutIntent);
                return true;
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addAddressNetworkRequest() {

        final String fullName = fullNameEditText.getText().toString();
        final String email = emailEditText.getText().toString();
        final String phoneNumber = phoneNumberEditText.getText().toString();
        final String country = countryEditText.getText().toString();
        final String city = cityEditText.getText().toString();
        final String zipCode = zipCodeEditText.getText().toString();
        final String address = addressEditText.getText().toString();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.godprice.com/api/address_add.php";
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
                                String addressId = jsonObject.getString("addressid");
                              //  Toast.makeText(getApplicationContext(), "Address registered successfully.", Toast.LENGTH_LONG).show();
                                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.response_message_linear);
                                linearLayout.setVisibility(View.VISIBLE);
                                linearLayout.setBackgroundColor(Color.parseColor("#9f64dd17"));
                                TextView responseTextViewTwo = (TextView) findViewById(R.id.response_message_two);
                                responseTextViewTwo.setText(message);
                                Intent intentCheckout = new Intent(AddAddressActivity.this, CheckoutActivity.class);
                                startActivity(intentCheckout);

                            } else if (statusInt == 201) {
                                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.response_message_linear);
                                linearLayout.setVisibility(View.VISIBLE);
                                TextView responseTextViewTwo = (TextView) findViewById(R.id.response_message_two);
                                responseTextViewTwo.setText(message);
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
            params.put("userid", sessionToken);
            params.put("fullname", fullName);
            params.put("emailid", email);
            params.put("phoneno", phoneNumber);
            params.put("country", country);
            params.put("zipcode", zipCode);
            params.put("city", city);
            params.put("address", address);
            return params;
        }
        };
        queue.add(stringRequest);
    }
}
