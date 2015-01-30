package com.dr.yokohamarally.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.dr.yokohamarally.R;

/**
 * Created by Masashi on 2015/01/30.
 */
public class FormActivity extends Activity{

    private EditText[] pointTitle = new EditText[10];
    private  String[] pointString = new String[10];
    private String[] pointAdress = new String[10];

    private int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_main);

        String[] pointStringCopy = getArrayFromSharedPreference("formPoint");
        String[] pointAdressCopy = getArrayFromSharedPreference("pointAdress");

        Intent intent = getIntent();
        int number = intent.getIntExtra("pointId" ,0);
        Log.d("number", number + "");
        count = number;


        if(pointAdressCopy != null){
            for( int j=0; j<pointAdressCopy.length; j++)pointAdress[j]=pointAdressCopy[j];
        }

        for( int i=0; i<= count; i++) {
            if(pointStringCopy != null) {
                pointString[i] = pointStringCopy[i];
            }

            purasuForm(i);

        }



//        //ライン作成
//        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
//        View view = getLayoutInflater().inflate(R.layout.form_line, null);
//        TextView line =(TextView)view.findViewById(R.id.point_line);
//        line.setText("ポイント"+(1));
//        layout.addView(view);
//
//        //ポイントフォーム作成
//        view = getLayoutInflater().inflate(R.layout.activity_form, null);
//        pointTitle[0] = (EditText)view.findViewById(R.id.name);
//        layout.addView(view);


        final Button img_button = (Button)findViewById(R.id.select);
        img_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(int i = 0; i <= count ; i++){
                    SpannableStringBuilder sb = (SpannableStringBuilder)pointTitle[i].getText();
                    pointString[i] = sb.toString();
                }
                saveArrayToSharedPreference(pointString ,"formPoint");
                Intent intent = new Intent(FormActivity.this, InputMapActivity.class);
                intent.putExtra("pointId", count);
                intent.putExtra("myId", v.getId());
                startActivity(intent);

            }
        });



        final Button purasu_button = (Button)findViewById(R.id.purasu_button);
        purasu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                count++;
                purasuForm();

            }
        });

//        Button form_button = (Button)view.findViewById(R.id.form_point);
//        form_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                for(int i = 0; i < count ; i++){
//                    SpannableStringBuilder sb = (SpannableStringBuilder)pointTitle[i].getText();
//                    pointString[i] = sb.toString();
//                }
//                saveArrayToSharedPreference(pointString ,"formPoint");
//
//
//                Intent intent = new Intent(FormActivity.this, InputMapActivity.class);
//                intent.putExtra("rootId", count);
//                startActivity(intent);
//            }
//        });




    }

    public void purasuForm(int i){

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        View view = getLayoutInflater().inflate(R.layout.form_line, null);
        TextView line =(TextView)view.findViewById(R.id.point_line);
        line.setText("ポイント"+(i+1));
        layout.addView(view);
        view = getLayoutInflater().inflate(R.layout.activity_form, null);
        pointTitle[i] = (EditText)view.findViewById(R.id.name);
        if(pointString[i] != null){
            pointTitle[i].setText(pointString[i]);
        }else{
            pointTitle[i].setText("未登録");
        }
        TextView adress = (TextView)view.findViewById(R.id.form_point_text);
        adress.setText(pointAdress[i]);
        Button point_button = (Button)view.findViewById(R.id.form_point);
        point_button.setId(i);
        point_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i <= count ; i++){
                    SpannableStringBuilder sb = (SpannableStringBuilder)pointTitle[i].getText();
                    pointString[i] = sb.toString();
                }
                saveArrayToSharedPreference(pointString ,"formPoint");
                Intent intent = new Intent(FormActivity.this, InputMapActivity.class);
                intent.putExtra("pointId", count);
                intent.putExtra("myId", v.getId());
                startActivity(intent);
            }
        });
        layout.addView(view);

    }

    public void purasuForm(){

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        View view = getLayoutInflater().inflate(R.layout.form_line, null);
        TextView line =(TextView)view.findViewById(R.id.point_line);
        line.setText("ポイント"+(count+1));
        layout.addView(view);
        view = getLayoutInflater().inflate(R.layout.activity_form, null);
        pointTitle[count] = (EditText)view.findViewById(R.id.name);
        Button point_button = (Button)view.findViewById(R.id.form_point);
        point_button.setId(count);
        point_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i <= count ; i++){
                    SpannableStringBuilder sb = (SpannableStringBuilder)pointTitle[i].getText();
                    pointString[i] = sb.toString();
                }
                saveArrayToSharedPreference(pointString ,"formPoint");
                Intent intent = new Intent(FormActivity.this, InputMapActivity.class);
                intent.putExtra("pointId", count);
                intent.putExtra("myId", v.getId());
                startActivity(intent);
            }
        });
        layout.addView(view);

    }



    /*---------------------------------------------------------
     * SharedPreferenceに配列の保存と読み込みを可能にする関数。
     * TODO: できれば他のクラスからも参照できるようにリファクタリングしたい
     *-------------------------------------------------------*/
    public void saveArrayToSharedPreference(String[] array, String key) {

        // ','をキーとして連結
        StringBuffer buffer = new StringBuffer();
        for (String value : array) {
            buffer.append(value + ",");
        }

        String stringItem = null;
        if (buffer != null) {
            String buf = buffer.toString();
            stringItem = buf.substring(0, buf.length() - 1);

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            sp.edit().putString(key, stringItem).commit();
        }
    }

    public String[] getArrayFromSharedPreference(String key) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String stringItem = sp.getString(key, "");
        if (stringItem != null && stringItem.length() != 0) {
            return  stringItem.split(",");
        }

        return null;
    }

}
