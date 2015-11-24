package com.app.jobaloon.utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.app.jobaloon.main.R;


/**
 * @author Sreejith SP
 */
public class MyProgressDialog {
    private Dialog progressDialog;

    public MyProgressDialog(Activity activity) {
        progressDialog = new Dialog(activity);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        progressDialog.setContentView(R.layout.progress);
//        ProgressWheel wheel = new ProgressWheel(activity);
//        wheel.setBarColor(Color.BLUE);
    }

    public void setProgress() {
        if (progressDialog != null) progressDialog.show();
    }

    public void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
