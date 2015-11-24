package com.app.jobaloon.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @author Srishti Innovative
 */
public class AppPreferences {
    private SharedPreferences appSharedPrefs;
    private Editor prefsEditor;

    @SuppressLint("CommitPrefEdits")
    public AppPreferences(Context context, String Preferncename) {
        this.appSharedPrefs = context.getSharedPreferences(Preferncename,
                Activity.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    /**
     * delete all AppPreference
     */
    public void deleteAll() {
        this.prefsEditor = appSharedPrefs.edit();
        this.prefsEditor.clear();
        this.prefsEditor.commit();
    }

    /**
     * *
     * <p/>
     * getdata() get the value from the preference
     */
    public String getData(String key) {
        return appSharedPrefs.getString(key, "");
    }

    /**
     * *
     * <p/>
     * SaveData() save the value to the preference
     */
    public void saveData(String Tag, String text) {
        prefsEditor.putString(Tag, text);
        prefsEditor.commit();
    }

    public int getIntData(String key) {
        return appSharedPrefs.getInt(key, 0);
    }

    /**
     * *
     * <p/>
     * SaveData() save the value to the preference
     */
    public void saveIntData(String text, Integer Tag) {
        // prefsEditor.putString(Tag, text);
        prefsEditor.putInt(text, Tag);
        prefsEditor.commit();
    }

    public void saveFloatData(String text, Float Tag) {
        // prefsEditor.putString(Tag, text);
        prefsEditor.putFloat(text, Tag);
        prefsEditor.commit();
    }

    public float getFloatData(String key) {
        // return appSharedPrefs.getString(key, "");
        float defValue = 1000;
        return appSharedPrefs.getFloat(key, defValue);
    }

    public void saveBooleanData(String text, Boolean Tag) {
        prefsEditor.putBoolean(text, Tag);
        prefsEditor.commit();
    }

    public boolean getBooleantData(String key) {
        return appSharedPrefs.getBoolean(key, false);
    }
}