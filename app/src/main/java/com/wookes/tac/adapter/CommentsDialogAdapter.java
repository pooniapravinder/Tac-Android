package com.wookes.tac.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wookes.tac.R;
import com.wookes.tac.custom.WookesTextView;
import com.wookes.tac.dialog.CommentOptions;
import com.wookes.tac.interfaces.a;
import com.wookes.tac.interfaces.b;
import com.wookes.tac.model.CommentsDialogModel;
import com.wookes.tac.model.CommentsRepliesDialogModel;
import com.wookes.tac.request.AsyncRequest;
import com.wookes.tac.request.RequestData;
import com.wookes.tac.ui.UserProfile;
import com.wookes.tac.urlconfig.URLConfig;
import com.wookes.tac.util.CustomCounter;
import com.wookes.tac.util.GlideApp;
import com.wookes.tac.util.TimeUtils;
import com.wookes.tac.util.ToastDisplay;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentsDialogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements a {

    private final Context context;
    private final ArrayList<CommentsDialogModel> commentsDialogModels;
    private final b b;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public CommentsDialogAdapter(Context context, ArrayList<CommentsDialogModel> commentsDialogModels, b b) {
        this.context = context;
        this.commentsDialogModels = commentsDialogModels;
        this.b = b;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_layout_comments, viewGroup, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_loading, viewGroup, false);
            return new LoadingViewHolder(view);
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


    private void onClickCommon(final ItemViewHolder holder, final int i) {
        if (holder.recyclerView.getAdapter() == null || commentsDialogModels.get(i).isPosting())
            return;
        b.commentReply(i, commentsDialogModels.get(i).getUserName(), commentsDialogModels.get(i).getComment(), holder.recyclerView);
    }

    @Override
    public int getItemCount() {
        return commentsDialogModels == null ? 0 : commentsDialogModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return commentsDialogModels.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public void copyComment(Object model, final int pos) {
        try {
            if (model instanceof ArrayList<?>) {
                String data;
                if (((ArrayList<?>) model).get(0) instanceof CommentsDialogModel) {
                    data = ((ArrayList<CommentsDialogModel>) model).get(pos).getComment();
                } else {
                    data = ((ArrayList<CommentsRepliesDialogModel>) model).get(pos).getComment();
                }
                ClipboardManager clipMan = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                clipMan.setPrimaryClip(ClipData.newPlainText("label", data));
                ToastDisplay.a(context, context.getResources().getString((R.string.comment_copied)), 0);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reportComment() {

    }

    @SuppressWarnings({"unchecked"})
    @Override
    public void deleteComment(Object model, final Object adapter, final int pos) {
        Map<String, String> map = new HashMap<>();
        try {
            if (model instanceof ArrayList<?>) {
                if (((ArrayList<?>) model).get(0) instanceof CommentsDialogModel) {
                    final ArrayList<CommentsDialogModel> commentsDialogModels = ((ArrayList<CommentsDialogModel>) model);
                    map.put("comment_id", String.valueOf(commentsDialogModels.get(pos).getCommentId()));
                    networkRequest(map, URLConfig.DELETE_COMMENTS_URL, isSuccess -> {
                        if (!isSuccess) return;
                        b.onCommentCountChange(false, commentsDialogModels.get(pos).getReplies() + 1);
                        CommentsDialogAdapter commentsDialogAdapter = (CommentsDialogAdapter) adapter;
                        commentsDialogModels.remove(pos);
                        commentsDialogAdapter.notifyItemRemoved(pos);
                        commentsDialogAdapter.notifyItemRangeChanged(pos, commentsDialogModels.size());
                    });
                } else {
                    final ArrayList<CommentsRepliesDialogModel> commentsRepliesDialogModels = ((ArrayList<CommentsRepliesDialogModel>) model);
                    map.put("reply_id", String.valueOf(commentsRepliesDialogModels.get(pos).getReplyId()));
                    networkRequest(map, URLConfig.DELETE_COMMENTS_REPLY_URL, isSuccess -> {
                        if (!isSuccess) return;
                        b.onCommentCountChange(false, 1);
                        CommentsReplyDialogAdapter commentsReplyDialogAdapter = (CommentsReplyDialogAdapter) adapter;
                        commentsRepliesDialogModels.remove(pos);
                        commentsReplyDialogAdapter.notifyItemRemoved(pos);
                        commentsReplyDialogAdapter.notifyItemRangeChanged(pos, commentsDialogModels.size());
                    });
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView profilePic;
        private final TextView username, time, likes, replies;
        private final WookesTextView comment;
        private final RecyclerView recyclerView;

        ItemViewHolder(View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profile_pic);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
            time = itemView.findViewById(R.id.time);
            likes = itemView.findViewById(R.id.likes);
            replies = itemView.findViewById(R.id.replies);
            recyclerView = itemView.findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
            itemView.setOnLongClickListener(view -> {
                if (commentsDialogModels.get(getAdapterPosition()).isPosting()) return false;
                new CommentOptions(context, CommentsDialogAdapter.this, commentsDialogModels, CommentsDialogAdapter.this, getAdapterPosition()).show();
                return false;
            });
            comment.setOnLongClickListener(view -> {
                if (commentsDialogModels.get(getAdapterPosition()).isPosting()) return false;
                new CommentOptions(context, CommentsDialogAdapter.this, commentsDialogModels, CommentsDialogAdapter.this, getAdapterPosition()).show();
                return false;
            });
            likes.setOnClickListener(view -> {
                commentsDialogModels.get(getAdapterPosition()).setLiked(!commentsDialogModels.get(getAdapterPosition()).isLiked());
                commentsDialogModels.get(getAdapterPosition()).setLikes(commentsDialogModels.get(getAdapterPosition()).isLiked() ? commentsDialogModels.get(getAdapterPosition()).getLikes() + 1 : commentsDialogModels.get(getAdapterPosition()).getLikes() - 1);
                toggleLikes(likes, commentsDialogModels.get(getAdapterPosition()).isLiked());
                likes.setText(String.valueOf(commentsDialogModels.get(getAdapterPosition()).getLikes()));
                Map<String, String> map = new HashMap<>();
                map.put("comment_id", String.valueOf(commentsDialogModels.get(getAdapterPosition()).getCommentId()));
                new AsyncRequest(context, output -> {
                    try {
                        JSONObject root = new JSONObject(output);
                        if (!root.has("success")) {
                            commentsDialogModels.get(getAdapterPosition()).setLiked(!commentsDialogModels.get(getAdapterPosition()).isLiked());
                            commentsDialogModels.get(getAdapterPosition()).setLikes(commentsDialogModels.get(getAdapterPosition()).isLiked() ? commentsDialogModels.get(getAdapterPosition()).getLikes() + 1 : commentsDialogModels.get(getAdapterPosition()).getLikes() - 1);
                            toggleLikes(likes, commentsDialogModels.get(getAdapterPosition()).isLiked());
                            likes.setText(String.valueOf(commentsDialogModels.get(getAdapterPosition()).getLikes()));
                            ToastDisplay.a(context, root.getString("error"), 0);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, null, new RequestData(URLConfig.LIKE_COMMENT_URL, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            });
        }
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

    private void populateItemRows(ItemViewHolder holder, int i) {
        GlideApp.loadProfilePic(context, commentsDialogModels.get(i).getProfilePic(), holder.profilePic);
        holder.username.setText(commentsDialogModels.get(i).getUserName());
        holder.username.setOnClickListener(view -> startProfile(commentsDialogModels.get(i).getUserId()));
        holder.profilePic.setOnClickListener(view -> startProfile(commentsDialogModels.get(i).getUserId()));
        holder.comment.setText(commentsDialogModels.get(i).getComment());
        toggleLikes(holder.likes, commentsDialogModels.get(i).isLiked());
        if (commentsDialogModels.get(i).isPosting()) {
            holder.time.setText(context.getResources().getString(R.string.posting));
        } else {
            holder.time.setText(TimeUtils.getTimeAgo(commentsDialogModels.get(i).getTime()));
        }
        if (commentsDialogModels.get(i).getLikes() > 0)
            holder.likes.setText(CustomCounter.format(commentsDialogModels.get(i).getLikes()));
        CommentsReplyDialogAdapter commentsReplyDialogAdapter = new CommentsReplyDialogAdapter(context, commentsDialogModels.get(i).getCommentsRepliesDialogModel());
        holder.recyclerView.setAdapter(commentsReplyDialogAdapter);
        holder.recyclerView.setNestedScrollingEnabled(false);
        commentsReplyDialogAdapter.setOnItemClickListener(new CommentsReplyDialogAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos, View v) {
                if (commentsDialogModels.get(i).getCommentsRepliesDialogModel().get(pos).isPosting())
                    return;
                b.commentReply(i, commentsDialogModels.get(i).getCommentsRepliesDialogModel().get(pos).getUserName(), commentsDialogModels.get(i).getCommentsRepliesDialogModel().get(pos).getComment(), holder.recyclerView);
            }

            @Override
            public void onItemLongClick(int pos, View v) {
                if (commentsDialogModels.get(i).getCommentsRepliesDialogModel().get(pos).isPosting())
                    return;
                new CommentOptions(context, CommentsDialogAdapter.this, commentsDialogModels.get(i).getCommentsRepliesDialogModel(), holder.recyclerView.getAdapter(), pos).show();
            }
        });
        if (commentsDialogModels.get(i).getReplies() > 0) {
            holder.replies.setVisibility(View.VISIBLE);
            holder.replies.setText(String.format(context.getResources().getString(R.string.view_replies), CustomCounter.format(commentsDialogModels.get(i).getReplies())));
            holder.replies.setOnClickListener(view -> {
                if (commentsDialogModels.get(i).getReplies() <= commentsDialogModels.get(i).getCommentsRepliesDialogModel().size()) {
                    if (holder.recyclerView.getVisibility() == View.VISIBLE) {
                        holder.recyclerView.setVisibility(View.GONE);
                        compoundDrawable(holder.replies, R.drawable.icon_comment_replies_arrow_down);
                        holder.replies.setText(String.format(context.getResources().getString(R.string.view_replies), CustomCounter.format(commentsDialogModels.get(i).getReplies())));
                    } else {
                        holder.recyclerView.setVisibility(View.VISIBLE);
                        compoundDrawable(holder.replies, R.drawable.icon_comment_replies_arrow_down);
                        holder.replies.setText(context.getResources().getString(R.string.hide_replies));
                    }
                    return;
                }
                compoundDrawable(holder.replies, -1);
                holder.replies.setText(R.string.loading_dialog);
                if (holder.recyclerView.getAdapter() == null) return;
                Map<String, String> map = new HashMap<>();
                map.put("comment_id", String.valueOf(commentsDialogModels.get(i).getCommentId()));
                map.put("pagination", String.valueOf(commentsDialogModels.get(i).getCommentsRepliesDialogModel().size()));
                new AsyncRequest(context, output -> {
                    try {
                        JSONObject jsonObject = new JSONObject(output);
                        Type type = new TypeToken<ArrayList<CommentsRepliesDialogModel>>() {
                        }.getType();
                        int prevSize = holder.recyclerView.getAdapter().getItemCount();
                        commentsDialogModels.get(i).getCommentsRepliesDialogModel().addAll(new Gson().fromJson(jsonObject.getString("data"), type));
                        holder.recyclerView.getAdapter().notifyItemRangeInserted(prevSize, commentsDialogModels.get(i).getCommentsRepliesDialogModel().size() - prevSize);
                        if (commentsDialogModels.get(i).getReplies() <= commentsDialogModels.get(i).getCommentsRepliesDialogModel().size()) {
                            compoundDrawable(holder.replies, R.drawable.icon_comment_replies_arrow_down);
                            holder.replies.setText(context.getResources().getString(R.string.hide_replies));
                        } else {
                            compoundDrawable(holder.replies, R.drawable.icon_comment_replies_arrow_down);
                            holder.replies.setText(String.format(context.getResources().getString(R.string.view_replies), CustomCounter.format(commentsDialogModels.get(i).getReplies() - commentsDialogModels.get(i).getCommentsRepliesDialogModel().size())));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, null, new RequestData(URLConfig.RETRIEVE_COMMENTS_REPLY_URL, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            });
        } else {
            holder.replies.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(view -> onClickCommon(holder, i));
        holder.comment.setOnClickListener(view -> onClickCommon(holder, i));
    }

    private void networkRequest(Map<String, String> map, String url, final Callback callback) {
        new AsyncRequest(context, output -> {
            try {
                JSONObject root = new JSONObject(output);
                if (!root.has("success")) {
                    ToastDisplay.a(context, root.getString("error"), 0);
                    return;
                }
                callback.call(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, null, new RequestData(url, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        callback.call(false);
    }

    public interface Callback {
        void call(boolean isSuccess);
    }

    private void compoundDrawable(TextView textView, int drawable) {
        if (drawable == -1) {
            textView.setCompoundDrawables(null, null, null, null);
            return;
        }
        Drawable drawable2 = context.getDrawable(drawable);
        drawable2.setBounds(0, 0, 50, 50);
        textView.setCompoundDrawables(null, null, drawable2, null);
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