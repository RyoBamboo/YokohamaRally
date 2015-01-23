package com.dr.yokohamarally.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
        findViewById(R.id.email_register_button).setOnClickListener(this);
    }


    public static boolean isLogin(SharedPreferences sp) {

        Boolean isLogin = sp.getBoolean("isLogin", false);
        if (isLogin == true) {
            return true;
        }

        return false;
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.email_login_button:
                Intent loginIntent = new Intent(LoginActiviry.this, LoginByEmail.class);
                startActivity(loginIntent);
                break;
            case R.id.email_register_button:
                Intent registerIntent = new Intent(LoginActiviry.this, RegisterByEmail.class);
                startActivity(registerIntent);
                break;
            default:
                break;
        }
    }
}

