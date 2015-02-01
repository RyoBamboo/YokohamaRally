package com.dr.yokohamarally.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dr.yokohamarally.R;
import com.dr.yokohamarally.filters.EmailFilter;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class LoginByEmail extends ActionBarActivity {

    private RequestQueue myQueue;
    private InputFilter[] filter = {new EmailFilter()};

    @InjectView(R.id.email)
    EditText emailEditText;

    @InjectView(R.id.password)
    EditText passEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_by_email);
        ButterKnife.inject(this);

        emailEditText.setFilters(filter);
        passEditText.setFilters(filter);
    }

    // ログイン処理
    @OnClick(R.id.submit)
    void submitLogin() {
        /*----------------------------------
         * バリデーション
         *--------------------------------*/
        String email = emailEditText.getText().toString();
        String pass = emailEditText.getText().toString();

        if (email.length() == 0){Toast.makeText(this, "メールアドレスを入力してください", Toast.LENGTH_LONG).show();return;}
        if (pass.length() == 0){Toast.makeText(this, "パスワードを入力ください", Toast.LENGTH_LONG).show();return;}

        // queue
        myQueue = Volley.newRequestQueue(this);

        // url
        String url = "http://yokohamarally.prodrb.com/api/login_by_email.php";

        // Request
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.equals("false")) {

                            String[] resultArray = response.split(",");

                            // ログイン成功したらユーザ情報をsharedPrefereceに保存
                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                            sp.edit().putBoolean("isLogin", true).commit();
                            sp.edit().putString("_completedRoots", resultArray[5]).commit();
                            sp.edit().putString("notice_flag", resultArray[4]).commit();
                            sp.edit().putString("profile_image", resultArray[3]).commit();
                            sp.edit().putString("email",resultArray[2]).commit();
                            sp.edit().putString("name", resultArray[1]).commit();
                            sp.edit().putString("id", resultArray[0]).commit();
                            System.out.println(resultArray[3]);

                            Intent intent =  new Intent(LoginByEmail.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "メールアドレス、またはパスワードが間違っています", Toast.LENGTH_LONG).show();
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
                    params.put("email", email);
                    params.put("pass", pass);
                    return params;
                }
        };

        myQueue.add(postRequest);
        myQueue.start();

    }
}
