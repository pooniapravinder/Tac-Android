package com.wookes.tac.adapter;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wookes.tac.R;
import com.wookes.tac.model.NewUserFollowModel;
import com.wookes.tac.request.AsyncRequest;
import com.wookes.tac.request.RequestData;
import com.wookes.tac.ui.LandingUi;
import com.wookes.tac.ui.Login;
import com.wookes.tac.ui.UserProfile;
import com.wookes.tac.urlconfig.URLConfig;
import com.wookes.tac.util.GlideApp;
import com.wookes.tac.util.SelfUser;
import com.wookes.tac.util.ToastDisplay;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FollowHolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<NewUserFollowModel> newUserFollowModels;
    private final int VIEW_TYPE_ITEM = 0;
    private SelfUser selfUser;
    private HashMap<String, String> map = new HashMap<>();

    public FollowHolderAdapter(Context context, ArrayList<NewUserFollowModel> newUserFollowModels) {
        this.context = context;
        this.selfUser = new SelfUser(context);
        this.newUserFollowModels = newUserFollowModels;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_layout_follow_home_view, viewGroup, false);
            return new ItemViewHolder(view);
        } else {
            throw new IllegalArgumentException("Invalid ViewType Provided : " + this.getClass());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        if (viewHolder instanceof ItemViewHolder) {
            populateItemRows((ItemViewHolder) viewHolder, i);
        }
    }

    @Override
    public int getItemCount() {
        return newUserFollowModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_ITEM;
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView profilePic, verified;
        private TextView username;
        private TextView fullname;
        private Button follow, unfollow;

        ItemViewHolder(View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profile_pic);
            username = itemView.findViewById(R.id.username);
            verified = itemView.findViewById(R.id.verified);
            fullname = itemView.findViewById(R.id.fullname);
            follow = itemView.findViewById(R.id.follow);
            unfollow = itemView.findViewById(R.id.unfollow);
        }
    }

    private void populateItemRows(ItemViewHolder holder, int i) {
        GlideApp.loadProfilePic(context, newUserFollowModels.get(i).getProfilePic(), holder.profilePic);
        holder.username.setText(newUserFollowModels.get(i).getUserName());
        holder.verified.setVisibility(newUserFollowModels.get(i).isVerified() ? View.VISIBLE : View.GONE);
        holder.fullname.setText(newUserFollowModels.get(i).getFullName());
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, UserProfile.class);
            intent.putExtra("user", String.valueOf(newUserFollowModels.get(i).getUserId()));
            intent.putExtra("user_type", 0);
            context.startActivity(intent);
        });
        holder.follow.setOnClickListener(view -> {
            map.put("user", String.valueOf(newUserFollowModels.get(i).getUserId()));
            if (selfUser.getSelfUser() == null) {
                context.startActivity(new Intent(context, Login.class));
                return;
            }
            toggleFollowUnfollow(holder);
            new AsyncRequest(context, output -> {
                try {
                    JSONObject jsonObject = new JSONObject(output);
                    if (!jsonObject.has("success")) {
                        toggleFollowUnfollow(holder);
                        ToastDisplay.a(context, jsonObject.getString("error"), 0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, null, new RequestData(URLConfig.FOLLOW_USER, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        });
        holder.unfollow.setOnClickListener(view -> {
            map.put("user", String.valueOf(newUserFollowModels.get(i).getUserId()));
            if (selfUser.getSelfUser() == null) {
                context.startActivity(new Intent(context, Login.class));
                return;
            }
            toggleFollowUnfollow(holder);
            new AsyncRequest(context, output -> {
                try {
                    JSONObject jsonObject = new JSONObject(output);
                    if (!jsonObject.has("success")) {
                        toggleFollowUnfollow(holder);
                        ToastDisplay.a(context, jsonObject.getString("error"), 0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, null, new RequestData(URLConfig.UNFOLLOW_USER, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        });
    }

    private void toggleFollowUnfollow(ItemViewHolder holder) {
        holder.follow.setVisibility(holder.follow.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        holder.unfollow.setVisibility(holder.unfollow.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

}