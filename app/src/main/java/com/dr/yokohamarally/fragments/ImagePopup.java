package com.dr.yokohamarally.fragments;


import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dr.yokohamarally.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class ImagePopup extends DialogFragment {

    String mUrl;
    int mid;
    private RequestQueue myQueue;
    private String mTitle;
    private String mSummary;

//    public  ImagePopup(){
//
//    }
    public static ImagePopup newInstance(String url,int id ,String title ,String summary){

        ImagePopup instance = new ImagePopup();
        Bundle bundle = new Bundle();
        bundle.putString("mUrl",url);
        bundle.putInt("mid",id);
        bundle.putString("mTitle",title);
        bundle.putString("mSummary",summary);
        instance.setArguments(bundle);

        return instance;


    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mUrl = getArguments().getString("mUrl");
        mid = getArguments().getInt("mid");
        mTitle = getArguments().getString("mTitle");
        mSummary = getArguments().getString("mSummary");


        final Dialog dialog = new Dialog(getActivity());

        String url = "http://yokohamarally.prodrb.com/api/get_root_by_id.php?id=";
        String params = String.valueOf(mid);
        StringBuffer buf = new StringBuffer();
        buf.append(url);
        buf.append(params);
        String uri = buf.toString();
        System.out.println(uri);
        myQueue = Volley.newRequestQueue(getActivity().getApplicationContext());






        // タイトル非表示
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // フルスクリーン
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.dialog_custom);

        //タイトルと概要入力
        TextView title =(TextView)dialog.findViewById(R.id.dialog_title);
        title.setText(mTitle);

        TextView summary =(TextView)dialog.findViewById(R.id.summary);
        summary.setText(mSummary);
        // 背景を透明にする
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // OK ボタンのリスナ
        dialog.findViewById(R.id.positive_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        // Close ボタンのリスナ
        dialog.findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { dismiss();
            }
        });

        myQueue.add(new JsonObjectRequest(Request.Method.GET, uri, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        final ImageView imageView = (ImageView)dialog.findViewById(R.id.detail_img);
                        Log.d("check" , mUrl);
                        ImageRequest request = new ImageRequest(
                                mUrl,
                                new Response.Listener<Bitmap>() {
                                    @Override
                                    public void onResponse(Bitmap response) {
                                        imageView.setImageBitmap(response);

                                    }
                                },
                                // 最大の幅、指定無しは0
                                0,
                                0,
                                Bitmap.Config.ARGB_8888,
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        System.out.println(error);

                                    }
                                }
                        );

                        myQueue.add(request);
                        myQueue.start();

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        //エラー時の処理
                        System.out.println(error);

                    }

                }));


        return dialog;
    }

}