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
import com.wookes.tac.ui.forgot.password.UpdatePasswordFlow;
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

public class VerifyOtp extends AppCompatActivity implements ResponseResult {
    private EditText etOtp;
    private Context n;
    private TextView tvAuthFailedText;
    private boolean resendBtn;
    private boolean flowForgotPassword = false;

    @Override
    public void onTaskDone(String output) {
        if (resendBtn) {
            resendBtn = false;
            try {
                JSONObject root = new JSONObject(output);
                ToastDisplay.a(this.n, !root.has("success") ? root.getString("error") : n.getResources().getString(R.string.otp_resend_success), 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            JSONObject root = new JSONObject(output);
            if (!root.has("success")) {
                tvAuthFailedText.setText(root.getString("error"));
                tvAuthFailedText.setVisibility(View.VISIBLE);
                return;
            }
            tvAuthFailedText.setVisibility(View.GONE);
            if (flowForgotPassword) {
                Intent intent = new Intent(n, UpdatePasswordFlow.class);
                intent.putExtra("token", root.getString("token"));
                intent.putExtra("user", root.getLong("user"));
                startActivity(intent);
                finish();
                return;
            }
            new c(n).logInUser();
            Intent intent = new Intent(n, AddProfilePhoto.class);
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
        setContentView(R.layout.activity_layout_verify_otp);
        Button btnOtp = findViewById(R.id.btn_verify_otp);
        flowForgotPassword = getIntent().getBooleanExtra("flow_forgot_password", false);
        etOtp = findViewById(R.id.et_otp);
        Button resendOTP = findViewById(R.id.btn_resend_otp);
        tvAuthFailedText = findViewById(R.id.tv_auth_failed_text);
        ((TextView) findViewById(R.id.copyright)).setText(getString(R.string.copyright, Calendar.getInstance().get(Calendar.YEAR)));
        ((TextView) findViewById(R.id.otp_mobile)).setText(getString(R.string.otp_mobile, getIntent().getStringExtra("hidden_phone")));
        this.n = this;
        btnOtp.setOnClickListener(view -> {
            String otp = etOtp.getText().toString().trim();
            if (!NetworkConnectivity.a(n)) {
                ToastDisplay.a(n, R.string.network_error, 0);
                return;
            }
            Map<String, String> map = new HashMap<>();
            map.put("otp", otp);
            map.put("phone", getIntent().getStringExtra("phone"));
            if (flowForgotPassword) {
                map.put("flow", "forgot_password");
            } else {
                map.put("username", getIntent().getStringExtra("username"));
                map.put("fullname", getIntent().getStringExtra("fullname"));
                map.put("password", getIntent().getStringExtra("password"));
                map.put("gender", getIntent().getStringExtra("gender"));
            }
            new AsyncRequest(VerifyOtp.this, VerifyOtp.this, getResources().getString(R.string.verifying_dialog), new RequestData(URLConfig.VERIFY_OTP_URL, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        });
        resendOTP.setOnClickListener(view -> {
            resendBtn = true;
            Map<String, String> map = new HashMap<>();
            map.put("phone", getIntent().getStringExtra("phone"));
            if (flowForgotPassword) {
                map.put("flow", "forgot_password");
            }
            new AsyncRequest(VerifyOtp.this, VerifyOtp.this, getResources().getString(R.string.loading_dialog), new RequestData(URLConfig.VERIFY_SEND_OTP_URL, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        });
    }
}