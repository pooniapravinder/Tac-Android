package com.wookes.tac.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.wookes.tac.R;
import com.wookes.tac.request.AsyncRequest;
import com.wookes.tac.request.RequestData;
import com.wookes.tac.request.ResponseResult;
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

public class SignupPhone extends AppCompatActivity implements ResponseResult {
    Button btnVerify;
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
            Intent intent = new Intent(SignupPhone.this, VerifyOtp.class);
            intent.putExtra("hidden_phone", root.getString("phone"));
            intent.putExtra("phone", etPhoneNumber.getText().toString());
            intent.putExtra("username", getIntent().getStringExtra("username"));
            intent.putExtra("fullname", getIntent().getStringExtra("fullname"));
            intent.putExtra("password", getIntent().getStringExtra("password"));
            intent.putExtra("gender", getIntent().getStringExtra("gender"));
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle var1) {
        super.onCreate(var1);
        StatusBar.setStatusBarNavigationBarWhite(getWindow());
        setContentView(R.layout.activity_layout_signup_phone);
        btnVerify = findViewById(R.id.btn_verify);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        tvAuthFailedText = findViewById(R.id.tv_auth_failed_text);
        TextView tos = findViewById(R.id.terms_conditions);
        Spannable s = (Spannable) Html.fromHtml(getResources().getString(R.string.terms_conditions));
        for (URLSpan u : s.getSpans(0, s.length(), URLSpan.class)) {
            s.setSpan(new UnderlineSpan() {
                public void updateDrawState(@NonNull TextPaint tp) {
                    tp.setUnderlineText(false);
                }
            }, s.getSpanStart(u), s.getSpanEnd(u), 0);
        }
        tos.setText(s);
        tos.setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView) findViewById(R.id.copyright)).setText(getString(R.string.copyright, Calendar.getInstance().get(Calendar.YEAR)));
        this.n = this;
        this.k();
        btnVerify.setOnClickListener(view -> {
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            if (!NetworkConnectivity.a(n)) {
                ToastDisplay.a(n, R.string.network_error, 0);
                return;
            }
            if (p.c(phoneNumber, tvAuthFailedText)) {
                Map<String, String> map = new HashMap<>();
                map.put("phone", phoneNumber);
                new AsyncRequest(SignupPhone.this, SignupPhone.this, getResources().getString(R.string.loading_dialog), new RequestData(URLConfig.VERIFY_SEND_OTP_URL, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
    }
}