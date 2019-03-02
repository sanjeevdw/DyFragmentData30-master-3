package com.example.android.dyfragmentdata;

public class OrderDetailData {

    // Variable declaration

    private String mProductID;
    private String mFullName;
    private String mEmailId;
    private String mPhoneno;
    private String mAddress;
    private String mCountry;
    private String mCity;
    private String mZipcode;
    private String mPhoneno_alternative;
    private String mProductName;
    private String mProductSize;
    private String mProductColor;
    private String mQuantity;
    private String mSubTotal;
    private String mTotalAmountValue;
    private String mInvoiceNo;

    public OrderDetailData(String ProductID, String InvoiceNo, String FullName, String EmailId, String Phoneno, String Address, String Country, String City, String Zipcode, String Phoneno_alternative,
    String ProductName, String ProductSize, String ProductColor, String Quantity, String SubTotal, String TotalAmountValue) {

        // Variable initial values
        mProductID = ProductID;
        mInvoiceNo = InvoiceNo;
        mFullName = FullName;
        mEmailId = EmailId;
        mPhoneno = Phoneno;
        mAddress = Address;
        mCountry = Country;
        mCity = City;
        mZipcode = Zipcode;
        mPhoneno_alternative = Phoneno_alternative;
        mProductName = ProductName;
        mProductSize = ProductSize;
        mProductColor = ProductColor;
        mQuantity = Quantity;
        mSubTotal = SubTotal;
        mTotalAmountValue = TotalAmountValue;
        }

    // Get method to return order history items

    public String getProductID() {
        return  mProductID;
    }

    public String getInvoiceNo() {
        return  mInvoiceNo;
    }

    public String getFullName() {
        return  mFullName;
    }

    public String getEmailId() {
        return  mEmailId;
    }

    public String getPhoneno() {
        return mPhoneno;
    }

    // Get method to return order history items

    public String getAddress() {
        return mAddress;
    }

    public String getCountry() {
        return  mCountry;
    }

    public String getCity() {
        return  mCity;
    }

    public String getZipcode() {
        return  mZipcode;
    }

    public String getPhoneno_alternative() {
        return  mPhoneno_alternative;
    }

    public String getProductName() {
        return  mProductName;
    }

    public String getProductSize() {
        return  mProductSize;
    }

    public String getProductColor() {
        return  mProductColor;
    }

    public String getQuantity() {
        return  mQuantity;
    }

    public String getSubTotal() {
        return  mSubTotal;
    }

    public String getTotalAmountValue() {
        return  mTotalAmountValue;
    }
}

