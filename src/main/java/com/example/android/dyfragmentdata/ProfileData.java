package com.example.android.dyfragmentdata;

public class ProfileData {

        // Variable declaration

    private String mProductId;
        private String mProductName;
        private String mEmailAddress;
        private String mMobile;

        // created constructor with two parameters templeName and templeLocation

        public ProfileData(String ProductId, String ProductName, String EmailAddress, String Mobile) {

            // Variable initial values

            mProductId = ProductId;
            mProductName = ProductName;
            mEmailAddress = EmailAddress;
            mMobile = Mobile;

            }

        // Get method to return temple name

        public String getProductId() {
            return  mProductId;
        }

        public String getProductName() {
            return mProductName;
        }

        // Get method to return temple location

    public String getEmailAddress() {
            return mEmailAddress;
        }

        public String getMobile() {
            return  mMobile;
        }
        }

