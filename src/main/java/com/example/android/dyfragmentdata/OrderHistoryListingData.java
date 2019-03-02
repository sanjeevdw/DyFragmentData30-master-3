package com.example.android.dyfragmentdata;

public class OrderHistoryListingData {

    // Variable declaration

   // private String mSerialNo;
    private String mInvoice;
    private String mDate;
    private String mTotal;
    private String mStatus;

    public OrderHistoryListingData(String Invoice, String Date, String Total, String Status) {

        // Variable initial values

    //    mSerialNo = SerialNo;
        mInvoice = Invoice;
        mDate = Date;
        mTotal = Total;
        mStatus = Status;

    }

    // Get method to return order history items

  //  public String getSerialNo() {
  //      return  mSerialNo;
 //   }

    public String getInvoice() {
        return  mInvoice;
    }

    public String getDate() {
        return mDate;
    }

    // Get method to return order history items

    public String getTotal() {
        return mTotal;
    }

    public String getStatus() {
        return  mStatus;
    }
}
