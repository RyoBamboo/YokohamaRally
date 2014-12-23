/**
 * RootTabActivity.java
 *
 * ルートのリスト一覧を表示するアクティビティ
 */

package com.dr.yokohamarally.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dr.yokohamarally.MyData;
import com.dr.yokohamarally.R;
import com.dr.yokohamarally.Root;
import com.dr.yokohamarally.RootAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RootTabActivity extends ActionBarActivity {

    private RequestQueue myQueue;

    private ArrayList<Root> roots;
    private RootAdapter adapter;
    private ListView rootListView;

    private MyData myData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_tab);

        /*------------------
         タブ表示処理
        ------------------*/
        FragmentTabHost tabHost =




        /*------------------
         リストビュー表示処理
        -----------------*/
        // queue
        myQueue = Volley.newRequestQueue(this);

        // arrayList
        roots = new ArrayList<Root>();

        // adapter
        adapter = new RootAdapter(this, 0, roots);

        // ListView
        rootListView = (ListView)findViewById(R.id.root_list);
        rootListView.setAdapter(adapter);

        myData = new MyData(this,myQueue,adapter);
        myData.getData();
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_root_tab, menu);
        return true;
    }
    */

    /*
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
    */
}
