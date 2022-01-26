package com.wookes.tac.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.wookes.tac.model.ProfileModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SelfUser {
    private SharedPreferences selfUser;

    public SelfUser(Context context) {
        selfUser = context.getSharedPreferences("self_user", Context.MODE_PRIVATE);
    }

    public ProfileModel getSelfUser() {
        Type type = new TypeToken<ProfileModel>() {
        }.getType();
        String data = selfUser.getString("user", null);
        if (data == null) {
            return null;
        } else {
            return new Gson().fromJson(data, type);
        }
    }

    public void saveUser(ProfileModel profileModel) {
        SharedPreferences.Editor c = selfUser.edit();
        c.putString("user", new Gson().toJson(profileModel));
        c.apply();
    }

    public void removeUser() {
        SharedPreferences.Editor c = selfUser.edit();
        c.remove("user");
        c.apply();
    }
}
