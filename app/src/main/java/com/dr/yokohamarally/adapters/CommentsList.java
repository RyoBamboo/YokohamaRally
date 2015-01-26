package com.dr.yokohamarally.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.dr.yokohamarally.R;
import com.dr.yokohamarally.models.Root;

import java.util.ArrayList;

public class CommentsList extends ArrayAdapter<Root> {

    // ビューを動的に書き換えるインフレイター
    private LayoutInflater layoutInflater;

    public CommentsList(Context context, int resource, ArrayList<Root> roots)  {

        // ArrayAdapterのコンストラクタ
        super(context, resource, roots);

        // インフレイターの取得
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    // 各行に表示するViewを返すメソッド
    public View getView(int position, View convertView, ViewGroup parent) {

        // MEMO by take: リストのリロード機能実装しないので必要ない可能性あり
        if (convertView == null) {
            // Viewがまだ作成していなければ、Viewを生成＝＞View
            convertView = layoutInflater.inflate(R.layout.comments_list_item, null);
        }

        // 対応する行のオブジェクトを取得
        Root root = (Root)getItem(position);

        convertView.setBackgroundResource(R.drawable.round_corner_list);

        // タイトルをセット
        TextView title = (TextView)convertView.findViewById(R.id.comment);
        title.setText(root.getComments());

        // タイトルをセット
        TextView name = (TextView)convertView.findViewById(R.id.user);
        name.setText(root.getName());

        // 日付をセット
        TextView date = (TextView)convertView.findViewById(R.id.date);
        date.setText(root.getClearDate());

        // 評価をセット
        LinearLayout mLinerLayout = (LinearLayout)convertView.findViewById(R.id.root_rate);
        int rate = root.getRate();
        for (int i = 1; i <= 5; i++) {
            ImageView rateImageView = new ImageView(this.getContext());
            rateImageView.setAdjustViewBounds(true);
            rateImageView.setMaxWidth(25);
            if (i <= rate) {
                rateImageView.setImageResource(R.drawable.star);
            } else {
                rateImageView.setImageResource(R.drawable.non_star);
            }
            mLinerLayout.addView(rateImageView);
        }




        String imageUrl = "http://yokohamarally.prodrb.com/img/" + root.getImageUrl();

        /*------------
         * 画像取得
         *----------*/
        final ImageView imageView = (ImageView)convertView.findViewById(R.id.root_image);

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

        RequestQueue myQueue;
        myQueue = Volley.newRequestQueue(this.getContext());

        myQueue.add(request);
        myQueue.start();


        return convertView;
    }
}
