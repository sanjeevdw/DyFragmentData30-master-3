package com.example.android.dyfragmentdata;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    private SharedPreferences prefs;

    public Session(Context context) {

        prefs = PreferenceManager.getDefaultSharedPreferences(context);

    }

    public void setusename(String usename) {

        prefs.edit().putString("usename", usename).commit();

    }

    public void setUserEmail(String userEmail) {

        prefs.edit().putString("userEmail", userEmail).commit();

    }

    public void setDeliveryToken(String deliveryToken) {

        prefs.edit().putString("deliveryToken", deliveryToken).commit();
    }

    public void setusertoken(String userToken) {

        prefs.edit().putString("userToken", userToken).commit();
    }

    public void setuserImage(String userImage) {

        prefs.edit().putString("userImage", userImage).commit();
    }

    public void setuserWalletAmount(String userWalletAmount) {

        prefs.edit().putString("userWalletAmount", userWalletAmount).commit();
    }

    public String getusename() {

        String usename = prefs.getString("usename", "");
        return usename;
    }

    public String getUserEmail() {

        String userEmail = prefs.getString("userEmail", "");
        return userEmail;
    }

    public String getusertoken() {

        String userToken = prefs.getString("userToken", "");
        return userToken;
        }

    public String getuserImage() {

        String userImage = prefs.getString("userImage", "");
        return userImage;
    }

    public String getDeliveryToken() {

        String deliveryToken = prefs.getString("deliveryToken", "");
        return deliveryToken;
    }

    public String getuserWalletAmount() {

        String userWalletAmount = prefs.getString("userWalletAmount", "");
        return userWalletAmount;
    }
}
