package com.app.jobaloon.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.jobaloon.main.Jobaloon;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Shadila on 20-Nov-15.
 */
public class SaveSecurePreference {
    Context con;
    String key, value;
    private SharedPreferences mSecurePrefs;

    public SaveSecurePreference(Context context) {
        this.con = context;
        //Secure preferences
        mSecurePrefs = Jobaloon.get().getSharedPreferences();
        Jobaloon.get().getSharedPreferences1000();
        updateEncValueDisplay();
        mSecurePrefs
                .registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(
                            SharedPreferences sharedPreferences, String key) {
                        updateEncValueDisplay();
                    }
                });


    }

    public void setPrefValue(String key, String value) {
        this.key = key;
        this.value = value;
        getSharedPref().edit().putString(key, value)
                .commit();
    }

    public String getPrefValue(String key) {
        final String value = getSharedPref().getString(key, null);
        return value;
    }

    private SharedPreferences getSharedPref() {
        if (mSecurePrefs == null) {
            mSecurePrefs = Jobaloon.get().getSharedPreferences();
        }
        return mSecurePrefs;
    }

    private void updateEncValueDisplay() {
        Map<String, ?> all = getSharedPref().getAll();
        StringBuilder builder = new StringBuilder();

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
        Date resultdate = new Date(System.currentTimeMillis());
        builder.append("updated: " + sdf.format(resultdate) + "\n");

        if (!all.isEmpty()) {

            Set<String> keys = all.keySet();
            Iterator<String> it = keys.iterator();
            while (it.hasNext()) {
                String key = it.next();
                builder.append("prefkey:" + key);
                Object value = all.get(key);
                if (value instanceof String) {
                    builder.append("\nprefvalue:" + (String) value);
                }
                builder.append("\n\n");
            }
        } else {
            builder.append("\nEMPTY");

        }
    }
}
