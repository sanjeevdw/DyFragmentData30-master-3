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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderDetailAdapter extends ArrayAdapter<OrderDetailData> {

    public OrderDetailAdapter(Context context, ArrayList<OrderDetailData> orderDetailData) {
        super(context, 0, orderDetailData);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {

        View itemListView = convertView;

        if (itemListView == null) {

            itemListView = LayoutInflater.from(getContext()).inflate(R.layout.order_detail_items, parent, false);
        }

        OrderDetailData CurrentOrderDetailData = getItem(position);

        TextView productIdView = (TextView) itemListView.findViewById(R.id.cart_id);
        productIdView.setText(CurrentOrderDetailData.getProductID());
        productIdView.setVisibility(View.INVISIBLE);

        TextView invoiceNoView = (TextView) itemListView.findViewById(R.id.invoice_no);
        invoiceNoView.setText(CurrentOrderDetailData.getInvoiceNo());

        TextView fullNameView = (TextView) itemListView.findViewById(R.id.full_name);
        fullNameView.setText(CurrentOrderDetailData.getFullName());

        TextView emailIdView = (TextView) itemListView.findViewById(R.id.email);
        emailIdView.setText(CurrentOrderDetailData.getEmailId());

        TextView mobileView = (TextView) itemListView.findViewById(R.id.mobile);
        mobileView.setText(CurrentOrderDetailData.getPhoneno());

        TextView addressView = (TextView) itemListView.findViewById(R.id.address);
        addressView.setText(CurrentOrderDetailData.getAddress());

        TextView countryView = (TextView) itemListView.findViewById(R.id.country);
        countryView.setText(CurrentOrderDetailData.getCountry());

        TextView cityView = (TextView) itemListView.findViewById(R.id.city);
        cityView.setText(CurrentOrderDetailData.getCity());

        TextView zipcodeView = (TextView) itemListView.findViewById(R.id.zipcode);
        zipcodeView.setText(CurrentOrderDetailData.getZipcode());

        TextView phoneNoView = (TextView) itemListView.findViewById(R.id.alternate_contact_no);
        phoneNoView.setText(CurrentOrderDetailData.getPhoneno_alternative());

        TextView productNameView = (TextView) itemListView.findViewById(R.id.product_name);
        productNameView.setText(CurrentOrderDetailData.getProductName());

      //  TextView productSizeView = (TextView) itemListView.findViewById(R.id.product_size);
     //   productSizeView.setText(" " + CurrentOrderDetailData.getProductSize());

       // TextView productColorView = (TextView) itemListView.findViewById(R.id.product_color);
      //  productColorView.setText(CurrentOrderDetailData.getProductColor());

        TextView quantityView = (TextView) itemListView.findViewById(R.id.quantity_value);
        quantityView.setText(CurrentOrderDetailData.getQuantity());

        TextView subTotalView = (TextView) itemListView.findViewById(R.id.sub_total_value);
        subTotalView.setText(CurrentOrderDetailData.getSubTotal());

        TextView totalAmountView = (TextView) itemListView.findViewById(R.id.total_amount_value);
        totalAmountView.setText(CurrentOrderDetailData.getTotalAmountValue());

        View textContainer = itemListView.findViewById(R.id.text_container);

        Button buttonBack = (Button) itemListView.findViewById(R.id.back_label);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v, position, 0);
            }
        });

        Button chatButton = (Button) itemListView.findViewById(R.id.chat_button_label);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v, position, 0);
            }
        });

        return itemListView;
    }
}

