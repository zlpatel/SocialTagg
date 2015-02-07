package com.socialtagg.fragments;


import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.socialtagg.R;
import com.socialtagg.adapters.AdapterValidateSpinner;
import com.socialtagg.domain.Event;
import com.socialtagg.domain.Profile;
import com.socialtagg.managers.ManagerApplication;
import com.socialtagg.util.Consts;
import com.socialtagg.util.UrlBuilder;
import com.stripe.android.model.Card;
import com.stripe.android.util.TextUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zeel on 8/29/14.
 */

public class FragmentTicketingPaymentProcessEnterInfo extends SherlockFragment implements View.OnClickListener {

    private TextView mTextViewTicketType;
    private TextView mTextViewTicketQty;
    private TextView mTextViewTicketPriceSingle;
    private TextView mTextViewTicketPriceTotal;
    private EditText mEditTextNameFirst;
    private EditText mEditTextNameLast;
    private EditText mEditTextEmail;
    private EditText mEditTextZipCode;
    private EditText mEditTextCreditCardNumber;
    private EditText mEditTextCVV;
    private Spinner mSpinnerMonth;
    private Spinner mSpinnerYear;
    private Button mButtonNextStep;
    private ImageView mImageCreditCards;
    private Drawable errorIcon;
    private Card card;

