package com.app.jobaloon.utils;

import android.widget.CheckBox;
import android.widget.EditText;

/**
 * @author Sreejith SP
 */
public class Validation {

    public boolean validateEditTexts(EditText... editTexts) {
        for (int i = 0; i < editTexts.length; i++) {
            if (editTexts[i].getText().toString().equals("")) {
                return false;
            }
        }
        return true;
    }

    public boolean validateAnyCheckBoxes(CheckBox... checkBoxes) {
        for (int i = 0; i < checkBoxes.length; i++) {
            if (checkBoxes[i].isChecked()) {
                return true;
            }
        }
        return false;
    }

    public boolean validateAnyEditText(EditText... editTexts) {
        for (int i = 0; i < editTexts.length; i++) {
            if (!editTexts[i].getText().toString().equals("")) {
                return true;
            }
        }
        return false;
    }

    public final boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
                    .matches();
        }
    }
}
