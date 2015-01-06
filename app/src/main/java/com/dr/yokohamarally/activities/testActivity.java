package com.dr.yokohamarally.activities;

import android.app.Activity;
import android.app.Application;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dr.yokohamarally.R;
import com.dr.yokohamarally.fragments.ImagePopup;

public class testActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        Button button = (Button) findViewById(R.id.test_button);
        // ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ダイアログを表示する
                DialogFragment newFragment = new ImagePopup();
                newFragment.show(getFragmentManager(), "test1");

            }
        });
    }


}
