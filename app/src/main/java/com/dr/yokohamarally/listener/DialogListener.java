package com.dr.yokohamarally.listener;

import java.util.EventListener;

public interface DialogListener extends EventListener {

    public void onPositiveClick(String tag);

    public void onNegativeClick(String tag);

}
