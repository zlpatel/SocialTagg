package com.socialtagg.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.socialtagg.R;
import com.socialtagg.activities.ActivityContent;
import com.socialtagg.activities.ActivityTicketingPaymentProcess;
import com.socialtagg.domain.Event;
import com.socialtagg.managers.ManagerApplication;
import com.socialtagg.managers.ManagerCache;
import com.socialtagg.net.NetworkResultsListener;
import com.socialtagg.util.Consts;
import com.socialtagg.util.Logger;
import com.socialtagg.views.MainContentView;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.Stripe;
import com.stripe.android.model.Token;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Charge;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by zeel on 9/5/14.
 */
public class FragmentTicketingPaymentProcessReview extends SherlockFragment implements View.OnClickListener {

    private TextView mEventName;
    private TextView mEventDate;
    private TextView mEventTime;
    private TextView mEventLocation;
    private TextView mTicketType;
    private TextView mTicketQuantity;
    private TextView mTicketPriceSingle;
    private TextView mTicketPriceTotal;
    private TextView mBuyerName;
    private TextView mBuyerEmail;
    private TextView mBillingZip;
    private TextView mCreditCard4Digits;
    private TextView mExpiryDate;
    private Button mButtonCompleteOrder;
    private Bundle getData;
    private Card card;
    private double totalPrice;
    private int totalPriceInCents;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_payment_process_review, null);
        mEventName=(TextView)view.findViewById(R.id.label_Event_Name_value);
        mEventDate=(TextView)view.findViewById(R.id.label_Event_Date_value);
        mEventTime=(TextView)view.findViewById(R.id.label_Event_Time_value);
        mEventLocation=(TextView)view.findViewById(R.id.label_Event_Location_value);
        mTicketType=(TextView)view.findViewById(R.id.label_ticket_price_single_value);
        mTicketQuantity=(TextView)view.findViewById(R.id.label_ticket_quantity_number);
        mTicketPriceTotal=(TextView)view.findViewById(R.id.label_ticket_price_total_page_2);
        mTicketPriceSingle=(TextView)view.findViewById(R.id.label_ticket_price_single_value);
        mBuyerName=(TextView)view.findViewById(R.id.label_Name_value);
        mBuyerEmail=(TextView)view.findViewById(R.id.label_Email_value);
        mBillingZip=(TextView)view.findViewById(R.id.label_zip_code_value);
        mCreditCard4Digits=(TextView)view.findViewById(R.id.label_credit_card_4_digits_value);
        mExpiryDate=(TextView)view.findViewById(R.id.label_exp_date_value);
        mButtonCompleteOrder=(Button)view.findViewById(R.id.button_complete_order);

        mButtonCompleteOrder.setOnClickListener(this);

        getData= getArguments();

        mEventName.setText(getData.getString(Consts.KEY_EVENT_NAME));
        mEventDate.setText(getData.getString(Consts.KEY_EVENT_DATE));
        mEventTime.setText(getData.getString(Consts.KEY_EVENT_TIME));
        mEventLocation.setText(getData.getString(Consts.KEY_EVENT_LOCATION));
        mTicketType.setText(getData.getString(Consts.KEY_EVENT_TICKET_TYPE));
        mTicketQuantity.setText(getData.getString(Consts.KEY_EVENT_TICKET_QTY));
        mTicketPriceSingle.setText("$"+getData.getString(Consts.KEY_EVENT_TICKET_PRICE_SINGLE));

        totalPrice=Double.valueOf(getData.getString(Consts.KEY_EVENT_TICKET_PRICE_TOTAL));
        //convert totalPrice to cents and store it as an int
        //because the "amount" attribute of class Change only accepts int value and that too in cents
        totalPriceInCents=(int)(totalPrice*100);
        mTicketPriceTotal.setText("$"+totalPrice);

        mBuyerName.setText(getData.getString(Consts.KEY_EVENT_TICKET_BUYER_NAME));
        mBuyerEmail.setText(getData.getString(Consts.KEY_EVENT_TICKET_BUYER_EMAIL));
        mBillingZip.setText(getData.getString(Consts.KEY_EVENT_TICKET_BUYER_ZIP));

        card=new Card(getData.getString(Consts.KEY_EVENT_CREDIT_CARD_NUMBER),getData.getInt(Consts.KEY_EVENT_CREDIT_CARD_EXP_MONTH),getData.getInt(Consts.KEY_EVENT_CREDIT_CARD_EXP_YEAR),getData.getString(Consts.KEY_EVENT_CREDIT_CARD_CVV_NUMBER));

        mCreditCard4Digits.setText(card.getLast4());
        mExpiryDate.setText(card.getExpMonth()+"/"+card.getExpYear());

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_complete_order:
                completeOrder();

        }
    }

    /**
     * Perform background processing for completing order using Stripe API
     */
    private void completeOrder() {

        try {
            Stripe stripe=new Stripe("pk_live_DeoiTtzfHgFVtKJLYeXli0eB");
            stripe.createToken(
                    card,
                    new TokenCallback() {
                        public void onSuccess(Token token){
                            //Token successfully created.
                            //Create a charge or save token to the server and use it later
                            final Map<String, Object> chargeParams = new HashMap<String, Object>();
                            chargeParams.put("amount", totalPriceInCents);
                            chargeParams.put("currency", "usd");
                            chargeParams.put("card", token.getId());

                            new AsyncTask<Void, String, Void>() {
                                boolean successfulChargeFlag=true;
                                Charge charge;

                                @Override
                                protected Void doInBackground(Void... params) {
                                    try {
                                        com.stripe.Stripe.apiKey = " sk_live_M55EFEcHACcpaVyOYzRkGbeA";
                                        charge = Charge.create(chargeParams);
                                    } catch (Exception e) {
                                        // TODO Auto-generated catch block
                                        successfulChargeFlag=false;
                                        e.printStackTrace();
                                        publishProgress(e.getLocalizedMessage());
                                    }
                                    return null;
                                }

                                @Override
                                protected void onProgressUpdate(String... values) {
                                    Toast.makeText(getActivity(),values[0],Toast.LENGTH_LONG).show();

                                }

                                protected void onPostExecute(Void result) {
                                    //Must be performed only when the charge is successful
                                    if(successfulChargeFlag){
                                        Toast.makeText(getActivity(),
                                                "Card Charged : " + charge.getCreated() + "\nPaid : " +charge.getPaid(),
                                                Toast.LENGTH_LONG
                                        ).show();

                                        //Set Order number here
                                        getData.putString(Consts.KEY_ORDER_NUMBER,"1234567890");

                                        registerUserForEvent();
                                        goToNextFragment();
                                    }

                                };

                            }.execute();



                        }
                        public void onError(Exception error) {
                            // Show localized error message
                            Toast.makeText(getActivity(),
                                    error.getLocalizedMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
            );
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }

    }

    private void registerUserForEvent() {

        Event mEvent=(Event)getActivity().getIntent().getSerializableExtra("event");

        Logger.d("register");
        // after register call comes back successfully, gray out button
        // Gray out "Register" after they click the button. add the event to their "my events"
        ManagerApplication.getManagerWebServices().registerForEvent(mEvent, false, ManagerApplication.getManagerProfile().getMyProfile(), new NetworkResultsListener()
        {
            @Override
            public void onEventResults(Object results)
            {
                Logger.d("beginEventRegistration onEventResults");

                if (getActivity() != null)
                {
                    ((MainContentView) getActivity()).hideProgress();
                }

                if (results == null || results instanceof Exception)
                {
//                    displayToast(getString(R.string.registration_failed));
                }
                else
                {
                    ManagerApplication.getManagerCache().remove(ManagerCache.createMyEventsCacheKey()); // cachekey cleared for myevents on success
                    // results cast the results TODO should parse registration object

//                    displayToast(getString(R.string.registration_success));

                }
            }
        });
    }

    /**
     * Going to the confirmation fragment
     */
    private void goToNextFragment() {
        Fragment f = new FragmentTicketingPaymentProcessConfirmation();
        f.setArguments(getData);
        FragmentManager manager = getActivity().getSupportFragmentManager();

        //To avoid going back from confirmation fragment to any of the previous fragments when clicked on the back button
        manager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.payment_process_fragment_container, f);
        ft.commit();

    }

    private void displayToast(String msg)
    {
        if (getActivity() != null)
        {
            ((ActivityTicketingPaymentProcess) getActivity()).displayToast(msg);
        }
    }
}
