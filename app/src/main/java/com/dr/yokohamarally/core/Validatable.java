package com.dr.yokohamarally.core;

import android.widget.EditText;

public interface Validatable {

    ValidateDto isValid(EditText editText);

    String getTailMessage();
}
