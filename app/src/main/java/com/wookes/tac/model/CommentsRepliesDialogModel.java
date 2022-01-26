package com.wookes.tac.model;

import com.google.gson.annotations.SerializedName;

public class CommentsRepliesDialogModel {

    @SerializedName("replyId")
    private long replyId;
    @SerializedName("profilePic")
    private String profilePic;
    @SerializedName("userId")
    private long userId;
    @SerializedName("userName")
    private String userName;
    @SerializedName("comment")
    private String comment;
    @SerializedName("likes")
    private long likes;
    @SerializedName("commentId")
    private long commentId;
    @SerializedName("time")
    private long time;
    @SerializedName("liked")
    private boolean liked;
    private boolean isPosting;
    private long identifier;

    public CommentsRepliesDialogModel(long replyId, String profilePic, long userId, String userName, String comment, long likes, long commentId, long time, boolean liked) {
        this.replyId = replyId;
        this.profilePic = profilePic;
        this.userId = userId;
        this.userName = userName;
        this.comment = comment;
        this.likes = likes;
        this.commentId = commentId;
        this.time = time;
        this.liked = liked;
    }

    public long getReplyId() {
        return replyId;
    }

    public void setReplyId(long replyId) {
        this.replyId = replyId;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean isPosting() {
        return isPosting;
    }

    public void setPosting(boolean posting) {
        isPosting = posting;
    }

    public long getIdentifier() {
        return identifier;
    }

    public void setIdentifier(long identifier) {
        this.identifier = identifier;
    }
}
