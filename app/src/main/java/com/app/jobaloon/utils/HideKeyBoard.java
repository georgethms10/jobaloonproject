package com.app.jobaloon.utils;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

/**
 * @author Sreejith SP
 */
public class HideKeyBoard {
    public HideKeyBoard(Activity activity) {
        if(activity.getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager)activity. getSystemService(activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
}
