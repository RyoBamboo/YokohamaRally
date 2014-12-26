package com.dr.yokohamarally.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.dr.yokohamarally.R;

/**
 * Created by Masashi on 2014/12/15.
 */
public class SubActivity extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub);
        Intent intent = getIntent();
        String id = intent.getStringExtra( "id" );
        id = "引き渡された数は" + id;
        TextView tv;
        tv = (TextView)this.findViewById(R.id.textView2);
        tv.setText(id);
    }

}
