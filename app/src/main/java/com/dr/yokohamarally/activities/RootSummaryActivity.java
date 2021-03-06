package com.dr.yokohamarally.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dr.yokohamarally.R;
import com.dr.yokohamarally.adapters.CommentsList;
import com.dr.yokohamarally.adapters.RootAdapter;
import com.dr.yokohamarally.adapters.TryAdapter;
import com.dr.yokohamarally.fragments.CommonDialogFragment;
import com.dr.yokohamarally.fragments.ImagePopup;
import com.dr.yokohamarally.listener.DialogListener;
import com.dr.yokohamarally.models.Root;

import org.json.JSONArray;
import org.json.JSONObject;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class RootSummaryActivity extends FragmentActivity implements DialogListener {

    @InjectView(R.id.try_button)
    Button _tryButton;

    private RequestQueue myQueue;
    private int rootId;

    /*-------------------------
     * 概要ページ用変数
     *-----------------------*/
    private String rootTitle;
    private int    rootRate;
    private String rootSummary;
    private String imageUrl;
    private String[] pointImageUrls;
    private String[] pointTitles;
    private String[] pointSummaries;



    private Button mapButton,tryButton;

    private ArrayList<Root> roots;
    private RootAdapter adapter;
    private ListView rootListView;
    private CommentsList mRootAdapter;
    private ListView mAllRootListView;
    private static ProgressDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_summary_activiey);

        showWaiDialog();

        ButterKnife.inject(this);

        ActionBar actionBar = getActionBar();
        System.out.println(actionBar);


        Intent intent = getIntent();
        rootId = intent.getIntExtra("rootId", 0);

        /*---------------------------
         もしクリアているルートであればボタン無効
         ---------------------------*/
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String[] completedroot = sp.getString("_completedRoots", "").split("-");
        for (String root : completedroot) {
            if (String.valueOf(rootId).equals(root)) {
                _tryButton.setClickable(false);
                _tryButton.setText("クリア済み");
            }
        }



        roots = new ArrayList<Root>();
        mRootAdapter = new CommentsList(RootSummaryActivity.this, 0, roots);
        mAllRootListView = (ListView)findViewById(R.id.comments_list);
        mAllRootListView.setAdapter(mRootAdapter);



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
        mapButton = (Button)findViewById(R.id.map_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RootSummaryActivity.this, MapActivity.class);
                intent.putExtra("rootId", rootId);
                startActivity(intent);

            }
        });

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
                                imageUrl = "http://yokohamarally.prodrb.com/img/" + json_root.getString("image_url");
                            }

                            JSONArray json_points = response.getJSONArray("points");
                            pointImageUrls = new String[json_points.length()]; // ポイントの画像URLを保存する配列
                            pointTitles = new String[json_points.length()];
                            pointSummaries = new String[json_points.length()];
                            for (int i = 0; i < json_points.length(); i++) {
                                JSONObject json_point = json_points.getJSONObject(i);
                                pointImageUrls[i] = json_point.getString("image_url");
                                pointTitles[i] = json_point.getString("name");
                                pointSummaries[i] = json_point.getString("summary");
                            }

                            JSONArray json_comments = response.getJSONArray("comments");
                            for (int i = 0; i < json_comments.length(); i++) {
                                JSONObject json_comment = json_comments.getJSONObject(i);
                                String comment = json_comment.getString("comment");
                                String name = json_comment.getString("name");
                                String userImage = json_comment.getString("userImage");
                                String date = json_comment.getString("date");
                                int rate = json_comment.getInt("rate");
                                final Root root = new Root();
                                final ArrayList<Root> _roots = new ArrayList<Root>();
                                root.setName(name);
                                root.setUseImage(userImage);
                                root.setComments(comment);
                                root.setRate(rate);
                                root.setClearDate(date);
                                _roots.add(root);
                                mRootAdapter.addAll(_roots);
                                setListViewHeightBasedOnChildren(mAllRootListView);
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

                        // TODO:さすがにごり押しすぎ・・・リファクタリング必須
                        // チェックポイント画像の表示
                        for (int i = 0; i < pointImageUrls.length; i++) {
                            requestPointImage(i);
                        }

                        // ルート画像の取得
                        requestTopImage();
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



    private void requestTopImage() {
        // ImageViewの取得
        final ImageView imageView = (ImageView)findViewById(R.id.root_image);

        ImageRequest request = new ImageRequest(
                imageUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageView.setImageBitmap(response);
                        System.out.println(imageView.getWidth());
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


    private void requestPointImage(int i) {
        final String url = "http://yokohamarally.prodrb.com/img/" + pointImageUrls[i];
        final ImageView imageView = new ImageView(getBaseContext());
        final LinearLayout linearLayout = (LinearLayout)findViewById(R.id.point_img);
        final String title = pointTitles[i];
        final String summary = pointSummaries[i];

        ImageRequest request = new ImageRequest(
                url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageView.setImageBitmap(response);

                        imageView.setMaxWidth(170);
                        imageView.setMinimumHeight(170);
                        imageView.setAdjustViewBounds(true);
                        imageView.setPadding(0, 20, 10, 0);
                        linearLayout.addView(imageView);
                    }
                },
                0,
                0,
                Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DialogFragment newFragment = ImagePopup.newInstance(url,rootId ,title ,summary);
                newFragment.setCancelable(false);
                newFragment.show(getFragmentManager(), "test1");

            }
        });

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

    public void showWaiDialog(){

        // インスタンス作成
        waitDialog = new ProgressDialog(this);
        // タイトル設定
        waitDialog.setTitle("読み込み中です");
        // メッセージ設定
        waitDialog.setMessage("now loading...");
        // スタイル設定 スピナー
        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // キャンセル可能か(バックキーでキャンセル）
        waitDialog.setCancelable(true);
        // ダイアログ表示
        waitDialog.show();

        // 別スレッドで時間のかかる処理を実行
        new Thread(new Runnable() {
            @Override
            public void run() {
                int cnt = 0;
                // 時間のかかる処理(疑似)
                while (cnt < 2) {
                    cnt++;
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {

                    }
                }
                // 終わったらダイアログ消去
                waitDialog.dismiss();
            }
        }).start();

    }
    /*--------------------------
     * DialogListener
     *------------------------*/
    @Override
    public void onPositiveClick(String tag) {
        // ルートidを挑戦中のルートidとして登録
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sp.edit().putInt("rootId", rootId).commit();

        // 前回のルートのクリア情報を初期化
        sp.edit().remove("checkedPoints").commit();
        sp.edit().remove("isCompleted").commit();


        // 画面の遷移
        Intent intent = new Intent(RootSummaryActivity.this, TryActivity.class);
        startActivity(intent);
    }

    @Override
    public void onNegativeClick(String tag) {

    }

    /*--------------------------
     * onClick
     *------------------------*/
    @OnClick(R.id.try_button)
    void submitTry() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int beforeRootId = sp.getInt("rootId", 0);
        if (beforeRootId != 0 && beforeRootId != rootId) {
            // 挑戦するルートを変更するときはダイアログを出す
            CommonDialogFragment alertDialogFragment = CommonDialogFragment.newInstance("新しいルートに挑戦", "現在の挑戦中のデータが消えますが大丈夫ですか？");
            alertDialogFragment.setDialogListener(this);
            alertDialogFragment.show(getFragmentManager(), "test");

        } else {
            // そうでなければTryアクティビティへ
            sp.edit().putInt("rootId", rootId).commit();
            Intent intent = new Intent(RootSummaryActivity.this, TryActivity.class);
            startActivity(intent);
        }

    }

    // リストビューの高さが一行になってしまうバグ修正の為のコード
    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
