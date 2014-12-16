package com.dr.yokohamarally;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

/**
 * Created by Masashi on 2014/12/16.
 */
public class TabTest extends TabActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        initTabs();
    }

    protected void initTabs(){

        Resources res = getResources();
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;

        // Tab1
        intent = new Intent().setClass(this, Tab1Activity.class);
        spec = tabHost.newTabSpec("Tab1").setIndicator(
                "Tab1", res.getDrawable(R.drawable.rupe))
                .setContent(intent);
        tabHost.addTab(spec);

        // Tab2
        intent = new Intent().setClass(this, Tab2Activity.class);
        spec = tabHost.newTabSpec("Tab2").setIndicator(
                "Tab2" , res.getDrawable(R.drawable.rupe))
                .setContent(intent);
        tabHost.addTab(spec);

        // Tab3
        intent = new Intent().setClass(this, Tab3Activity.class);
        spec = tabHost.newTabSpec("Tab3").setIndicator(
                "Tab3", res.getDrawable(R.drawable.rupe))
                .setContent(intent);
        tabHost.addTab(spec);

        // Set Default Tab - zero based index
        tabHost.setCurrentTab(0);

    }


}
