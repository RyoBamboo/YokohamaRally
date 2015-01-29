package com.dr.yokohamarally.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dr.yokohamarally.R;
import com.dr.yokohamarally.adapters.TryAdapter;
import com.dr.yokohamarally.fragments.BitmapHolder;
import com.dr.yokohamarally.fragments.ImagePopup;
import com.dr.yokohamarally.fragments.TryInformation;
import com.dr.yokohamarally.models.Root;

import android.content.DialogInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TryActivity extends Activity {

    private RequestQueue mQueue;

    /*-------------------------
    * 変数
    *-----------------------*/
    private int    rootId;
    private String[]  checkedPoints = new String[10];
    private String[]  checkedPointImages = new String[10];
    private int    rootRate;
    private String rootTitle;
    private String rootSummary;
    private String imageUrl;
    private double[] pointLatitude;
    private String[] pointImageTitle;
    private double[] pointLongitude;
    private String[] pointImageUrls;

    private TryAdapter mRootAdapter;
    private ArrayList<Root> roots;
    private ListView mAllRootListView;
    private Bitmap bmp;
    private int totalPoints;
    private int totalChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try);


        roots = new ArrayList<Root>();
        mRootAdapter = new TryAdapter(TryActivity.this, 0, roots);
        mAllRootListView = (ListView)findViewById(R.id.try_list);
        mAllRootListView.setAdapter(mRootAdapter);

        mQueue = Volley.newRequestQueue(this);

        // ダイアログを表示する
        DialogFragment newFragment = new CommentDialogFragment(mQueue);
        newFragment.show(getFragmentManager(), "test");

        /*--------------------------------------------------
         * 挑戦中のルートIDと達成したチェックポイントのIDと画像を取得
         *------------------------------------------------*/
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        rootId = sp.getInt("rootId", 0);
        String[] checkedPointsCopy = getArrayFromSharedPreference("checkedPoints");
        String[] checkedPointImagesCopy = getArrayFromSharedPreference("checkedPointImages");

        //クリアした数を格納する変数の初期化
        totalChecked = 0;
        //配列拡張のための処理
        checkedPointImages = new String[10];
        checkedPoints = new String[10];
        if(checkedPointImagesCopy != null){
            for( int i=0; i<checkedPointImagesCopy.length; i++)checkedPointImages[i]=checkedPointImagesCopy[i];
        }
        if(checkedPointsCopy != null) {
            for (int i = 0; i < checkedPointsCopy.length; i++)
                checkedPoints[i] = checkedPointsCopy[i];
        }



        /*--------------------------------
         * 挑戦中のルート情報をIDから取得
         *------------------------------*/
        String url = "http://yokohamarally.prodrb.com/api/get_root_by_id.php?id=";
        String params = String.valueOf(sp.getInt("rootId", 0));
        StringBuffer buf = new StringBuffer();
        buf.append(url);
        buf.append(params);
        String uri = buf.toString();


        mQueue.add(new JsonObjectRequest(Request.Method.GET, uri, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray json_roots = response.getJSONArray("roots");

                            for(int i = 0; i < json_roots.length(); i++) {
                                JSONObject json_root = json_roots.getJSONObject(i);
                                System.out.println(json_root);

                                rootTitle = json_root.getString("title");
                                rootSummary = json_root.getString("summary");
                                rootRate = json_root.getInt("rate");
                                System.out.println(rootRate);
                                imageUrl = "http://yokohamarally.prodrb.com/img/" + json_root.getString("image_url");
                            }

                            JSONArray json_points = response.getJSONArray("points");
                            pointImageUrls = new String[json_points.length()]; // ポイントの画像URLを保存する配列
                            pointImageTitle = new String[json_points.length()]; // ポイントの画像タイトルを保存する配列
                            totalPoints = json_points.length();

                            /*--------------------------------------------
                             * チェックポイントの数を保存
                             *------------------------------------------*/
                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                            sp.edit().putInt("checkpointNum", json_points.length()).commit();


                            for (int i = 0; i < json_points.length(); i++) {
                                JSONObject json_point = json_points.getJSONObject(i);
                                Log.d("MYTAG",json_point.getString("image_url") + "");
                                pointImageUrls[i] = json_point.getString("image_url");
                                pointImageTitle[i] = json_point.getString("name");
                                TryInformation.latitude[i] = json_point.getDouble("latitude");
                                TryInformation.longitude[i] = json_point.getDouble("longitude");

                            }

                        } catch (Exception e) {

                        }



                        // トップ画像の取得
                        requestTopImage();

                        // チェックポイントの表示
                        for (int i = 0; i < pointImageUrls.length; i++) {
                            requestCheckPoint(i);
                        }

                        TextView text = (TextView)findViewById(R.id.totalChecked);
                        text.setText("現在の達成数: " +  totalChecked +"/" + totalPoints );



                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ));

    }

    private void requestTopImage() {
        // ImageViewの取得
        final ImageView imageView = (ImageView)findViewById(R.id.root_image);

        ImageRequest request = new ImageRequest(
                imageUrl,
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

        mQueue.add(request);
        mQueue.start();
    }


    // TODO: かなり無理やりリファクタリング必須
    protected void requestCheckPoint(int i) {
        final LinearLayout parentLinearLayout = (LinearLayout)findViewById(R.id.checkpoints);
        final String url = "http://yokohamarally.prodrb.com/img/" + pointImageUrls[i];
        final ImageView mImageView = new ImageView(this);
        final int number = i;
        final ArrayList<Root> _roots = new ArrayList<Root>();
        final Root root = new Root();

        if("true".equals(checkedPoints[i])){
            root.setCheckedPoint(true);
            totalChecked++;
        }
        root.setId(i);
        root.setTitle(pointImageTitle[i]);
        root.setImageUrl(url);
        _roots.add(root);
        mRootAdapter.addAll(_roots);




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


   public static class CommentDialogFragment extends DialogFragment implements TextWatcher{

       private String comment;
       private static RequestQueue queue;

       public CommentDialogFragment(RequestQueue queue) {
           this.queue = queue;
       }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View content = inflater.inflate(R.layout.comment_dialog, null);

            EditText editText = (EditText)content.findViewById(R.id.comment);
            editText.addTextChangedListener(this);

            builder.setView(content);

            builder.setMessage("コメントしてね")
                    .setPositiveButton("決定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {


                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

                            int root_id = sp.getInt("rootId", 0);

                            RatingBar ratingBar = (RatingBar) content.findViewById(R.id.rate);
                            float rate = ratingBar.getRating();

                            //EditText editText = (EditText)content.findViewById(R.id.comment);

                            final String rootStr = String.valueOf(root_id);
                            final String rateStr = String.valueOf(rate);
                            final String userStr = sp.getString("id", "");
                            //String comment = editText.getText().toString();
                            System.out.println("comment=" + comment);

                            // コメント投稿処理
                            String url = "http://yokohamarally.prodrb.com/api/create_comment.php?";

                            String params = "user_id=" + userStr + "&root_id=" + rootStr + "&rate=" + rateStr + "&comment=" + comment;
                            StringBuffer buf = new StringBuffer();
                            buf.append(url);
                            buf.append(params);
                            String uri = buf.toString();
                            System.out.println(uri);

                            /*
                            queue.add(new JsonObjectRequest(Request.Method.GET, uri, null,
                                    new Response.Listener<JSONObject>()
                                    {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            System.out.println("res=" + response);
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            System.out.println(error);
                                        }
                                    }
                            ));
                            */


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
                                    }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<String, String>();

                                    params.put("user_id", userStr);
                                    params.put("root_id", rootStr);
                                    params.put("rate", rateStr);
                                    params.put("comment", comment);

                                    return params;
                                }
                            };

                            queue.add(postRequest);
                            queue.start();
                        }
                    })
                    .setNegativeButton("閉じる", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }

       @Override
       public void beforeTextChanged(CharSequence s, int start, int count,int after) {
           //操作前のEtidTextの状態を取得する
       }

       @Override
       public void onTextChanged(CharSequence s, int start, int before, int count) {
           //操作中のEtidTextの状態を取得する
       }

       @Override
       public void afterTextChanged(Editable s) {
           //操作後のEtidTextの状態を取得する
           System.out.println(s);
           comment = s.toString();
           /*---　取得例　---*/

       }

   }

}
