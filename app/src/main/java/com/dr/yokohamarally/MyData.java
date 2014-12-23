package com.dr.yokohamarally;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyData {

    private RequestQueue myQueue;
    private RootAdapter adapter;

    private ArrayList<Root> roots;

    // 引数に渡されたqueueとadapterを受け取る
    public MyData(Context context, RequestQueue myQueue, RootAdapter adapter) {

        this.myQueue = myQueue;
        this.adapter = adapter;
    }

    // データの取得と更新
    public void getData() {

        roots = new ArrayList<Root>();

        String url = "http://yokohamarally.prodrb.com/api/get_all_root.php";

        myQueue.add(new JsonObjectRequest(Request.Method.GET, url, null,
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

                                String title = json_root.getString("title");

                                System.out.println(title);

                                Root root = new Root();
                                root.setTitle(title);

                                roots.add(root);
                            }

                            // adapterに反映、追加
                            adapter.addAll(roots);
                        } catch (Exception e) {
                            System.out.println(e);
                        }
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

}
