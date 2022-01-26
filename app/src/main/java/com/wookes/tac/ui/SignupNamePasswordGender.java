package com.wookes.tac.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.wookes.tac.R;
import com.wookes.tac.util.StatusBar;
import com.wookes.tac.util.d;

import java.util.Calendar;

public class SignupNamePasswordGender extends AppCompatActivity {
    private RadioGroup gender;
    private EditText etFullname;
    private EditText etPassword;
    private TextView tvAuthFailedText;
    private d d;

    @Override
    protected void onCreate(Bundle var1) {
        super.onCreate(var1);
        StatusBar.setStatusBarNavigationBarWhite(getWindow());
        setContentView(R.layout.activity_layout_signup_name_password_gender);
        d = new d(this);
        etFullname = findViewById(R.id.et_fullname);
        etPassword = findViewById(R.id.et_password);
        gender = findViewById(R.id.gender);
        Button btnNext = findViewById(R.id.btn_next);
        tvAuthFailedText = findViewById(R.id.tv_auth_failed_text);
        ((TextView) findViewById(R.id.copyright)).setText(getString(R.string.copyright, Calendar.getInstance().get(Calendar.YEAR)));
        btnNext.setOnClickListener(view -> {
            if (d.b(etFullname.getText().toString(), etPassword.getText().toString(), tvAuthFailedText)) {
                Intent intent = new Intent(SignupNamePasswordGender.this, SignupPhone.class);
                intent.putExtra("username", getIntent().getStringExtra("username"));
                intent.putExtra("fullname", etFullname.getText().toString().trim());
                intent.putExtra("password", etPassword.getText().toString().trim());
                intent.putExtra("gender", ((RadioButton) findViewById((gender.getCheckedRadioButtonId()))).getText());
                startActivity(intent);
            }
        });
    }
}