package com.wookes.tac.ui.h;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wookes.tac.model.PAuthOutputModel;
import com.wookes.tac.model.PostUploadModel;
import com.wookes.tac.util.AssD;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class PublishUtil {

    public static String parseUrl(int id, PostUploadModel postUploadModel) {
        PAuthOutputModel pAuthOutputModel = parseObject(id, postUploadModel);
        return pAuthOutputModel != null ? pAuthOutputModel.getAuth() : null;
    }

    public static String parseUrl(int id, String authKey, String auth) {
        String data = AssD.a(authKey, auth);
        PAuthOutputModel pAuthOutputModel = getModel(id, data);
        return pAuthOutputModel != null ? pAuthOutputModel.getAuth() : null;
    }

    public static PAuthOutputModel parseObject(int id, PostUploadModel postUploadModel) {
        String data = AssD.a(postUploadModel.getAuthKey(), postUploadModel.getAuth());
        return getModel(id, data);
    }

    public static PAuthOutputModel parseObject(int id, String authKey, String auth) {
        String data = AssD.a(authKey, auth);
        return getModel(id, data);
    }

    private static PAuthOutputModel getModel(int id, String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            Type type = new TypeToken<ArrayList<PAuthOutputModel>>() {
            }.getType();
            ArrayList<PAuthOutputModel> pAuthOutputModels = new Gson().fromJson(jsonObject.getString("data"), type);
            for (PAuthOutputModel pAuthOutputModel : pAuthOutputModels) {
                if (pAuthOutputModel.getId() == id) {
                    return pAuthOutputModel;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
