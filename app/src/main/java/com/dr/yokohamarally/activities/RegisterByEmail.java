package com.dr.yokohamarally.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dr.yokohamarally.R;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterByEmail extends ActionBarActivity {

    private RequestQueue myQueue;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_by_email);
        ButterKnife.inject(this);
    }


    @OnClick(R.id.submit)
    void submitRegister() {
        System.out.println("onClick");

        // queue
        myQueue = Volley.newRequestQueue(this);

        // url
        String url = "http://yokohamarally.prodrb.com/api/register_by_email.php";

        // Request
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (s.equals("success")) {

                            // ログイン成功したらユーザ情報をsharedPrefereceに保存
                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                            sp.edit().putString("username", name).commit();
                            sp.edit().putBoolean("isLogin", true).commit();


                            Intent intent = new Intent(RegisterByEmail.this, MainActivity.class);
                            startActivity(intent);
                        }
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
                // POSTするパラメーター
                EditText emailEditText = (EditText)findViewById(R.id.email);
                String email = emailEditText.getText().toString();
                EditText passEditText = (EditText)findViewById(R.id.password);
                String pass = passEditText.getText().toString();
                EditText nameEditText = (EditText)findViewById(R.id.name);
                name = nameEditText.getText().toString();

                params.put("email", email);
                params.put("pass", pass);
                params.put("name", name);

                return params;
            }
        };

        myQueue.add(postRequest);
        myQueue.start();
    }
}
