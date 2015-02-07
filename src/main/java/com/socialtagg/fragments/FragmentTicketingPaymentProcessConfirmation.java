package com.socialtagg.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.socialtagg.R;
import com.socialtagg.activities.ActivityContent;
import com.socialtagg.managers.ManagerEmail;
import com.socialtagg.util.Consts;
import com.socialtagg.util.Logger;

/**
 * Created by zeel on 9/9/14.
 */
public class FragmentTicketingPaymentProcessConfirmation extends SherlockFragment implements View.OnClickListener {

    private TextView mEventName;
    private TextView mOrderNumber;
    private TextView mTicketType;
    private TextView mTicketQuantity;
    private TextView mTicketPriceSingle;
    private TextView mTicketPriceTotal;
    private TextView mConfirmationEmail;
    String orderNumber;
    private Button mSeeEventDetails;
    private Bundle getData;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_payment_process_confirmation, null);

        mEventName=(TextView)view.findViewById(R.id.label_event_name_page_3);
        mOrderNumber=(TextView)view.findViewById(R.id.label_order_number_value);
        mTicketType=(TextView)view.findViewById(R.id.label_ticket_type_name_page_3);
        mTicketQuantity=(TextView)view.findViewById(R.id.label_ticket_quantity_number_page_3);
        mTicketPriceSingle=(TextView)view.findViewById(R.id.label_ticket_price_single_value_page_3);
        mTicketPriceTotal=(TextView)view.findViewById(R.id.label_ticket_price_total_page_3);
        mConfirmationEmail=(TextView)view.findViewById(R.id.label_confirmation_email);
        mSeeEventDetails=(Button)view.findViewById(R.id.button_see_event_details);

        mSeeEventDetails.setOnClickListener(this);

        getData=getArguments();

        orderNumber=getData.getString(Consts.KEY_ORDER_NUMBER);
        mOrderNumber.setText("#"+orderNumber);
        mEventName.setText(getData.getString(Consts.KEY_EVENT_NAME));
        mTicketType.setText(getData.getString(Consts.KEY_EVENT_TICKET_TYPE));
        mTicketQuantity.setText(getData.getString(Consts.KEY_EVENT_TICKET_QTY));
        mTicketPriceSingle.setText("$"+getData.getString(Consts.KEY_EVENT_TICKET_PRICE_SINGLE));
        mTicketPriceTotal.setText("$"+getData.getString(Consts.KEY_EVENT_TICKET_PRICE_TOTAL));
        mConfirmationEmail.setText(getData.getString(Consts.KEY_EVENT_TICKET_BUYER_EMAIL));

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_see_event_details:
                goToSeeEventDetails();


        }
    }

    private void goToSeeEventDetails() {

        Logger.d("Entered event Details");
        getActivity().finish();
//        getActivity().onBackPressed();


    }


}
