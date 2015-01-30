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

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class RegisterByEmail extends ActionBarActivity {

    private RequestQueue myQueue;
    private String name;

    private InputFilter[] emailFilter = { new EmailFilter() };

    @InjectView(R.id.name)
    EditText nameEditText;

    @InjectView(R.id.email)
    EditText emailEditText;

    @InjectView(R.id.password)
    EditText passEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_by_email);
        ButterKnife.inject(this);

        // フィルターのセット
        emailEditText.setFilters(emailFilter);
        passEditText.setFilters(emailFilter);

    }


    @OnClick(R.id.submit)
    void submitRegister() {

        /*------------------------------
        * バリデーション
        *-----------------------------*/
        String _email = emailEditText.getText().toString();
        String _pass = passEditText.getText().toString();
        String _name = nameEditText.getText().toString();

        if (_name.length()==0) {Toast.makeText(this, "名前を入力してください", Toast.LENGTH_LONG).show(); return;}
        if (_email.length() == 0) {Toast.makeText(this, "メールアドレスを入力してください", Toast.LENGTH_LONG).show();return;}
        if (_pass.length()==0) {Toast.makeText(this, "パスワードを入力してください", Toast.LENGTH_LONG).show(); return;}

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
                        } else if(s.equals("isUser")) {
                            Toast.makeText(getBaseContext(), "既に登録されているメールアドレスです", Toast.LENGTH_LONG).show();
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
