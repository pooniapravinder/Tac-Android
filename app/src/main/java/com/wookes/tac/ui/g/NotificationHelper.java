package com.wookes.tac.ui.g;

public class NotificationHelper {
    public static String getNotificationText(byte type) {
        String text = "";
        switch (type) {
            case 1:
                text = "started following you.";
                break;
            case 2:
                text = "liked your post.";
                break;
            case 3:
                text = "commented on your post.";
                break;
            case 4:
                text = "liked your comment: ";
                break;
            case 5:
                text = "mentioned you in a comment.";
                break;
            case 6:
                text = "mentioned you in a post.";
                break;
        }
        return text;
    }
}
