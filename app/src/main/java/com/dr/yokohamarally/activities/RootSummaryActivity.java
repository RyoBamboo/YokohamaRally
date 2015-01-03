package com.dr.yokohamarally.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import com.dr.yokohamarally.models.Root;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RootSummaryActivity extends Activity {

    private RequestQueue myQueue;
    private int rootId;

    /*-------------------------
     * 概要ページ用変数
     *-----------------------*/
    private String rootTitle;
    private int    rootRate;
    private String rootSummary;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_summary_activiey);

        ActionBar actionBar = getActionBar();
        System.out.println(actionBar);


        Intent intent = getIntent();
        rootId = intent.getIntExtra("rootId", 0);



        /*-------------------------
         * 概要取得処理
         ------------------------*/
        myQueue = Volley.newRequestQueue(this);

        String url = "http://yokohamarally.prodrb.com/api/get_root_by_id.php?id=";
        String params = String.valueOf(rootId);
        StringBuffer buf = new StringBuffer();
        buf.append(url);
        buf.append(params);
        String uri = buf.toString();
        System.out.println(uri);

        myQueue.add(new JsonObjectRequest(Request.Method.GET, uri, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try {
                            JSONArray json_roots = response.getJSONArray("roots");

                            // JSONObjectとして１取り出す
                            for (int i = 0; i < json_roots.length(); i++) {
                                JSONObject json_root = json_roots.getJSONObject(i);

                                rootTitle = json_root.getString("title");
                                rootSummary = json_root.getString("summary");
                                rootRate = json_root.getInt("rate");
                                System.out.println(rootRate);
                                imageUrl = "http://yokohamarally.prodrb.com/img/" + json_root.getString("image_url");
                            }

                            JSONArray json_points = response.getJSONArray("points");
                            for (int i = 0; i < json_points.length(); i++) {
                                JSONObject json_point = json_points.getJSONObject(i);
                                System.out.println(json_point);
                            }

                            // adapterに反映、追加
                        } catch (Exception e) {
                            System.out.println(e);
                        }

                        /*----------------
                          ビューの書き換え
                         ----------------*/
                        TextView titleView = (TextView)findViewById(R.id.root_title);
                        TextView summaryView = (TextView)findViewById(R.id.root_summary);
                        summaryView.setText(rootSummary);
                        titleView.setText(rootTitle);

                        // TODO: さすがにごり押しすぎ・・・リファクタリング必須
                        // 評価の表示
                        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.root_rate);
                        for (int i = 1; i <= 5; i++) {
                            ImageView rateImageView = new ImageView(getBaseContext());
                            rateImageView.setAdjustViewBounds(true);
                            rateImageView.setMaxWidth(40);
                            if (i <= rootRate) {
                                rateImageView.setImageResource(R.drawable.star);
                            } else {
                                rateImageView.setImageResource(R.drawable.non_star);
                            }
                            linearLayout.addView(rateImageView);
                        }


                        requestImage();
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
    }



    private void requestImage() {
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

                    }
                }
        );

        myQueue.add(request);
        myQueue.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_root_summary_activiey, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
