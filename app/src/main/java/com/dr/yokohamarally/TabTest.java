package com.dr.yokohamarally;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class TabTest extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tab);
        FragmentTabHost host = (FragmentTabHost) findViewById(android.R.id.tabhost);
        host.setup(this, getSupportFragmentManager(), R.id.content);

        TabSpec tabSpec1 = host.newTabSpec("tab1");
        Button button1 = new Button(this);
        tabSpec1.setIndicator(button1);
        Bundle bundle1 = new Bundle();
        bundle1.putString("name", "Tab1");
        host.addTab(tabSpec1, SampleFragment.class, bundle1);

        TabSpec tabSpec2 = host.newTabSpec("tab2");
        Button button2 = new Button(this);
        button2.setBackgroundResource(R.drawable.rupe);
        tabSpec2.setIndicator(button2);
        Bundle bundle2 = new Bundle();
        bundle2.putString("name", "Tab2");
        host.addTab(tabSpec2, SampleFragment.class, bundle2);

        TabSpec tabSpec3 = host.newTabSpec("tab3");
        Button button3 = new Button(this);
        button3.setBackgroundResource(R.drawable.rupe);
        tabSpec3.setIndicator(button3);
        Bundle bundle3 = new Bundle();
        bundle3.putString("name", "Tab3");
        host.addTab(tabSpec3, SampleFragment.class, bundle3);
    }

    public static class SampleFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = null;
            if(getArguments().getString("name").equals("Tab1") ) {
                v = inflater.inflate(R.layout.activity_main, container, false);
            }
            if(getArguments().getString("name").equals("Tab2") ) {
                v = inflater.inflate(R.layout.fragment_second_tab, container, false);
            }
            if(getArguments().getString("name").equals("Tab3" +
                    "") ) {
                v = inflater.inflate(R.layout.fragment_third_tab, container, false);
            }
            return v;
        }

    }
}