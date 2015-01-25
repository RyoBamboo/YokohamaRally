package com.dr.yokohamarally.core;

import android.widget.EditText;

import com.dr.yokohamarally.R;

public class MinLengthValidatable implements Validatable {
    // default length
    private int minText;

    public MinLengthValidatable(int minText) {
        this.minText = minText;
    }

    @Override
    public ValidateDto isValid(EditText editText) {
        boolean isValid = true;

        if (editText.getText().length() < minText) {
            isValid = false;
        }

        ValidateDto ret = new ValidateDto(isValid, minText);
        return ret;
    }

    @Override
    public String getTailMessage() {
        return String.valueOf(minText);
    }

}
