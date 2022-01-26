package com.wookes.tac.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.wookes.tac.model.PostUploadModel;
import com.wookes.tac.model.ProfileModel;
import com.wookes.tac.request.AsyncRequest;
import com.wookes.tac.request.PersistentCookieStore;
import com.wookes.tac.request.RequestData;
import com.wookes.tac.request.ResponseResult;
import com.wookes.tac.ui.LandingUi;
import com.wookes.tac.urlconfig.URLConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class c {
    private SharedPreferences originValue;
    private SharedPreferences firstTimeOpen;
    private SharedPreferences postUploadModelsPref;
    private SharedPreferences playerPrefs;
    private Context context;
    private Map<String, String> map = new HashMap<>();

    public c(Context context) {
        this.context = context;
        this.firstTimeOpen = context.getSharedPreferences("first_init", Context.MODE_PRIVATE);
        this.originValue = context.getSharedPreferences("origin_value", Context.MODE_PRIVATE);
        this.postUploadModelsPref = context.getSharedPreferences("post_upload_models", Context.MODE_PRIVATE);
        this.playerPrefs = context.getSharedPreferences("player_prefs", Context.MODE_PRIVATE);
    }

    public void logInUser() {
        refreshNotificationToken(FirebaseInstanceId.getInstance().getToken());
        SharedPreferences.Editor editor = originValue.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }

    public void putFirstTime(boolean val) {
        SharedPreferences.Editor editor = firstTimeOpen.edit();
        editor.putBoolean("first", val);
        editor.apply();
    }

    public void playerPrefs(boolean isFullscreen) {
        SharedPreferences.Editor editor = playerPrefs.edit();
        editor.putBoolean("fullscreen", isFullscreen);
        editor.apply();
    }

    public boolean isFullscreenPlayer() {
        return playerPrefs.getBoolean("fullscreen", false);
    }

    public void removeUser() {
        refreshNotificationToken(FirebaseInstanceId.getInstance().getToken());
        SharedPreferences.Editor editor = originValue.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return originValue.getBoolean("isLoggedIn", false);
    }

    public boolean isFirstTime() {
        return firstTimeOpen.getBoolean("first", true);
    }

    public PostUploadModel getStoredPostUploadModels() {
        Type type = new TypeToken<PostUploadModel>() {
        }.getType();
        String data = postUploadModelsPref.getString("postUploadModels", null);
        if (data == null) {
            return null;
        } else {
            return new Gson().fromJson(data, type);
        }
    }

    public void storePostUploadModel(PostUploadModel postUploadModel) {
        SharedPreferences.Editor c = postUploadModelsPref.edit();
        c.putString("postUploadModels", new Gson().toJson(postUploadModel));
        c.apply();
    }

    public void removePostUploadModel() {
        PostDataGenerate.deletePostDirectory(context);
        SharedPreferences.Editor c = postUploadModelsPref.edit();
        c.clear();
        c.apply();
    }

    public void removePostUploadModelAll() {
        SharedPreferences.Editor c = postUploadModelsPref.edit();
        c.clear();
        c.apply();
    }

    public void refreshProfile(Context context) {
        map.put("user_type", String.valueOf(0));
        new AsyncRequest(context, responseResult, null, new RequestData(URLConfig.RETRIEVE_PROFILE_INFO, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void refreshNotificationToken(String token) {
        map.put("token", token);
        new AsyncRequest(context, output -> {
        }, null, new RequestData(URLConfig.SAVE_NOTIFICATION_TOKEN, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void logoutUser() {
        PersistentCookieStore persistentCookieStore = new PersistentCookieStore(context);
        persistentCookieStore.removeAll();
        removePostUploadModelAll();
        removeUser();
        new SelfUser(context).removeUser();
        Intent localIntent = new Intent(context, LandingUi.class);
        localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(localIntent);
    }

    private ResponseResult responseResult = new ResponseResult() {
        @Override
        public void onTaskDone(String output) {
            try {
                JSONObject jsonObject = new JSONObject(output);
                if (!jsonObject.has("success")) {
                    return;
                }
                Type type = new TypeToken<ProfileModel>() {
                }.getType();
                ProfileModel profileModel = new Gson().fromJson(jsonObject.getString("data"), type);
                new SelfUser(context).saveUser(profileModel);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
