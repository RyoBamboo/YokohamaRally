package com.dr.yokohamarally.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dr.yokohamarally.R;
import com.dr.yokohamarally.fragments.BitmapHolder;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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
    private String pointStr;

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
            img.setImageResource(R.drawable.noimage);
        }


        final String[] pointStringCopy = getArrayFromSharedPreference("formPoint");
        final String[] pointAdressCopy = getArrayFromSharedPreference("pointAdress");

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

                if (count < 4){
                    count++;
                    purasuForm();
                }else{
                    Toast.makeText(getApplicationContext(), "ポイントの最大数は５です", Toast.LENGTH_SHORT).show();

                }

            }
        });




        final Button passive_button = (Button)findViewById(R.id.submit);
        passive_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText rarryName = (EditText)findViewById(R.id.rarry_name);
                SpannableStringBuilder sb = (SpannableStringBuilder) rarryName.getText();

                EditText rarrySammary = (EditText)findViewById(R.id.rarry_sammary);
                SpannableStringBuilder sb2 = (SpannableStringBuilder) rarrySammary.getText();
                if("".equals(sb.toString())){
                    oneDo("ラリー名を記入してください",-1);
                    return;
                }
                if("".equals(sb2.toString())){
                    oneDo("ラリー概要を記入してください",-1);
                    return;
                }

                for(int i = 0; i <= count ; i++){

                    SpannableStringBuilder sb3 = (SpannableStringBuilder)pointTitle[i].getText();
                    pointString[i] = sb3.toString();
                    System.out.println(i+1 + " "+pointString[i] );
                    if("未登録".equals(pointAdress[i]) ||"".equals(pointAdress[i]) || pointAdress[i]==null){
                        oneDo("の場所をしてしてください",i+1);
                        return;
                    }
                    if("".equals(pointString[i]) || pointString[i] == null){
                        oneDo("のポイント名を記入してください",i+1);
                        return;
                    }
                }


                checkDialog();


            }



        });

        final Button mainasu_button = (Button)findViewById(R.id.mainasu_button);
        mainasu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (count > 0){
                    LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
                    layout.removeViewAt(count);
                    count--;
                }else{
                    Toast.makeText(getApplicationContext(), "１つはポイントを入力してください", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    public void purasuForm(int i){

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        View view = getLayoutInflater().inflate(R.layout.activity_form, null);
        TextView line =(TextView)view.findViewById(R.id.point_line);
        line.setText("ポイント"+(i+1));

        pointTitle[i] = (EditText)view.findViewById(R.id.name);
        pointTitle[i].setText(pointString[i]);
        TextView adress = (TextView)view.findViewById(R.id.form_point_text);
        if(pointAdress !=null)adress.setText(pointAdress[i]);
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
        View view = getLayoutInflater().inflate(R.layout.activity_form, null);

        TextView line =(TextView)view.findViewById(R.id.point_line);
        line.setText("ポイント"+(count+1));
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

    public void oneDo(String message , int i){

        if(i == -1 )Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        if(i > -1)Toast.makeText(getApplicationContext(), "ポイント"+i+message, Toast.LENGTH_LONG).show();


    }

    public void checkDialog(){

        new AlertDialog.Builder(FormActivity.this)
                .setTitle("投稿してよろしいですか？")
                .setPositiveButton(
                        "はい",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO ここに書いてほしい
                                EditText rarryName = (EditText)findViewById(R.id.rarry_name);
                                SpannableStringBuilder sb = (SpannableStringBuilder) rarryName.getText();
                                final String rootName = sb.toString(); //TODO  ルート名

                                EditText rarrySammary = (EditText)findViewById(R.id.rarry_sammary);
                                SpannableStringBuilder sb2 = (SpannableStringBuilder) rarrySammary.getText();
                                final String rootSummary = sb2.toString(); //TODO ルート概要

                                final String[] pointLatitude = getArrayFromSharedPreference("pointLatitude"); //TODO Latitude
                                final String[] pointLongitude =getArrayFromSharedPreference("pointLongitude");//TODO Longitude

                                final String  image = sp.getString("samarry_image", ""); //TODO image

                                /*
                                for(int i = 0; i <= count ; i++){
                                    SpannableStringBuilder sb3 = (SpannableStringBuilder)pointTitle[i].getText();
                                    pointString[i] = sb3.toString(); //TODO ポイント名
                                }
                                */
                                pointStr = "";
                                for (int i = 0; i <= count; i++) {
                                    if (i == 0) {
                                        pointStr = pointStr + pointTitle[i].getText().toString();
                                    } else {
                                        pointStr = pointStr + "," + pointTitle[i].getText().toString();
                                    }
                                }

                                System.out.println("pointStr = " + pointStr);

                                RequestQueue myQueue = Volley.newRequestQueue(getBaseContext());
                                String url = "http://yokohamarally.prodrb.com/api/create_root.php";

                                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String s) {
                                                System.out.println(s);
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                System.out.println(error);
                                            }
                                        }){
                                    @Override
                                    protected Map<String,String> getParams() {
                                        Map<String, String> params = new HashMap<String, String>();
                                        sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

                                        params.put("title", rootName); // ルートタイトル
                                        params.put("userId", sp.getString("id", "")); // ユーザID
                                        params.put("summary", rootSummary); // ルート概要
                                        params.put("latitude",sp.getString("pointLatitude", "")); // ルート概要
                                        params.put("longitude", sp.getString("pointLongitude", "")); // ルート概要
                                        params.put("image_url", image);
                                        params.put("points", pointStr);

                                        return params;
                                    }
                                };

                                myQueue.add(postRequest);
                                myQueue.start();


                            }
                        })
                .setNegativeButton(
                        "いいえ",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                .show();

    }

}
