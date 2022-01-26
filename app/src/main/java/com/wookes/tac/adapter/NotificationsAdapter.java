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
import com.wookes.tac.model.NotificationsModel;
import com.wookes.tac.request.AsyncRequest;
import com.wookes.tac.request.RequestData;
import com.wookes.tac.ui.UserProfile;
import com.wookes.tac.ui.g.NotificationText;
import com.wookes.tac.urlconfig.URLConfig;
import com.wookes.tac.util.GlideApp;
import com.wookes.tac.util.ToastDisplay;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final ArrayList<NotificationsModel> notificationsModels;
    private final int VIEW_TYPE_ITEM = 1;
    private final int VIEW_TYPE_LOADING = 2;
    private final NotificationText notificationText;
    private final HashMap<String, String> map = new HashMap<>();

    public NotificationsAdapter(Context context, ArrayList<NotificationsModel> notificationsModels) {
        this.context = context;
        this.notificationsModels = notificationsModels;
        this.notificationText = new NotificationText(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_layout_notification_items, viewGroup, false);
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
        if (viewHolder instanceof LoadingViewHolder) {
            populateLoader((LoadingViewHolder) viewHolder, i);
        } else if (viewHolder instanceof ItemViewHolder) {
            populateItemRows((ItemViewHolder) viewHolder, i);
        }
    }

    @Override
    public int getItemCount() {
        return notificationsModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (notificationsModels.size() > 0 && notificationsModels.get(position) == null) {
            return VIEW_TYPE_LOADING;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView profilePic;
        private final ImageView contentImage;
        private final Button follow;
        private final Button unfollow;
        private final TextView text;
        private final TextView notificationTime;

        ItemViewHolder(View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profile_pic);
            text = itemView.findViewById(R.id.text);
            follow = itemView.findViewById(R.id.follow);
            unfollow = itemView.findViewById(R.id.unfollow);
            notificationTime = itemView.findViewById(R.id.notification_time);
            contentImage = itemView.findViewById(R.id.content_image);
        }
    }

    private void populateItemRows(ItemViewHolder holder, int i) {
        GlideApp.loadProfilePic(context, notificationsModels.get(i).getUser().getProfilePic() != null ? notificationsModels.get(i).getUser().getProfilePic().split(",")[0] : null, holder.profilePic);
        holder.profilePic.setOnClickListener(view -> {
            Intent intent = new Intent(context, UserProfile.class);
            intent.putExtra("user", (notificationsModels.get(i).getUser().getUserName().split(",")[0]).replace("@", ""));
            intent.putExtra("user_type", 1);
            context.startActivity(intent);
        });
        notificationText.setNotificationText(holder.text, notificationsModels.get(i).getUser().getUserName().split(","), null, notificationsModels.get(i).getType(), notificationsModels.get(i).getTimestamp());
        if (notificationsModels.get(i).getType() == 1) {
            holder.unfollow.setVisibility(notificationsModels.get(i).getIsFollowing() ? View.VISIBLE : View.GONE);
            holder.follow.setVisibility(notificationsModels.get(i).getIsFollowing() ? View.GONE : View.VISIBLE);
        } else {
            holder.follow.setVisibility(View.GONE);
            holder.unfollow.setVisibility(View.GONE);
        }
        if (notificationsModels.get(i).getMedia() != null) {
            GlideApp.loadSimple(context, notificationsModels.get(i).getMedia().getThumbnail(), holder.contentImage);
            holder.contentImage.setVisibility(View.VISIBLE);
        } else {
            holder.contentImage.setVisibility(View.GONE);
        }
        if (i == 0 || !notificationText.getNotificationTime(notificationsModels.get(i).getTimestamp()).equals(notificationText.getNotificationTime(notificationsModels.get(i - 1).getTimestamp()))) {
            holder.notificationTime.setVisibility(View.VISIBLE);
            holder.notificationTime.setText(notificationText.getNotificationTime(notificationsModels.get(i).getTimestamp()));
        } else {
            holder.notificationTime.setVisibility(View.GONE);
        }
        holder.follow.setOnClickListener(view -> {
            map.put("user", String.valueOf(notificationsModels.get(i).getUser().getId()));
            toggleFollowUnfollow(holder, true, notificationsModels.get(i).getUser().getId());
            new AsyncRequest(context, output -> {
                try {
                    JSONObject jsonObject = new JSONObject(output);
                    if (!jsonObject.has("success")) {
                        toggleFollowUnfollow(holder, false, notificationsModels.get(i).getUser().getId());
                        ToastDisplay.a(context, jsonObject.getString("error"), 0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, null, new RequestData(URLConfig.FOLLOW_USER, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        });
        holder.unfollow.setOnClickListener(view -> {
            map.put("user", String.valueOf(notificationsModels.get(i).getUser().getId()));
            toggleFollowUnfollow(holder, false, notificationsModels.get(i).getUser().getId());
            new AsyncRequest(context, output -> {
                try {
                    JSONObject jsonObject = new JSONObject(output);
                    if (!jsonObject.has("success")) {
                        toggleFollowUnfollow(holder, true, notificationsModels.get(i).getUser().getId());
                        ToastDisplay.a(context, jsonObject.getString("error"), 0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, null, new RequestData(URLConfig.UNFOLLOW_USER, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        });
    }

    private static class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }

    private void populateLoader(LoadingViewHolder viewHolder, int position) {
    }

    private void toggleFollowUnfollow(ItemViewHolder holder, boolean isFollow, long userId) {
        for (NotificationsModel notificationsModel : notificationsModels) {
            if (notificationsModel.getUser().getId() == userId) {
                notificationsModel.setIsFollowing(isFollow);
            }
        }
        holder.follow.setVisibility(holder.follow.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        holder.unfollow.setVisibility(holder.unfollow.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

}