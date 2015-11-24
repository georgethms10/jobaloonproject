package com.app.jobaloon.utils;

import android.app.Activity;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class MyCrouton {

    public void showCrouton(Activity _activity, String croutonText, int type) {
        Crouton crouton = null;
        if (type == 1) {
            crouton = Crouton.makeText(_activity, croutonText, Style.ALERT).setConfiguration(new Configuration.Builder().setDuration(800).build());
        } else if (type == 2)
            crouton = Crouton.makeText(_activity, croutonText, Style.CONFIRM).setConfiguration(new Configuration.Builder().setDuration(800).build());
        crouton.show();
    }
}
