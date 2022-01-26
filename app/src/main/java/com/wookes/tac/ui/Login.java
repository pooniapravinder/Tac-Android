package com.wookes.tac.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.wookes.tac.R;
import com.wookes.tac.request.AsyncRequest;
import com.wookes.tac.request.RequestData;
import com.wookes.tac.request.ResponseResult;
import com.wookes.tac.ui.forgot.password.UsernamePhoneFlow;
import com.wookes.tac.urlconfig.URLConfig;
import com.wookes.tac.util.StatusBar;
import com.wookes.tac.util.ToastDisplay;
import com.wookes.tac.util.c;
import com.wookes.tac.util.network.NetworkConnectivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements ResponseResult {
    Button btnLogin;
    Button btnSignup;
    EditText etUsername;
    EditText etPassword;
    private Context n;
    TextView tvAuthFailedText;

    @Override
    public void onTaskDone(String output) {
        try {
            JSONObject root = new JSONObject(output);
            if (!root.has("success")) {
                tvAuthFailedText.setText(root.getString("error"));
                tvAuthFailedText.setVisibility(View.VISIBLE);
                return;
            }
            tvAuthFailedText.setVisibility(View.GONE);
            new c(n).logInUser();
            Intent intent = new Intent(n, LandingUi.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle var1) {
        super.onCreate(var1);
        StatusBar.setStatusBarNavigationBarWhite(getWindow());
        setContentView(R.layout.activity_layout_login);
        btnLogin = findViewById(R.id.btn_login);
        btnSignup = findViewById(R.id.btn_signup);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        tvAuthFailedText = findViewById(R.id.tv_auth_failed_text);
        ((TextView) findViewById(R.id.copyright)).setText(getString(R.string.copyright, Calendar.getInstance().get(Calendar.YEAR)));
        this.n = this;
        btnLogin.setOnClickListener(view -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            if (!NetworkConnectivity.a(n)) {
                ToastDisplay.a(n, R.string.network_error, 0);
                return;
            }
            Map<String, String> map = new HashMap<>();
            map.put("usernamePhone", username);
            map.put("password", password);
            new AsyncRequest(Login.this, Login.this, getResources().getString(R.string.logging_dialog), new RequestData(URLConfig.LOGIN_URL, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        });
        btnSignup.setOnClickListener(view -> startActivity(new Intent(n, SignupUsername.class)));
        findViewById(R.id.btn_forgot_password).setOnClickListener(view -> startActivity(new Intent(n, UsernamePhoneFlow.class)));
    }
}