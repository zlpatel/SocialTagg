package com.socialtagg.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.socialtagg.R;
import com.socialtagg.fragments.FragmentTicketingPaymentProcessEnterInfo;
import com.socialtagg.util.Logger;

/**
 * Created by zeel on 8/26/14.
 */
public class ActivityTicketingPaymentProcess extends SherlockFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.d("ENTER");
        setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticketing_payment_process);

        // add the badge fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment f = new FragmentTicketingPaymentProcessEnterInfo();
        ft.replace(R.id.payment_process_fragment_container, f);
        ft.commit();

    }

    /**
     * Displays a message in the center of the screen
     * @param msg
     */
    public void displayToast(String msg)
    {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0,0);
        toast.show();
    }
}