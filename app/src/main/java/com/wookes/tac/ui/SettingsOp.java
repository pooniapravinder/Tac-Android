package com.wookes.tac.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.wookes.tac.R;
import com.wookes.tac.urlconfig.URLConfig;
import com.wookes.tac.util.StatusBar;
import com.wookes.tac.util.c;

public class SettingsOp extends AppCompatActivity {
    private c c;
    private TextView termsOfUse;
    private TextView privacyPolicy;
    private TextView communityGuidelines;
    private TextView creator;
    private TextView balance;
    private TextView logout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle var1) {
        super.onCreate(var1);
        StatusBar.setStatusBarNavigationBarWhite(getWindow());
        setContentView(R.layout.activity_layout_settings);
        c = new c(this);
        initViews();
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        termsOfUse.setOnClickListener(view -> openBrowser(URLConfig.TERMS_OF_USE));
        privacyPolicy.setOnClickListener(view -> openBrowser(URLConfig.PRIVACY_POLICY));
        communityGuidelines.setOnClickListener(view -> openBrowser(URLConfig.COMMUNITY_GUIDELINES));
        creator.setOnClickListener(view -> startActivity(new Intent(SettingsOp.this, Creator.class)));
        balance.setOnClickListener(view -> startActivity(new Intent(SettingsOp.this, Balance.class)));
        logout.setOnClickListener(view -> c.logoutUser());
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        creator = findViewById(R.id.creator);
        balance = findViewById(R.id.balance);
        termsOfUse = findViewById(R.id.terms_of_use);
        privacyPolicy = findViewById(R.id.privacy_policy);
        communityGuidelines = findViewById(R.id.community_guidelines);
        logout = findViewById(R.id.log_out);
    }

    private void openBrowser(String link) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(browserIntent);
    }
}
