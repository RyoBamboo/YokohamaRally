package com.dr.yokohamarally.fragments;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dr.yokohamarally.R;
import com.dr.yokohamarally.activities.RootSummaryActivity;
import com.dr.yokohamarally.adapters.RootAdapter;
import com.dr.yokohamarally.core.RequestManager;
import com.dr.yokohamarally.core.VolleyApi;
import com.dr.yokohamarally.models.Root;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewRootFragment extends Fragment{

    private RootAdapter mRootAdapter;
    private ArrayList<Root> roots;
    private ListView mAllRootListView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_root_list, container, false);

        roots = new ArrayList<Root>();
        mRootAdapter = new RootAdapter(container.getContext(), 0, roots);
        mAllRootListView = (ListView)view.findViewById(R.id.root_list);
        mAllRootListView.setAdapter(mRootAdapter);
        setupAllRootListView();

        return view;
    }

    private void setupAllRootListView() {
        // ここでタップされた時のリスナー登録
        mAllRootListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int _id = (int)view.getTag(R.string.listItemTag);
                System.out.println("id=" + _id);
                Intent intent = new Intent(getActivity(), RootSummaryActivity.class);
                intent.putExtra("rootId", _id);
                startActivity(intent);
            }
        });

        // http通信
        RequestManager.addRequest(new JsonObjectRequest(Method.GET, "http://yokohamarally.prodrb.com/api/get_new_root.php", null, responseListener(), errorListener()), this);
    }

    private Response.Listener<JSONObject> responseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                ArrayList<Root> _roots = new ArrayList<Root>();

                try {
                    JSONArray json_roots = response.getJSONArray("roots");

                    // JSONObjectとして１取り出す
                    for (int i = 0; i < json_roots.length(); i++) {
                        JSONObject json_root = json_roots.getJSONObject(i);

                        String title = json_root.getString("title");
                        int id = Integer.parseInt(json_root.getString("id"));
                        int rate = json_root.getInt("rate");
                        int completedCount = json_root.getInt("completed_count");
                        int number = i;
                        String name = json_root.getString("user_name");
                        String number_title = String.valueOf(number + 1) + ". " + title;
                        String imageUrl = json_root.getString("image_url");
                        Root root = new Root();
                        root.setId(id);
                        root.setName(name);
                        root.setCompletedCount(completedCount);
                        root.setTitle(number_title);
                        root.setImageUrl(imageUrl);
                        root.setRate(rate);

                        _roots.add(root);
                    }

                    // adapterに反映、追加
                    mRootAdapter.addAll(_roots);
                    setListViewHeightBasedOnChildren(mAllRootListView);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        };
    }

    private Response.ErrorListener errorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        };
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