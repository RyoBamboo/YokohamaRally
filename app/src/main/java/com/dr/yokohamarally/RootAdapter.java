package com.dr.yokohamarally;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class RootAdapter extends ArrayAdapter<Root> {

    // ビューを動的に書き換えるインフレイター
    private LayoutInflater layoutInflater;

    public RootAdapter(Context context, int resource, ArrayList<Root> roots)  {

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
            convertView = layoutInflater.inflate(R.layout.root_list_item, null);
        }

        // 対応する行のオブジェクトを取得
        Root root = (Root)getItem(position);

        // タイトルをセット
        TextView title = (TextView)convertView.findViewById(R.id.title);
        title.setText(root.getTitle());

        return convertView;
    }
}
