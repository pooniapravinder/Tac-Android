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
import com.wookes.tac.ui.LandingUi;
import com.wookes.tac.ui.Login;
import com.wookes.tac.urlconfig.URLConfig;
import com.wookes.tac.util.SelfUser;
import com.wookes.tac.util.StatusBar;
import com.wookes.tac.util.ToastDisplay;
import com.wookes.tac.util.d;
import com.wookes.tac.util.network.NetworkConnectivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UpdatePasswordFlow extends AppCompatActivity implements ResponseResult {
    private EditText etPassword, etConfPassword;
    private Context n;
    private d p;
    private TextView tvAuthFailedText;
    private SelfUser selfUser;

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
            if (selfUser.getSelfUser() == null) {
                ToastDisplay.a(n, R.string.password_update_success, 0);
                Intent intent = new Intent(n, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                ToastDisplay.a(n, R.string.password_update_success, 0);
                Intent intent = new Intent(n, LandingUi.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle var1) {
        super.onCreate(var1);
        StatusBar.setStatusBarNavigationBarWhite(getWindow());
        setContentView(R.layout.activity_layout_update_password_flow);
        this.n = this;
        selfUser = new SelfUser(n);
        ImageView done = findViewById(R.id.done);
        String token = getIntent().getStringExtra("token");
        long user = getIntent().getLongExtra("user", 0);
        etPassword = findViewById(R.id.et_password);
        etConfPassword = findViewById(R.id.et_conf_password);
        tvAuthFailedText = findViewById(R.id.tv_auth_failed_text);
        ((TextView) findViewById(R.id.copyright)).setText(getString(R.string.copyright, Calendar.getInstance().get(Calendar.YEAR)));
        this.k();
        done.setOnClickListener(view -> {
            String password = etPassword.getText().toString().trim();
            String confPassword = etConfPassword.getText().toString().trim();
            if (!NetworkConnectivity.a(n)) {
                ToastDisplay.a(n, R.string.network_error, 0);
                return;
            }
            if (p.d(password, confPassword, tvAuthFailedText)) {
                Map<String, String> map = new HashMap<>();
                map.put("password", password);
                map.put("confPassword", confPassword);
                map.put("token", token);
                map.put("user", String.valueOf(user));
                new AsyncRequest(UpdatePasswordFlow.this, UpdatePasswordFlow.this, getResources().getString(R.string.updating_dialog), new RequestData(URLConfig.UPDATE_PASSWORD, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
    }
}