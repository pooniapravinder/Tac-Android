package com.wookes.tac.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.wookes.tac.R;
import com.wookes.tac.request.AsyncRequest;
import com.wookes.tac.request.RequestData;
import com.wookes.tac.request.ResponseResult;
import com.wookes.tac.urlconfig.URLConfig;
import com.wookes.tac.util.StatusBar;
import com.wookes.tac.util.ToastDisplay;
import com.wookes.tac.util.network.NetworkConnectivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SignupUsername extends AppCompatActivity implements ResponseResult {
    private EditText etUsername;
    private Context context;
    private TextView tvAuthFailedText;
    private Timer timer = new Timer();
    private boolean isTyping = false;
    private boolean isNextClick = false;
    private ProgressBar usernameLoader;

    @Override
    public void onTaskDone(String output) {
        usernameLoader.setVisibility(View.GONE);
        try {
            JSONObject root = new JSONObject(output);
            if (!root.has("success")) {
                tvAuthFailedText.setText(root.getString("error"));
                tvAuthFailedText.setVisibility(View.VISIBLE);
                return;
            }
            tvAuthFailedText.setVisibility(View.GONE);
            if (isNextClick) {
                Intent intent = new Intent(SignupUsername.this, SignupNamePasswordGender.class);
                intent.putExtra("username", etUsername.getText().toString());
                startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle var1) {
        super.onCreate(var1);
        StatusBar.setStatusBarNavigationBarWhite(getWindow());
        setContentView(R.layout.activity_layout_signup_username);
        etUsername = findViewById(R.id.et_username);
        usernameLoader = findViewById(R.id.username_loader);
        tvAuthFailedText = findViewById(R.id.tv_auth_failed_text);
        ((TextView) findViewById(R.id.copyright)).setText(getString(R.string.copyright, Calendar.getInstance().get(Calendar.YEAR)));
        context = this;
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                isNextClick = false;
                tvAuthFailedText.setVisibility(View.GONE);
                if (!isTyping) {
                    isTyping = true;
                }
                timer.cancel();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        isTyping = false;
                        makeRequest(false);
                    }
                }, 500);
            }
        });
        findViewById(R.id.btn_next).setOnClickListener(view -> {
            isNextClick = true;
            makeRequest(true);
        });
    }

    void makeRequest(boolean isNextButton) {
        String username = etUsername.getText().toString().trim();
        if (!NetworkConnectivity.a(context)) {
            runOnUiThread(() -> ToastDisplay.a(context, R.string.network_error, 0));
            return;
        }
        runOnUiThread(() -> usernameLoader.setVisibility(View.VISIBLE));
        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        new AsyncRequest(SignupUsername.this, SignupUsername.this, isNextButton ? getResources().getString(R.string.loading_dialog) : null, new RequestData(URLConfig.SIGN_UP_USERNAME_URL, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}