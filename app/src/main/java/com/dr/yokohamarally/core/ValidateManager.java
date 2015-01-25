package com.dr.yokohamarally.core;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidateManager {
    private Map<EditText, Validators> validateMap;
    private Context context;

    public ValidateManager(Context context) {
        this.context = context;
        validateMap = new HashMap<EditText, Validators>();
    }

    public void add(EditText editText, Validators validators) {
        validateMap.put(editText, validators);
    }

    public List<String> validate() {
        List<String> errorMessages = new ArrayList<String>();

        for(Map.Entry<EditText, Validators> keyValue : validateMap.entrySet()) {
            Validators validators = keyValue.getValue();
            EditText editText = keyValue.getKey();

            for (Validatable validatable : validators.as_list()) {
                ValidateDto validateResult = validatable.isValid(editText);
                if (validateResult.isValid()) {
                    continue;
                }

                // validate error
                StringBuffer errStr =  new StringBuffer();
                errStr.append(editText.getContentDescription().toString())
                        .append(context.getString(validateResult.getMessage()));
                if (validatable.getTailMessage() != null) {
                    errStr.append(validatable.getTailMessage());
                }

                Log.d("errorMessage", errStr.toString());
                errorMessages.add(errStr.toString());
            }
        }

        return errorMessages;
    }
}
