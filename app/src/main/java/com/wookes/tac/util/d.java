package com.wookes.tac.util;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.wookes.tac.R;


public class d {
    private Context context;

    public d(Context context) {
        this.context = context;
    }

    public boolean b(String fullName, String password, TextView error) {
        if (fullName.length() < 2 || fullName.length() > 30) {
            error.setVisibility(View.VISIBLE);
            error.setText(context.getResources().getString(R.string.invalid_fullname_length));
            return false;
        } else if (!fullName.matches("^(([A-Za-z\\s]){2,30})*$")) {
            error.setVisibility(View.VISIBLE);
            error.setText(context.getResources().getString(R.string.invalid_fullname));
            return false;
        }else if(password.length() < 5 || password.length() > 16) {
            error.setVisibility(View.VISIBLE);
            error.setText(context.getResources().getString(R.string.invalid_password_length));
            return false;
        }
        error.setVisibility(View.GONE);
        return true;
    }

    public boolean c(String phoneNumber, TextView error) {
        if (!phoneNumber.matches("^(?=.*[0-9]).{10}$")) {
            error.setVisibility(View.VISIBLE);
            error.setText(context.getResources().getString(R.string.invalid_phone));
            return false;
        }
        error.setVisibility(View.GONE);
        return true;
    }

    public boolean d(String password, String confPassword, TextView error) {
        if(password.length() < 5 || password.length() > 16) {
            error.setVisibility(View.VISIBLE);
            error.setText(context.getResources().getString(R.string.invalid_password_length));
            return false;
        }else if(!password.equals(confPassword)){
            error.setVisibility(View.VISIBLE);
            error.setText(context.getResources().getString(R.string.password_mismatch));
            return false;
        }
        error.setVisibility(View.GONE);
        return true;
    }

}
