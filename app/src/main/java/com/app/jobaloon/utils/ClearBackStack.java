package com.app.jobaloon.utils;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Created by SICS-Dpc2 on 27-Jan-15.
 */
public class ClearBackStack {
    public ClearBackStack(FragmentActivity _activity) {
        FragmentManager manager = _activity.getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
}
