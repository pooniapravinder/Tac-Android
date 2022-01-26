package com.wookes.tac.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CommentsDialogModel {
    @SerializedName("commentId")
    private long commentId;
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
    @SerializedName("replies")
    private long replies;
    @SerializedName("postId")
    private long postId;
    @SerializedName("time")
    private long time;
    @SerializedName("liked")
    private boolean liked;
    @SerializedName("commentsReplies")
    private ArrayList<CommentsRepliesDialogModel> commentsRepliesDialogModel;
    private boolean isPosting;
    private long identifier;

    public CommentsDialogModel(long commentId, String profilePic, long userId, String userName, String comment, long likes, long replies, long postId, long time, boolean liked, ArrayList<CommentsRepliesDialogModel> commentsRepliesDialogModel) {
        this.commentId = commentId;
        this.profilePic = profilePic;
        this.userId = userId;
        this.userName = userName;
        this.comment = comment;
        this.likes = likes;
        this.replies = replies;
        this.postId = postId;
        this.time = time;
        this.liked = liked;
        this.commentsRepliesDialogModel = commentsRepliesDialogModel;
    }

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
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

    public long getReplies() {
        return replies;
    }

    public void setReplies(long replies) {
        this.replies = replies;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
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

    public ArrayList<CommentsRepliesDialogModel> getCommentsRepliesDialogModel() {
        return commentsRepliesDialogModel;
    }

    public void setCommentsRepliesDialogModel(ArrayList<CommentsRepliesDialogModel> commentsRepliesDialogModel) {
        this.commentsRepliesDialogModel = commentsRepliesDialogModel;
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
