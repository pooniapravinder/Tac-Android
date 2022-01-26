package com.wookes.tac.adapter;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wookes.tac.R;
import com.wookes.tac.model.FollowingFollowersModel;
import com.wookes.tac.request.AsyncRequest;
import com.wookes.tac.request.RequestData;
import com.wookes.tac.ui.LandingUi;
import com.wookes.tac.ui.Login;
import com.wookes.tac.ui.UserProfile;
import com.wookes.tac.urlconfig.URLConfig;
import com.wookes.tac.util.GlideApp;
import com.wookes.tac.util.SelfUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FollowingFollowersDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<FollowingFollowersModel> followingFollowersModels;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private boolean areFollowers;
    private SelfUser selfUser;
    private long profileOfUser;

    public FollowingFollowersDataAdapter(Context context, ArrayList<FollowingFollowersModel> followingFollowersModels, boolean areFollowers, long profileOfUser) {
        this.context = context;
        this.followingFollowersModels = followingFollowersModels;
        this.areFollowers = areFollowers;
        this.selfUser = new SelfUser(context);
        this.profileOfUser = profileOfUser;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_layout_following_followers, viewGroup, false);
            return new ItemViewHolder(view);
        } else if (i == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_loading, viewGroup, false);
            return new LoadingViewHolder(view);
        } else {
            throw new IllegalArgumentException("Invalid ViewType Provided : " + this.getClass());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        if (viewHolder instanceof ItemViewHolder) {
            populateItemRows((ItemViewHolder) viewHolder, i);
        } else if (viewHolder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) viewHolder, i);
        }
    }

    @Override
    public int getItemCount() {
        return followingFollowersModels == null ? 0 : followingFollowersModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return followingFollowersModels.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView profilePic;
        private TextView username, fullName;
        private Button unfollow, follow, remove;

        ItemViewHolder(View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profile_pic);
            username = itemView.findViewById(R.id.username);
            fullName = itemView.findViewById(R.id.full_name);
            unfollow = itemView.findViewById(R.id.unfollow);
            follow = itemView.findViewById(R.id.follow);
            remove = itemView.findViewById(R.id.remove);
        }
    }

    private void populateItemRows(ItemViewHolder holder, int i) {
        GlideApp.loadProfilePic(context, followingFollowersModels.get(i).getProfilePic(), holder.profilePic);
        holder.username.setText(String.valueOf(followingFollowersModels.get(i).getUserName()));
        holder.fullName.setText(String.valueOf(followingFollowersModels.get(i).getFullName()));
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, UserProfile.class);
            intent.putExtra("user", String.valueOf(followingFollowersModels.get(i).getUserId()));
            intent.putExtra("user_type", 0);
            context.startActivity(intent);
        });
        if (areFollowers && selfUser.getSelfUser().getUserId() == profileOfUser) {
            holder.remove.setVisibility(View.VISIBLE);
        } else {
            holder.remove.setVisibility(View.GONE);
            if (selfUser.getSelfUser() == null) {
                holder.follow.setVisibility(View.VISIBLE);
                holder.follow.setOnClickListener(view -> context.startActivity(new Intent(context, Login.class)));
            } else {
                if (selfUser.getSelfUser().getUserId() == followingFollowersModels.get(i).getUserId()) {
                    holder.unfollow.setVisibility(View.GONE);
                    holder.follow.setVisibility(View.GONE);
                } else {
                    if (followingFollowersModels.get(i).isFollowing()) {
                        holder.unfollow.setVisibility(View.VISIBLE);
                        holder.follow.setVisibility(View.GONE);
                    } else {
                        holder.unfollow.setVisibility(View.GONE);
                        holder.follow.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
        holder.remove.setOnClickListener(view -> {
            holder.remove.setVisibility(View.GONE);
            Map<String, String> map = new HashMap<>();
            map.put("user", String.valueOf(followingFollowersModels.get(i).getUserId()));
            new AsyncRequest(context, output -> {
                try {
                    JSONObject jsonObject = new JSONObject(output);
                    if (!jsonObject.has("success")) {
                        holder.remove.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, null, new RequestData(URLConfig.REMOVE_FOLLOWER, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        });
        holder.unfollow.setOnClickListener(view -> {
            toggleFollowUnfollow(holder);
            Map<String, String> map = new HashMap<>();
            map.put("user", String.valueOf(followingFollowersModels.get(i).getUserId()));
            new AsyncRequest(context, output -> {
                try {
                    JSONObject jsonObject = new JSONObject(output);
                    if (!jsonObject.has("success")) {
                        toggleFollowUnfollow(holder);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, null, new RequestData(URLConfig.UNFOLLOW_USER, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        });
        holder.follow.setOnClickListener(view -> {
            if (selfUser.getSelfUser() == null) {
                context.startActivity(new Intent(context, Login.class));
                return;
            }
            toggleFollowUnfollow(holder);
            Map<String, String> map = new HashMap<>();
            map.put("user", String.valueOf(followingFollowersModels.get(i).getUserId()));
            new AsyncRequest(context, output -> {
                try {
                    JSONObject jsonObject = new JSONObject(output);
                    if (!jsonObject.has("success")) {
                        toggleFollowUnfollow(holder);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, null, new RequestData(URLConfig.FOLLOW_USER, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        });
    }

    private static class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed
    }

    private void toggleFollowUnfollow(ItemViewHolder holder) {
        holder.follow.setVisibility(holder.follow.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        holder.unfollow.setVisibility(holder.unfollow.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

}