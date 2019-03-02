package com.example.android.dyfragmentdata;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class TransactionAdapter extends ArrayAdapter<TransactionData> {

    public TransactionAdapter(Context context, ArrayList<TransactionData> transactionDetailData) {
        super(context, 0, transactionDetailData);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {

        View itemListView = convertView;

        if (itemListView == null) {

            itemListView = LayoutInflater.from(getContext()).inflate(R.layout.transaction_items, parent, false);
        }

        TransactionData CurrentTransactionData = getItem(position);

        TextView TransactionView = (TextView) itemListView.findViewById(R.id.transaction_value);
        TransactionView.setText(CurrentTransactionData.getTransaction());

        TextView ForView = (TextView) itemListView.findViewById(R.id.for_value);
        ForView.setText(CurrentTransactionData.getFor());

        TextView EventView = (TextView) itemListView.findViewById(R.id.event_value);
        EventView.setText(CurrentTransactionData.getEvent());

        TextView ModeView = (TextView) itemListView.findViewById(R.id.mode_value);
        ModeView.setText(CurrentTransactionData.getMode());

        TextView AmountView = (TextView) itemListView.findViewById(R.id.amount_value);
        AmountView.setText(CurrentTransactionData.getAmount());

        TextView DateView = (TextView) itemListView.findViewById(R.id.date_value);
        DateView.setText(CurrentTransactionData.getDate());

        TextView statusView = (TextView) itemListView.findViewById(R.id.status_value);
        statusView.setText(CurrentTransactionData.getStatus());

        View textContainer = itemListView.findViewById(R.id.text_container);

        return itemListView;
    }
}
