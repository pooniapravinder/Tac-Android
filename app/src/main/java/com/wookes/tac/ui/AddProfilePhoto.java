package com.wookes.tac.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.wookes.tac.R;
import com.wookes.tac.fragments.GalleryDialogImage;
import com.wookes.tac.util.StatusBar;

public class AddProfilePhoto extends AppCompatActivity {
    private Context context;

    @Override
    protected void onCreate(Bundle var1) {
        super.onCreate(var1);
        StatusBar.setStatusBarNavigationBarWhite(getWindow());
        setContentView(R.layout.activity_layout_add_profile_photo);
        context = this;
        findViewById(R.id.add).setOnClickListener(view -> {
            final GalleryDialogImage galleryDialogImage = GalleryDialogImage.newInstance();
            Bundle args = new Bundle();
            args.putSerializable("signUpFlow", true);
            galleryDialogImage.setArguments(args);
            galleryDialogImage.show(((AppCompatActivity) context).getSupportFragmentManager(), GalleryDialogImage.TAG);
        });
        findViewById(R.id.skip).setOnClickListener(view -> {
            Intent intent = new Intent(context, LandingUi.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}
