package com.example.android.dyfragmentdata;

public class TransactionData {

    // Variable declaration

    private String mTransaction;
    private String mFor;
    private String mEvent;
    private String mMode;
    private String mAmount;
    private String mDate;
    private String mStatus;

    public TransactionData(String Transaction, String For, String Event, String Mode, String Amount, String Date, String Status) {

        // Variable initial values
        mTransaction = Transaction;
        mFor = For;
        mEvent = Event;
        mMode = Mode;
        mAmount = Amount;
        mDate = Date;
        mStatus = Status;
        }

    // Get method to return order history items

    public String getTransaction() {
        return  mTransaction;
    }

    public String getFor() {
        return  mFor;
    }

    public String getEvent() {
        return  mEvent;
    }

    public String getMode() {
        return mMode;
    }

    // Get method to return order history items

    public String getAmount() {
        return mAmount;
    }

    public String getDate() {
        return  mDate;
    }

    public String getStatus() {
        return  mStatus;
    }
}
