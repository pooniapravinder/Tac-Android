package com.wookes.tac.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.wookes.tac.R;
import com.wookes.tac.util.StatusBar;

public class UserProfile extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_user_profile);
        StatusBar.setStatusBarNavigationBarWhite(getWindow());
        loadFragment(Profile.newInstance());
    }

    private void loadFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("user", getIntent().getStringExtra("user"));
        bundle.putInt("user_type", getIntent().getIntExtra("user_type", 0));
        fragment.setArguments(bundle);
        String backStateName = fragment.getClass().getName();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment, backStateName);
        fragmentTransaction.commit();
    }
}
