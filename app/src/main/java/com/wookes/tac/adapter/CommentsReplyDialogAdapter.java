package com.wookes.tac.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wookes.tac.R;
import com.wookes.tac.custom.WookesTextView;
import com.wookes.tac.model.CommentsRepliesDialogModel;
import com.wookes.tac.request.AsyncRequest;
import com.wookes.tac.request.RequestData;
import com.wookes.tac.ui.UserProfile;
import com.wookes.tac.urlconfig.URLConfig;
import com.wookes.tac.util.CustomCounter;
import com.wookes.tac.util.GlideApp;
import com.wookes.tac.util.TimeUtils;
import com.wookes.tac.util.ToastDisplay;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentsReplyDialogAdapter extends RecyclerView.Adapter<CommentsReplyDialogAdapter.viewHolder> {

    private final Context context;
    private final ArrayList<CommentsRepliesDialogModel> commentsRepliesDialogModels;
    private OnItemClickListener onItemClickListener;

    public CommentsReplyDialogAdapter(Context context, ArrayList<CommentsRepliesDialogModel> commentsRepliesDialogModels) {
        this.context = context;
        this.commentsRepliesDialogModels = commentsRepliesDialogModels;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.inflate_layout_comments_reply, viewGroup, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(final viewHolder holder, final int i) {
        GlideApp.loadProfilePic(context, commentsRepliesDialogModels.get(i).getProfilePic(), holder.profilePic);
        holder.username.setText(commentsRepliesDialogModels.get(i).getUserName());
        holder.username.setOnClickListener(view -> startProfile(commentsRepliesDialogModels.get(i).getUserId()));
        holder.profilePic.setOnClickListener(view -> startProfile(commentsRepliesDialogModels.get(i).getUserId()));
        holder.comment.setText(commentsRepliesDialogModels.get(i).getComment());
        if (commentsRepliesDialogModels.get(i).isPosting()) {
            holder.time.setText(context.getResources().getString(R.string.posting));
        } else {
            holder.time.setText(TimeUtils.getTimeAgo(commentsRepliesDialogModels.get(i).getTime()));
        }
        if (commentsRepliesDialogModels.get(i).getLikes() > 0)
            holder.likes.setText(CustomCounter.format(commentsRepliesDialogModels.get(i).getLikes()));
        toggleLikes(holder.likes, commentsRepliesDialogModels.get(i).isLiked());
        if (commentsRepliesDialogModels.get(i).getLikes() > 0)
            holder.likes.setText(CustomCounter.format(commentsRepliesDialogModels.get(i).getLikes()));
    }

    @Override
    public int getItemCount() {
        return commentsRepliesDialogModels.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {
        private final ImageView profilePic;
        private final TextView username;
        private final WookesTextView comment;
        private final TextView time;
        private final TextView likes;

        viewHolder(View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profile_pic);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
            time = itemView.findViewById(R.id.time);
            likes = itemView.findViewById(R.id.likes);
            itemView.setOnLongClickListener(view -> {
                onItemClickListener.onItemLongClick(getAdapterPosition(), view);
                return false;
            });
            comment.setOnLongClickListener(view -> {
                onItemClickListener.onItemLongClick(getAdapterPosition(), view);
                return false;
            });
            itemView.setOnClickListener(view -> onItemClickListener.onItemClick(getAdapterPosition(), view));
            comment.setOnClickListener(view -> onItemClickListener.onItemClick(getAdapterPosition(), view));
            likes.setOnClickListener(view -> {
                commentsRepliesDialogModels.get(getAdapterPosition()).setLiked(!commentsRepliesDialogModels.get(getAdapterPosition()).isLiked());
                commentsRepliesDialogModels.get(getAdapterPosition()).setLikes(commentsRepliesDialogModels.get(getAdapterPosition()).isLiked() ? commentsRepliesDialogModels.get(getAdapterPosition()).getLikes() + 1 : commentsRepliesDialogModels.get(getAdapterPosition()).getLikes() - 1);
                toggleLikes(likes, commentsRepliesDialogModels.get(getAdapterPosition()).isLiked());
                likes.setText(String.valueOf(commentsRepliesDialogModels.get(getAdapterPosition()).getLikes()));
                Map<String, String> map = new HashMap<>();
                map.put("reply_id", String.valueOf(commentsRepliesDialogModels.get(getAdapterPosition()).getReplyId()));
                new AsyncRequest(context, output -> {
                    try {
                        JSONObject root = new JSONObject(output);
                        if (!root.has("success")) {
                            commentsRepliesDialogModels.get(getAdapterPosition()).setLiked(!commentsRepliesDialogModels.get(getAdapterPosition()).isLiked());
                            commentsRepliesDialogModels.get(getAdapterPosition()).setLikes(commentsRepliesDialogModels.get(getAdapterPosition()).isLiked() ? commentsRepliesDialogModels.get(getAdapterPosition()).getLikes() + 1 : commentsRepliesDialogModels.get(getAdapterPosition()).getLikes() - 1);
                            toggleLikes(likes, commentsRepliesDialogModels.get(getAdapterPosition()).isLiked());
                            likes.setText(String.valueOf(commentsRepliesDialogModels.get(getAdapterPosition()).getLikes()));
                            ToastDisplay.a(context, root.getString("error"), 0);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, null, new RequestData(URLConfig.LIKE_COMMENT_REPLY_URL, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int pos, View v);

        void onItemLongClick(int pos, View v);
    }

    private void startProfile(long userId) {
        Intent intent = new Intent(context, UserProfile.class);
        intent.putExtra("user", String.valueOf(userId));
        intent.putExtra("user_type", 0);
        context.startActivity(intent);
    }

    private void toggleLikes(TextView textView, boolean isLiked) {
        int color = isLiked ? context.getResources().getColor(R.color.heart_red) : context.getResources().getColor(R.color.fadeGray);
        textView.setTextColor(color);
        int size = (int) context.getResources().getDimension(R.dimen.margin_18);
        Drawable drawable = context.getDrawable(isLiked ? R.drawable.icon_video_like_enable : R.drawable.icon_comment_like_nor);
        drawable.setBounds(0, 0, size, size);
        textView.setCompoundDrawables(null, drawable, null, null);
        textView.getCompoundDrawables()[1].setTint(color);
    }
}