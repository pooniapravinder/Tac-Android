package com.wookes.tac.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.wookes.tac.R;
import com.wookes.tac.adapter.HashtagViewpagerAdapter;
import com.wookes.tac.model.HashtagModel;
import com.wookes.tac.request.AsyncRequest;
import com.wookes.tac.request.RequestData;
import com.wookes.tac.request.ResponseResult;
import com.wookes.tac.urlconfig.URLConfig;
import com.wookes.tac.util.CustomCounter;
import com.wookes.tac.util.GlideApp;
import com.wookes.tac.util.StatusBar;
import com.wookes.tac.util.ToastDisplay;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;

public class Hashtag extends AppCompatActivity implements ResponseResult {
    private AppBarLayout appBarLayout;
    private LinearLayout upperLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int topInset;
    private int bottomInset;
    private Context context;
    private TextView hashtagName, noOfVideos;
    private ImageView hashtagPic;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private final HashMap<String, String> hashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle var1) {
        super.onCreate(var1);
        StatusBar.windowTransparentWhite(this, getWindow(), true);
        setContentView(R.layout.activity_layout_hashtag);
        context = this;
        String hashtag = getIntent().getStringExtra("hashtag");
        int hashtagType = getIntent().getIntExtra("hashtag_type", 0);
        initView();
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        StatusBar.getInset(appBarLayout, (topInset1, bottomInset1) -> {
            topInset = topInset1;
            bottomInset = bottomInset1;
            upperLayout.setPadding(0, topInset, 0, 0);
        });
        hashMap.put("hashtag", hashtag);
        hashMap.put("hashtag_type", String.valueOf(hashtagType));
        new AsyncRequest(this, Hashtag.this, null, new RequestData(URLConfig.RETRIEVE_HASHTAG, hashMap, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void initView() {
        appBarLayout = findViewById(R.id.appBar);
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progress_bar);
        upperLayout = findViewById(R.id.upperLayout);
        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewpager);
        hashtagName = findViewById(R.id.hashtag_name);
        hashtagPic = findViewById(R.id.hashtag_pic);
        noOfVideos = findViewById(R.id.no_of_videos);
    }

    @Override
    public void onTaskDone(String output) {
        progressBar.setVisibility(View.GONE);
        try {
            JSONObject jsonObject = new JSONObject(output);
            if (!jsonObject.has("success")) {
                ToastDisplay.a(context, jsonObject.getString("error"), 0);
                return;
            }
            noOfVideos.setVisibility(View.VISIBLE);
            Type type = new TypeToken<HashtagModel>() {
            }.getType();
            HashtagModel hashtagModel = new Gson().fromJson(jsonObject.getString("data"), type);
            hashtagName.setText(getString(R.string.hashtag_name, hashtagModel.getHashtagName()));
            GlideApp.loadSimple(context, hashtagModel.getThumbnail(), hashtagPic);
            noOfVideos.setText(getString(R.string.hashtag_total_videos, CustomCounter.format(hashtagModel.getHashtagTotal())));
            tabLayout.setPadding(0, topInset, 0, 0);
            tabLayout.addTab(tabLayout.newTab().setText("Top"));
            tabLayout.addTab(tabLayout.newTab().setText("Recent"));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            final HashtagViewpagerAdapter hashtagViewpagerAdapter = new HashtagViewpagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), bottomInset, hashtagModel.getHashtagId());
            viewPager.setAdapter(hashtagViewpagerAdapter);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
