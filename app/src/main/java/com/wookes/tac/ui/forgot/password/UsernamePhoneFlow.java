package com.wookes.tac.ui.forgot.password;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.wookes.tac.R;
import com.wookes.tac.request.AsyncRequest;
import com.wookes.tac.request.RequestData;
import com.wookes.tac.request.ResponseResult;
import com.wookes.tac.ui.VerifyOtp;
import com.wookes.tac.urlconfig.URLConfig;
import com.wookes.tac.util.StatusBar;
import com.wookes.tac.util.ToastDisplay;
import com.wookes.tac.util.d;
import com.wookes.tac.util.network.NetworkConnectivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UsernamePhoneFlow extends AppCompatActivity implements ResponseResult {
    ImageView next;
    EditText etPhoneNumber;
    private Context n;
    private d p;
    TextView tvAuthFailedText;

    private void k() {
        this.p = new d(this.n);
    }

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
            Intent intent = new Intent(UsernamePhoneFlow.this, VerifyOtp.class);
            intent.putExtra("hidden_phone", root.getString("phone"));
            intent.putExtra("phone", etPhoneNumber.getText().toString());
            intent.putExtra("flow_forgot_password", true);
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle var1) {
        super.onCreate(var1);
        StatusBar.setStatusBarNavigationBarWhite(getWindow());
        setContentView(R.layout.activity_layout_username_phone_flow);
        next = findViewById(R.id.next);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        tvAuthFailedText = findViewById(R.id.tv_auth_failed_text);
        ((TextView) findViewById(R.id.copyright)).setText(getString(R.string.copyright, Calendar.getInstance().get(Calendar.YEAR)));
        this.n = this;
        this.k();
        next.setOnClickListener(view -> {
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            if (!NetworkConnectivity.a(n)) {
                ToastDisplay.a(n, R.string.network_error, 0);
                return;
            }
            if (p.c(phoneNumber, tvAuthFailedText)) {
                Map<String, String> map = new HashMap<>();
                map.put("phone", phoneNumber);
                map.put("flow", "forgot_password");
                new AsyncRequest(UsernamePhoneFlow.this, UsernamePhoneFlow.this, getResources().getString(R.string.loading_dialog), new RequestData(URLConfig.VERIFY_SEND_OTP_URL, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
    }
}