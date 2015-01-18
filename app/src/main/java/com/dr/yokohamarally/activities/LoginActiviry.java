package com.dr.yokohamarally.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.view.View;

import com.android.volley.RequestQueue;
import com.dr.yokohamarally.R;

public class LoginActiviry extends ActionBarActivity implements OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.email_login_button).setOnClickListener(this);
    }


    public static boolean isLogin() {
        return false;
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.email_login_button:
                Intent intent = new Intent(LoginActiviry.this, LoginByEmail.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
