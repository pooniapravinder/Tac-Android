package com.wookes.tac.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.wookes.tac.R;
import com.wookes.tac.fragments.GalleryDialogImage;
import com.wookes.tac.model.ProfileModel;
import com.wookes.tac.request.AsyncRequest;
import com.wookes.tac.request.RequestData;
import com.wookes.tac.request.ResponseResult;
import com.wookes.tac.urlconfig.URLConfig;
import com.wookes.tac.util.GlideApp;
import com.wookes.tac.util.StatusBar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class EditProfile extends AppCompatActivity implements ResponseResult {
    private Toolbar toolbar;
    private TextView username, fullname, bio, errorLayout, changePhoto;
    private ImageView profilePic;
    private View dataLoad;
    private Context context;
    private View profileData, saving;
    private ImageButton save;
    private Timer timer = new Timer();
    private boolean isTyping = false;
    private ProgressBar usernameLoader;
    private TextInputLayout bioInputLayout;
    private boolean isFirst = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_edit_profile);
        StatusBar.setStatusBarNavigationBarWhite(getWindow());
        context = this;
        initView();
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("user_type", "0");
        new AsyncRequest(context, EditProfile.this, null, new RequestData(URLConfig.RETRIEVE_PROFILE_INFO, hashMap, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        save.setOnClickListener(view -> {
            errorLayout.setText("");
            saving.setVisibility(View.VISIBLE);
            save.setVisibility(View.GONE);
            Map<String, String> map = new HashMap<>();
            map.put("username", username.getText().toString());
            map.put("fullname", fullname.getText().toString());
            try {
                map.put("bio", URLEncoder.encode(bio.getText().toString(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            new AsyncRequest(context, saveProfile, null, new RequestData(URLConfig.UPDATE_PROFILE_INFO, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        });

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                runOnUiThread(() -> {
                    if (!isFirst) usernameLoader.setVisibility(View.VISIBLE);
                });
                if (!isTyping) {
                    isTyping = true;
                }
                timer.cancel();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        isTyping = false;
                        if (!isFirst) checkUsername();
                        isFirst = false;
                    }
                }, 500);
            }
        });

        bio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    bioInputLayout.setHint(getString(R.string.bio_hint_counter, s.toString().length()));
                } else {
                    bioInputLayout.setHint(getString(R.string.bio));
                }
            }

            @Override
            public void afterTextChanged(final Editable s) {
            }
        });
        changePhoto.setOnClickListener(view -> {
            GalleryDialogImage.newInstance().show(((AppCompatActivity) context).getSupportFragmentManager(), GalleryDialogImage.TAG);
        });
    }

    private final ResponseResult saveProfile = new ResponseResult() {
        @Override
        public void onTaskDone(String output) {
            saving.setVisibility(View.GONE);
            save.setVisibility(View.VISIBLE);
            try {
                JSONObject jsonObject = new JSONObject(output);
                if (!jsonObject.has("success")) {
                    errorLayout.setText(jsonObject.getString("error"));
                    return;
                }
                setResult(Activity.RESULT_OK, new Intent());
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        errorLayout = findViewById(R.id.error_layout);
        username = findViewById(R.id.username);
        fullname = findViewById(R.id.fullname);
        bio = findViewById(R.id.bio);
        bioInputLayout = findViewById(R.id.et_bio_input_layout);
        profilePic = findViewById(R.id.profile_pic);
        dataLoad = findViewById(R.id.data_load);
        profileData = findViewById(R.id.profile_data);
        save = findViewById(R.id.save);
        saving = findViewById(R.id.saving);
        usernameLoader = findViewById(R.id.username_loader);
        changePhoto = findViewById(R.id.change_photo);
    }

    @Override
    public void onTaskDone(String output) {
        dataLoad.setVisibility(View.GONE);
        profileData.setVisibility(View.VISIBLE);
        try {
            JSONObject jsonObject = new JSONObject(output);
            if (!jsonObject.has("success")) {
                return;
            }
            Type type = new TypeToken<ProfileModel>() {
            }.getType();
            ProfileModel profileModel = new Gson().fromJson(jsonObject.getString("data"), type);
            username.setText(profileModel.getUserName());
            fullname.setText(profileModel.getFullName());
            bio.setText(profileModel.getBio());
            GlideApp.loadProfilePic(getApplicationContext(), profileModel.getProfilePic(), profilePic);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkUsername() {
        Map<String, String> map = new HashMap<>();
        map.put("username", username.getText().toString());
        new AsyncRequest(this, checkUsername, null, new RequestData(URLConfig.SIGN_UP_USERNAME_URL, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private final ResponseResult checkUsername = new ResponseResult() {
        @Override
        public void onTaskDone(String output) {
            runOnUiThread(() -> usernameLoader.setVisibility(View.GONE));
            try {
                JSONObject root = new JSONObject(output);
                if (!root.has("success")) {
                    errorLayout.setText(root.getString("error"));
                    return;
                }
                errorLayout.setText("");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("user_type", "0");
            new AsyncRequest(context, EditProfile.this, null, new RequestData(URLConfig.RETRIEVE_PROFILE_INFO, hashMap, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }
}
