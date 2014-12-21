package com.dr.yokohamarally;

import com.navdrawer.SimpleSideDrawer;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

public class tets extends Activity {

    private SimpleSideDrawer mNav;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNav = new SimpleSideDrawer(this);
        mNav.setLeftBehindContentView(R.layout.fragment_first_tab);
        findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mNav.toggleLeftDrawer();
            }
        });

        View view = findViewById(R.id.lineup);
        view.setOnTouchListener(new FlickTouchListener());





    }

    private float lastTouchX;
    private float currentX;
    private class FlickTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastTouchX = event.getX();
                    break;

                case MotionEvent.ACTION_UP:
                    currentX = event.getX();
                    if (lastTouchX - currentX < -80) {
                        //前に戻る動作
                        mNav.toggleLeftDrawer();
                    }
                    if (lastTouchX > currentX) {
                        //次に移動する動作
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    currentX = event.getX();
                    if (lastTouchX - currentX < -80) {
                        //前に戻る動作
                        mNav.toggleLeftDrawer();
                    }
                    if (lastTouchX > currentX) {
                        //次に移動する動作
                    }
                    break;
            }
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem actionItem = menu.add("Action Button");
        actionItem.setIcon(android.R.drawable.ic_menu_share);
        return true;
    }

}