    private String eventName;
    private String eventDate;
    private String eventTime;
    private String eventLocation;
    private String ticketType;
    private double single_price;
    private int qty;
    private double total_price;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.view_payment_process_enter_info, null);

        Event mEvent=(Event)getActivity().getIntent().getSerializableExtra("event");

        eventName=mEvent.getEventName(); //"Party";
        Date date=mEvent.getStartTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
        eventDate=dateFormat.format(date); //"04/21/2015";
        eventTime=timeFormat.format(date); //"20:00";
        eventLocation=mEvent.getAddress(); //"San Jose,CA";
        ticketType="General Admission";
        single_price=Double.valueOf("5.54");
        qty=Integer.valueOf("1");
        total_price=single_price*qty;

        setupUIComponents(view);

        populateStaticFields();

        return view;
    }

    private void populateStaticFields() {
        mTextViewTicketType.setText(ticketType+" Ticket");
        mTextViewTicketPriceSingle.setText(String.valueOf(single_price));
        mTextViewTicketQty.setText(String.valueOf(qty));
        mTextViewTicketPriceTotal.setText(String.valueOf(total_price));
    }

    private void setupUIComponents(View view) {
        mTextViewTicketType=(TextView)view.findViewById(R.id.label_ticket_type_page_1);
        mTextViewTicketQty=(TextView)view.findViewById(R.id.label_ticket_quantity_page_1);
        mTextViewTicketPriceSingle=(TextView)view.findViewById(R.id.label_ticket_price_single_page_1);
        mTextViewTicketPriceTotal=(TextView)view.findViewById(R.id.label_ticket_price_total_page_1);
        mEditTextNameFirst=(EditText)view.findViewById(R.id.payment_enter_info_first_name);
        mEditTextNameLast=(EditText)view.findViewById(R.id.payment_enter_info_last_name);
        mEditTextEmail=(EditText)view.findViewById(R.id.payment_enter_email);
        mEditTextZipCode=(EditText)view.findViewById(R.id.payment_enter_info_zip_code);
        mEditTextCreditCardNumber=(EditText)view.findViewById(R.id.payment_enter_info_credit_card_number);
        mEditTextCVV=(EditText)view.findViewById(R.id.payment_enter_info_ccv);
        mSpinnerMonth=(Spinner)view.findViewById(R.id.spinner_month);
        mSpinnerYear=(Spinner)view.findViewById(R.id.spinner_year);
        mButtonNextStep=(Button)view.findViewById(R.id.button_next_step);
        mImageCreditCards=(ImageView)view.findViewById(R.id.image_creditcards);

        errorIcon=getResources().getDrawable(R.drawable.warning_icon_small);
        errorIcon.setBounds(new Rect(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight()));

        mTextViewTicketPriceTotal.setText(String.valueOf(new DecimalFormat("#0.00").format(total_price)));


        mEditTextNameFirst.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEditTextNameFirst.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEditTextNameLast.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEditTextNameLast.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mButtonNextStep.setOnClickListener(this);

        ArrayList<Integer> monthList = new ArrayList<Integer>();
        for (int i = 1; i <= 12; i++)
        {
            monthList.add(i);
        }

        AdapterValidateSpinner monthAdapter=new AdapterValidateSpinner(getActivity(),android.R.layout.simple_spinner_item, monthList);
        mSpinnerMonth.setAdapter(monthAdapter);

        ArrayList<Integer> yearList = new ArrayList<Integer>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i <= 10; i++)
        {
            yearList.add(thisYear+i);
        }
        AdapterValidateSpinner yearAdapter=new AdapterValidateSpinner(getActivity(),android.R.layout.simple_spinner_item, yearList);
        mSpinnerYear.setAdapter(yearAdapter);



        //pre populating first name, last name and email of the user
        Profile myProfile = ManagerApplication.getManagerProfile().getMyProfile();

        if (myProfile.getNameFirst() != null){
            String firstName = myProfile.getNameFirst();
            mEditTextNameFirst.setText(firstName);
        }
        if(myProfile.getNameLast() != null){
            String lastName= myProfile.getNameLast();
            mEditTextNameLast.setText(lastName);
        }
        if(myProfile.getEmail() !=null){
            String email=myProfile.getEmail();
            mEditTextEmail.setText(email);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_next_step:
                validateFields();
        }
    }

    /**
     * Perform Validation of all the fields
     */
    private void validateFields() {

        if(TextUtils.isBlank(mEditTextNameFirst.getText().toString()) || TextUtils.isBlank(mEditTextNameLast.getText().toString()) || TextUtils.isBlank(mEditTextEmail.getText().toString()) || TextUtils.isBlank(mEditTextCreditCardNumber.getText().toString()) || TextUtils.isBlank(mEditTextZipCode.getText().toString()) || TextUtils.isBlank(mEditTextCVV.getText().toString()) ){
            if(TextUtils.isBlank(mEditTextNameFirst.getText().toString())){
                mEditTextNameFirst.setError("Mandatory field",errorIcon);
            }
            if(TextUtils.isBlank(mEditTextNameLast.getText().toString())){
                mEditTextNameLast.setError("Mandatory field",errorIcon);
            }
            if(TextUtils.isBlank(mEditTextEmail.getText().toString())){
                mEditTextEmail.setError("Mandatory field",errorIcon);
            }
            if(!TextUtils.isBlank(mEditTextEmail.getText().toString()) && !android.util.Patterns.EMAIL_ADDRESS.matcher(mEditTextEmail.getText().toString()).matches()){
                mEditTextEmail.setError("Invalid email address",errorIcon);
            }
            if(TextUtils.isBlank(mEditTextZipCode.getText().toString())){
                mEditTextZipCode.setError("Mandatory field",errorIcon);
            }
            if(TextUtils.isBlank(mEditTextCreditCardNumber.getText().toString())){
                mEditTextCreditCardNumber.setError("Mandatory field",errorIcon);
            }
            if(TextUtils.isBlank(mEditTextCVV.getText().toString())){
                mEditTextCVV.setError("Mandatory field",errorIcon);
            }

        }
        else{

            String creditCardNum=mEditTextCreditCardNumber.getText().toString();
            Integer expMonth=Integer.parseInt(mSpinnerMonth.getSelectedItem().toString());
            Integer expYear=Integer.parseInt(mSpinnerYear.getSelectedItem().toString());
            String CVV=mEditTextCVV.getText().toString();

            card=new Card(creditCardNum,expMonth,expYear,CVV);

            if(!card.validateCard()){
                if(!card.validateNumber()){
                    mEditTextCreditCardNumber.setError("Invalid number",errorIcon);
                }
                if(!card.validateCVC()){
                    mEditTextCVV.setError("Invalid CVC number",errorIcon);
                }
                if(!card.validateExpiryDate()){
                    AdapterValidateSpinner adapter = (AdapterValidateSpinner)mSpinnerMonth.getAdapter();
                    View view = mSpinnerMonth.getSelectedView();
                    adapter.setError(view, "Expiry Date can't be in the past",errorIcon);

                }
            }
            else{
                //go To review fragment upon validating all the fields
                goToReviewFragment();
            }

        }
    }

    /**
     * Go to Review Fragment
     */
    private void goToReviewFragment() {

        Fragment f = new FragmentTicketingPaymentProcessReview();

        Bundle args = new Bundle();

        args.putString(Consts.KEY_EVENT_NAME,eventName);
        args.putString(Consts.KEY_EVENT_DATE,eventDate);
        args.putString(Consts.KEY_EVENT_TIME,eventTime);
        args.putString(Consts.KEY_EVENT_LOCATION,eventLocation);
        args.putString(Consts.KEY_EVENT_TICKET_TYPE,mTextViewTicketType.getText().toString());
        args.putString(Consts.KEY_EVENT_TICKET_PRICE_SINGLE,mTextViewTicketPriceSingle.getText().toString());
        args.putString(Consts.KEY_EVENT_TICKET_QTY,mTextViewTicketQty.getText().toString());
        args.putString(Consts.KEY_EVENT_TICKET_PRICE_TOTAL,mTextViewTicketPriceTotal.getText().toString());
        args.putString(Consts.KEY_EVENT_TICKET_BUYER_NAME,mEditTextNameFirst.getText().toString()+" "+mEditTextNameLast.getText().toString());
        args.putString(Consts.KEY_EVENT_TICKET_BUYER_EMAIL,mEditTextEmail.getText().toString());
        args.putString(Consts.KEY_EVENT_TICKET_BUYER_ZIP,mEditTextZipCode.getText().toString());
        args.putString(Consts.KEY_EVENT_CREDIT_CARD_NUMBER,card.getNumber());
        args.putString(Consts.KEY_EVENT_CREDIT_CARD_CVV_NUMBER,card.getCVC());
        args.putInt(Consts.KEY_EVENT_CREDIT_CARD_EXP_MONTH,card.getExpMonth());
        args.putInt(Consts.KEY_EVENT_CREDIT_CARD_EXP_YEAR,card.getExpYear());

        f.setArguments(args);

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.payment_process_fragment_container,f);
        ft.addToBackStack(null);
        ft.commit();


    }


}