package com.app.jobaloon.main;

import android.app.ActionBar;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.app.jobaloon.UtilsGcm;
import com.app.jobaloon.utils.MyCrouton;
import com.app.jobaloon.utils.UncaughtExceptionReporter;


public class BaseFragmentActivity extends FragmentActivity {
    private long back_pressed = 0;
    private MyCrouton myCrouton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//      to send error report
        Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionReporter(
                getApplicationContext()));


//      Google Analytics
        Tracker t = ((Jobaloon) getApplication()).getTracker(Jobaloon.TrackerName.APP_TRACKER);
        t.setScreenName("Jobaloon");
        t.send(new HitBuilders.AppViewBuilder().build());

        setContentView(R.layout.fragment_base);
//        getDeviceToken();
        myCrouton = new MyCrouton();
        // adding action bar
        ActionBar mActionBar = getActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setHomeButtonEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(false);
        mActionBar.setBackgroundDrawable(new ColorDrawable(R.color.blue));

        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);

        // Do stuff to Title Bar //

        TextView mTitleTextView = (TextView) mCustomView
                .findViewById(R.id.titleText);
        TextView upload_video_left_text = (TextView) mCustomView.findViewById(R.id.upload_video_left_text);
        ImageView mRightImage = (ImageView) mCustomView
                .findViewById(R.id.rightActionImage);
        ImageView mLeftImage = (ImageView) mCustomView
                .findViewById(R.id.leftActionImage);
        mLeftImage.setVisibility(View.VISIBLE);
        mRightImage.setVisibility(View.GONE);
        Typeface font = Typeface.createFromAsset(getAssets(), "Hero Light.otf");
        mTitleTextView.setTypeface(font);
        upload_video_left_text.setTypeface(font);
        mActionBar.setCustomView(mCustomView);
        View v = mActionBar.getCustomView();
        mLeftImage.setImageResource(R.drawable.action_bar_icon);
        mActionBar.setDisplayShowCustomEnabled(true);
        ViewGroup.LayoutParams layoutParams = mCustomView.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        v.setLayoutParams(layoutParams);
        replaceFragment(new WelcomePage(), false, FragmentTransaction.TRANSIT_ENTER_MASK, "WelcomePage");
    }

    private void replaceFragment(Fragment fragment, boolean addToBackStack,
                                 int transition, String name) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.setTransition(transition);
        if (addToBackStack)
            fragmentTransaction.addToBackStack(name);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        int stackSize = getSupportFragmentManager().getBackStackEntryCount();
        if (stackSize == 0) {
            if (back_pressed + 2000 > System.currentTimeMillis())
                super.onBackPressed();
            else
                myCrouton.showCrouton(BaseFragmentActivity.this,
                        getResources().getString(R.string.press_again_to_exit), 1);
            back_pressed = System.currentTimeMillis();
        } else
            super.onBackPressed();
    }

    private void getDeviceToken() {
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        String regId = GCMRegistrar.getRegistrationId(this);
        int check = 0;
        if (regId.equals("")) {
            GCMRegistrar.register(this, UtilsGcm.GCMSenderId);
        } else {
            check = 10;
            Log.v("", "Already registered: " + regId);
        }
        String deviceTocken;
        if (check == 10) {
            deviceTocken = regId;
            UtilsGcm.registrationId = regId;
        } else {
            deviceTocken = UtilsGcm.registrationId;
            UtilsGcm.registrationId = regId;
        }
    }
}
