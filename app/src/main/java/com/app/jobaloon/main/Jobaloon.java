package com.app.jobaloon.main;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.securepreferences.SecurePreferences;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.security.GeneralSecurityException;
import java.util.HashMap;

/**
 * Created by SICS-Dpc2 on 16-May-15.
 */
public class Jobaloon extends Application {

    private static final String PROPERTY_ID = "UA-63048163-1";

    private static final String TAG = "secureprefsample";
    protected static Jobaloon instance;
    private SecurePreferences mSecurePrefs;
    private SecurePreferences mUserPrefs;

    public Jobaloon() {
        super();
        instance = this;
    }

    public static Jobaloon get() {
        return instance;
    }

    /**
     * Single point for the app to get the secure prefs object
     *
     * @return
     */

    public SharedPreferences getSharedPreferences() {
        if (mSecurePrefs == null) {
            mSecurePrefs = new SecurePreferences(this, "", "my_prefs.xml");
            SecurePreferences.setLoggingEnabled(true);
        }
        return mSecurePrefs;
    }

    public SharedPreferences getDefaultSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    public SharedPreferences getSharedPreferences1000() {
        try {
            AesCbcWithIntegrity.SecretKeys myKey = AesCbcWithIntegrity.generateKeyFromPassword(Build.SERIAL,AesCbcWithIntegrity.generateSalt(),1000);
            SharedPreferences securePrefs1000 = new SecurePreferences(this, myKey, "my_prefs_1000.xml");
            return securePrefs1000;
        } catch (GeneralSecurityException e) {
            Log.e(TAG, "Failed to create custom key for SecurePreferences", e);
        }
        return null;
    }


    /**
     * Enum used to identify the tracker that needs to be used for tracking.
     * <p/>
     * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
     * storing them all in Application object helps ensure that they are created only once per
     * application instance.
     */
    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
                    : analytics.newTracker(R.xml.ecommerce_tracker);
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }
}
