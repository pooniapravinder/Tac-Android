package com.wookes.tac.exo;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.wookes.tac.R;
import com.wookes.tac.model.FeedModel;
import com.wookes.tac.ui.MusicUi;
import com.wookes.tac.util.CustomCounter;
import com.wookes.tac.util.GlideApp;

public class PlayerViewHolder extends RecyclerView.ViewHolder {
    FrameLayout mediaContainer, buffering;
    LinearLayout upperLayout, reactContainer;
    ImageView mediaCoverImage, playbackControl, like, comment, share, profilePic, musicIcon, fullscreen, fullscreenExit, verified;
    TextView likes, comments;
    Button follow, unfollow;
    TextView title, userHandle, musicName;
    private final View parent, musicContainer;

    public PlayerViewHolder(@NonNull View itemView) {
        super(itemView);
        parent = itemView;
        mediaContainer = itemView.findViewById(R.id.mediaContainer);
        mediaContainer.setClipToOutline(true);
        musicIcon = itemView.findViewById(R.id.music_icon);
        verified = itemView.findViewById(R.id.verified);
        fullscreen = itemView.findViewById(R.id.fullscreen);
        fullscreenExit = itemView.findViewById(R.id.fullscreen_exit);
        buffering = itemView.findViewById(R.id.buffering);
        like = itemView.findViewById(R.id.like);
        likes = itemView.findViewById(R.id.no_of_likes);
        follow = itemView.findViewById(R.id.follow);
        unfollow = itemView.findViewById(R.id.unfollow);
        comment = itemView.findViewById(R.id.comment);
        share = itemView.findViewById(R.id.share);
        comments = itemView.findViewById(R.id.no_of_comments);
        upperLayout = itemView.findViewById(R.id.upperLayout);
        reactContainer = itemView.findViewById(R.id.react_container);
        profilePic = itemView.findViewById(R.id.profile_pic);
        mediaCoverImage = itemView.findViewById(R.id.ivMediaCoverImage);
        title = itemView.findViewById(R.id.tvTitle);
        userHandle = itemView.findViewById(R.id.tvUserHandle);
        musicName = itemView.findViewById(R.id.music_name);
        musicContainer = itemView.findViewById(R.id.music_container);
        playbackControl = itemView.findViewById(R.id.ivPlaybackControl);
    }

    public void onBind(int bottomInset, FeedModel feedModel, Context context, Drawable placeholder) {
        parent.setTag(this);
        if (feedModel.getPost().getHeight() >= 960) {
            mediaCoverImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            mediaCoverImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        playbackControl.setVisibility(View.GONE);
        musicName.setText(context.getString(R.string.original_sound, feedModel.getMusic().getUsername(), feedModel.getMusic().getFullName()));
        musicContainer.setOnClickListener(view -> {
            Intent intent = new Intent(context, MusicUi.class);
            intent.putExtra("data", feedModel.getMusic());
            intent.putExtra("duration", feedModel.getPost().getDuration());
            context.startActivity(intent);
        });
        title.setText(feedModel.getPost().getCaption());
        title.setVisibility(TextUtils.isEmpty(feedModel.getPost().getCaption()) ? View.GONE : View.VISIBLE);
        upperLayout.setPadding(upperLayout.getLeft(), upperLayout.getPaddingTop(), upperLayout.getPaddingRight(), bottomInset);
        userHandle.setText(feedModel.getUser().getUserName());
        if (feedModel.getPost().isLiked()) {
            like.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_video_like_enable));
        } else {
            like.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_video_like_nor));
        }
        verified.setVisibility(feedModel.getUser().isVerified() ? View.VISIBLE : View.GONE);
        likes.setText(CustomCounter.format(feedModel.getPost().getLikes()));
        comments.setText(CustomCounter.format(feedModel.getPost().getComments()));
        GlideApp.loadProfilePic(context, feedModel.getUser().getProfilePic(), profilePic);
        GlideApp.loadCoverImage(context, feedModel.getPost().getThumbnail(), mediaCoverImage, placeholder);
    }

}