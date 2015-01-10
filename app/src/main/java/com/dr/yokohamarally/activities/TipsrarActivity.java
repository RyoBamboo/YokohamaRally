package com.dr.yokohamarally.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import com.dr.yokohamarally.R;
import com.dr.yokohamarally.fragments.BitmapHolder;

public class TipsrarActivity extends Activity {

    private static int GETTRIM = 1;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        Intent _intent = new Intent(getApplicationContext(),TrimingActivity.class);

        BitmapHolder._holdedBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ookami);
        startActivityForResult(_intent,GETTRIM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GETTRIM){
            if(resultCode == RESULT_OK){
                Toast.makeText(this, "画像の取得に成功しました。\nトリミングおめでとう", Toast.LENGTH_SHORT).show();
                BitmapHolder._holdedBitmap = null;
            }else{
                Toast.makeText(this, "画像の取得に失敗しました。", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}