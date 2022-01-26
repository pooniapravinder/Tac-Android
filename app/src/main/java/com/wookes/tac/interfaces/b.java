package com.wookes.tac.interfaces;

import androidx.recyclerview.widget.RecyclerView;


public interface b {

    void commentReply(int commentPos, String username, String comment, RecyclerView commentReplyRecyclerView);

    void onCommentCountChange(boolean isIncreased, long affected);
}
