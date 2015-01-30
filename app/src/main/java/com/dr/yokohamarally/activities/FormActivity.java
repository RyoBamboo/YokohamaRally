package com.dr.yokohamarally.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableStringBuilder;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.dr.yokohamarally.R;
import com.dr.yokohamarally.fragments.BitmapHolder;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by Masashi on 2015/01/30.
 */
public class FormActivity extends Activity{

    private EditText[] pointTitle = new EditText[10];
    private  String[] pointString = new String[10];
    private String[] pointAdress = new String[10];
    private static final int IMAGE_CAPTURE = 101;
    private static int GETTRIM = 10;
    private String bmpString;
    private SharedPreferences sp;

    private int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_main);


        sp = PreferenceManager.getDefaultSharedPreferences(this);
        String mBmpString  = sp.getString("samarry_image", "");
        String mRarryName  = sp.getString("rarry_name", "");
        String mRarrySammary  = sp.getString("rarry_sammary", "");

        EditText rarryName = (EditText)findViewById(R.id.rarry_name);
        EditText rarrySammary = (EditText)findViewById(R.id.rarry_sammary);
        rarryName.setText(mRarryName);
        rarrySammary.setText(mRarrySammary);

        if(!("".equals(mBmpString))){
            byte[] b = Base64.decode(mBmpString, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
            ImageView img = (ImageView)findViewById(R.id.image_sammary);
            img.setImageBitmap(bmp);
        }else{
            ImageView img = (ImageView)findViewById(R.id.image_sammary);
            img.setImageResource(R.drawable.ookami);
        }


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

                for (int i = 0; i <= count; i++) {
                    SpannableStringBuilder sb = (SpannableStringBuilder) pointTitle[i].getText();
                    pointString[i] = sb.toString();
                }
                EditText rarryName = (EditText)findViewById(R.id.rarry_name);
                SpannableStringBuilder sb = (SpannableStringBuilder) rarryName.getText();
                sp.edit().putString("rarry_name", sb.toString()).commit();

                EditText rarrySammary = (EditText)findViewById(R.id.rarry_sammary);
                SpannableStringBuilder sb2 = (SpannableStringBuilder) rarrySammary.getText();
                sp.edit().putString("rarry_sammary", sb2.toString()).commit();

                saveArrayToSharedPreference(pointString, "formPoint");

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, IMAGE_CAPTURE);

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

                EditText rarryName = (EditText)findViewById(R.id.rarry_name);
                SpannableStringBuilder sb = (SpannableStringBuilder) rarryName.getText();
                sp.edit().putString("rarry_name", sb.toString()).commit();

                EditText rarrySammary = (EditText)findViewById(R.id.rarry_sammary);
                SpannableStringBuilder sb2 = (SpannableStringBuilder) rarrySammary.getText();
                sp.edit().putString("rarry_sammary", sb2.toString()).commit();

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
                saveArrayToSharedPreference(pointString, "formPoint");

                EditText rarryName = (EditText)findViewById(R.id.rarry_name);
                SpannableStringBuilder sb = (SpannableStringBuilder) rarryName.getText();
                sp.edit().putString("rarry_name", sb.toString()).commit();

                EditText rarrySammary = (EditText)findViewById(R.id.rarry_sammary);
                SpannableStringBuilder sb2 = (SpannableStringBuilder) rarrySammary.getText();
                sp.edit().putString("rarry_sammary", sb2.toString()).commit();

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_CAPTURE) {
            try {
                InputStream in = getContentResolver().openInputStream(data.getData());
                Bitmap bmp = BitmapFactory.decodeStream(in);
                in.close();

                Intent _intent = new Intent(getApplicationContext(),TrimingActivity.class);
                BitmapHolder._holdedBitmap = bmp;
                startActivityForResult(_intent,GETTRIM);

            } catch (Exception e) {

            }
        } else if (requestCode == GETTRIM) {
            ImageView profile = (ImageView)findViewById(R.id.image_sammary);
            profile.setImageBitmap(BitmapHolder._holdedBitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BitmapHolder._holdedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            bmpString = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            sp.edit().putString("samarry_image", bmpString).commit();
        }
    }

}
