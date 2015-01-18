package com.dr.yokohamarally.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dr.yokohamarally.R;

import java.util.HashMap;
import java.util.Map;

import butterknife.OnClick;

public class LoginByEmail extends ActionBarActivity {

    private RequestQueue myQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_by_email);
    }

    // ログイン処理
    @OnClick(R.id.submit)
    void submitLogin() {

        // queue
        myQueue = Volley.newRequestQueue(this);

        // url
        String url = "http://yokohamarally.prodrb.com/api/login_by_email.php";

        // Request
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
                        params.put("email", "bamboo@bamboo.com");
                    return params;
                }
        };

    }
}
