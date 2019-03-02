package com.example.android.dyfragmentdata;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.SimpleFormatter;

public class OrderHistoryListingAdapter extends ArrayAdapter<OrderHistoryListingData> {

    public OrderHistoryListingAdapter(Context context, ArrayList<OrderHistoryListingData> orderHistoryItems) {
        super(context, 0, orderHistoryItems);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {

        View itemListView = convertView;

        if (itemListView == null) {

            itemListView = LayoutInflater.from(getContext()).inflate(R.layout.order_history_items, parent, false);
        }

        OrderHistoryListingData CurrentOrderHistoryListingData = getItem(position);

      //  TextView SerialNoView = (TextView) itemListView.findViewById(R.id.serial_no);
     //   SerialNoView.setText(CurrentOrderHistoryListingData.getSerialNo());

        TextView InvoiceView = (TextView) itemListView.findViewById(R.id.invoice_no);
        InvoiceView.setText(CurrentOrderHistoryListingData.getInvoice());

        String dateOrder = CurrentOrderHistoryListingData.getDate();

        try {
            ;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
            Date date = formatter.parse(dateOrder);
            formatter.applyPattern("dd/mm/yyyy");
            String newFormat = formatter.format(date);
            TextView DateView = (TextView) itemListView.findViewById(R.id.date);
            DateView.setText(newFormat);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextView TotalView = (TextView) itemListView.findViewById(R.id.total);
        TotalView.setText(CurrentOrderHistoryListingData.getTotal());

        TextView StatusView = (TextView) itemListView.findViewById(R.id.status);
        StatusView.setText(CurrentOrderHistoryListingData.getStatus());

        Button detailsButton = (Button) itemListView.findViewById(R.id.action_view);
        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v, position, 0);
            }
        });

        View textContainer = itemListView.findViewById(R.id.text_container);

        return itemListView;
    }
}
