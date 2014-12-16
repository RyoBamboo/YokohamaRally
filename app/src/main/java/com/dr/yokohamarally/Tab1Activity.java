package com.dr.yokohamarally;

import android.os.Bundle;
import android.widget.TextView;
import android.app.Activity;

/**
 * Created by Masashi on 2014/12/16.
 */
public class Tab1Activity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView view = new TextView(this);
        view.setText("Tab1");
        setContentView(view);
    }



}
