package com.wookes.tac.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wookes.tac.R;
import com.wookes.tac.adapter.ProfileViewpagerAdapter;
import com.wookes.tac.model.ProfileModel;
import com.wookes.tac.request.AsyncRequest;
import com.wookes.tac.request.RequestData;
import com.wookes.tac.request.ResponseResult;
import com.wookes.tac.urlconfig.URLConfig;
import com.wookes.tac.util.CustomCounter;
import com.wookes.tac.util.GlideApp;
import com.wookes.tac.util.SelfUser;
import com.wookes.tac.util.StatusBar;
import com.wookes.tac.util.ToastDisplay;
import com.wookes.tac.util.c;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Profile extends Fragment implements ResponseResult {
    private Context context;
    private AppCompatActivity activity;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int topInset;
    private TextView username, fullname, following, followers, likes, bio;
    private ImageView profilePic, moreOptions, verified;
    private Button follow, unfollow, editProfile;
    private ProgressBar progressBar;
    private ProfileModel currentUser;
    private View middle;
    private String intentUsername;
    private long followingTotal;
    private long followersTotal;
    private long profileOfUser;
    private int userType;

    public static Profile newInstance() {
        return new Profile();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.activity_layout_profile, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        activity = (AppCompatActivity) getActivity();
        currentUser = new SelfUser(getActivity()).getSelfUser();
        Map<String, String> map = new HashMap<>();
        initView(getView());
        AppCompatActivity appCompatActivity = ((AppCompatActivity) getActivity());
        appCompatActivity.setSupportActionBar(toolbar);
        appCompatActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
        if (getArguments() != null) {
            userType = getArguments().getInt("user_type", 0);
            if (getArguments().getString("user") != null) {
                map.put("user", getArguments().getString("user"));
            }
        }
        if (activity instanceof UserProfile)
            appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        map.put("user_type", String.valueOf(userType));
        StatusBar.getInset(appBarLayout, (topInset2, bottomInset2) -> {
            topInset = topInset2;
            appBarLayout.setPadding(0, topInset, 0, 0);
        });
        new AsyncRequest(getActivity(), Profile.this, null, new RequestData(URLConfig.RETRIEVE_PROFILE_INFO, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        (getView().findViewById(R.id.followers_layout)).setOnClickListener(view -> startFollowingFollowers(false));
        (getView().findViewById(R.id.following_layout)).setOnClickListener(view -> startFollowingFollowers(true));
        moreOptions.setOnClickListener(view -> startActivity(new Intent(getActivity(), SettingsOp.class)));
    }

    private void initView(View view) {
        appBarLayout = view.findViewById(R.id.appBar);
        toolbar = view.findViewById(R.id.toolbar);
        tabLayout = view.findViewById(R.id.tabs);
        viewPager = view.findViewById(R.id.viewpager);
        username = view.findViewById(R.id.username);
        fullname = view.findViewById(R.id.fullname);
        following = view.findViewById(R.id.following);
        followers = view.findViewById(R.id.followers);
        likes = view.findViewById(R.id.likes);
        bio = view.findViewById(R.id.bio);
        profilePic = view.findViewById(R.id.profile_pic);
        verified = view.findViewById(R.id.verified);
        follow = view.findViewById(R.id.follow);
        unfollow = view.findViewById(R.id.unfollow);
        editProfile = view.findViewById(R.id.edit_profile);
        progressBar = view.findViewById(R.id.progress_bar);
        middle = view.findViewById(R.id.middle);
        moreOptions = view.findViewById(R.id.more_options);
        editProfile.setOnClickListener(view1 -> startActivityForResult(new Intent(getActivity(), EditProfile.class), 1));
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
            middle.setVisibility(View.VISIBLE);
            Type type = new TypeToken<ProfileModel>() {
            }.getType();
            ProfileModel profileModel = new Gson().fromJson(jsonObject.getString("data"), type);
            ProfileModel.ExtraData extraData = null;
            if (jsonObject.has("extra_data")) {
                type = new TypeToken<ProfileModel.ExtraData>() {
                }.getType();
                extraData = new Gson().fromJson(jsonObject.getString("extra_data"), type);
            }
            username.setText(getActivity() != null ? getActivity().getString(R.string.profile_username, profileModel.getUserName()) : "");
            intentUsername = profileModel.getUserName();
            verified.setVisibility(profileModel.isVerified() ? View.VISIBLE : View.GONE);
            fullname.setText(profileModel.getFullName());
            following.setText(CustomCounter.format(profileModel.getFollowing()));
            followingTotal = profileModel.getFollowing();
            followers.setText(CustomCounter.format(profileModel.getFollowers()));
            followersTotal = profileModel.getFollowers();
            profileOfUser = profileModel.getUserId();
            likes.setText(CustomCounter.format(profileModel.getLikes()));
            if (!TextUtils.isEmpty(profileModel.getBio())) {
                bio.setVisibility(View.VISIBLE);
                bio.setText(profileModel.getBio());
            } else {
                bio.setVisibility(View.GONE);
            }
            if (getActivity() != null)
                GlideApp.loadProfilePic(getActivity().getApplicationContext(), profileModel.getProfilePic(), profilePic);
            if (extraData != null && currentUser != null && profileModel.getUserId() != currentUser.getUserId()) {
                if (extraData.isFollowing()) {
                    unfollow.setVisibility(View.VISIBLE);
                } else {
                    follow.setVisibility(View.VISIBLE);
                }
                ProfileModel.ExtraData finalExtraData = extraData;
                follow.setOnClickListener(view -> {
                    finalExtraData.setFollowing(true);
                    profileModel.setFollowers(profileModel.getFollowers() + 1);
                    followersTotal = profileModel.getFollowers();
                    followers.setText(CustomCounter.format(profileModel.getFollowers()));
                    toggleFollowUnfollow();
                    Map<String, String> map = new HashMap<>();
                    map.put("user", String.valueOf(profileModel.getUserId()));
                    new AsyncRequest(getActivity(), output1 -> {
                        try {
                            JSONObject jsonObject1 = new JSONObject(output1);
                            if (!jsonObject1.has("success")) {
                                finalExtraData.setFollowing(false);
                                profileModel.setFollowers(profileModel.getFollowers() - 1);
                                followersTotal = profileModel.getFollowers();
                                followers.setText(CustomCounter.format(profileModel.getFollowers()));
                                Profile.this.toggleFollowUnfollow();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, null, new RequestData(URLConfig.FOLLOW_USER, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                });
                unfollow.setOnClickListener(view -> {
                    finalExtraData.setFollowing(false);
                    profileModel.setFollowers(profileModel.getFollowers() - 1);
                    followersTotal = profileModel.getFollowers();
                    followers.setText(CustomCounter.format(profileModel.getFollowers()));
                    toggleFollowUnfollow();
                    Map<String, String> map = new HashMap<>();
                    map.put("user", String.valueOf(profileModel.getUserId()));
                    new AsyncRequest(getActivity(), output1 -> {
                        try {
                            JSONObject jsonObject1 = new JSONObject(output1);
                            if (!jsonObject1.has("success")) {
                                finalExtraData.setFollowing(true);
                                profileModel.setFollowers(profileModel.getFollowers() + 1);
                                followersTotal = profileModel.getFollowers();
                                followers.setText(CustomCounter.format(profileModel.getFollowers()));
                                toggleFollowUnfollow();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, null, new RequestData(URLConfig.UNFOLLOW_USER, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                });
            } else {
                if (getActivity() != null && !(new c(getActivity())).isLoggedIn()) {
                    follow.setVisibility(View.VISIBLE);
                    follow.setOnClickListener(view -> context.startActivity(new Intent(activity, Login.class)));
                } else {
                    editProfile.setVisibility(View.VISIBLE);
                    moreOptions.setVisibility(View.VISIBLE);
                }
            }
            tabLayout.removeAllTabs();
            tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_profile_videos));
            tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_comment_like_nor));
            if (currentUser != null && profileModel.getUserId() == currentUser.getUserId()) {
                tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_saved_empty));
            }
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            if (getActivity() != null && isAdded()) {
                final ProfileViewpagerAdapter profileViewpagerAdapter = new ProfileViewpagerAdapter(getChildFragmentManager(), tabLayout.getTabCount(), getActivity() != null && getActivity().findViewById(R.id.navigation) != null ? getActivity().findViewById(R.id.navigation).getHeight() : 0, profileModel.getUserId());
                viewPager.setAdapter(profileViewpagerAdapter);
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
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            (new c(getActivity())).refreshProfile(getActivity());
            Map<String, String> map = new HashMap<>();
            map.put("user_type", "0");
            new AsyncRequest(getActivity(), Profile.this, null, new RequestData(URLConfig.RETRIEVE_PROFILE_INFO, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void toggleFollowUnfollow() {
        follow.setVisibility(follow.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        unfollow.setVisibility(unfollow.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    private void startFollowingFollowers(boolean isFollowing) {
        Intent intent = new Intent(getActivity(), FollowingFollowers.class);
        intent.putExtra("tab", isFollowing ? 0 : 1);
        intent.putExtra("userId", profileOfUser);
        intent.putExtra("username", intentUsername);
        intent.putExtra("followingTotal", followingTotal);
        intent.putExtra("followersTotal", followersTotal);
        startActivity(intent);
    }
}
