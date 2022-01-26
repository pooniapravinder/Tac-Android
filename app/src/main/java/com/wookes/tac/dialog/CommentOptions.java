package com.wookes.tac.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.wookes.tac.R;
import com.wookes.tac.interfaces.a;
import com.wookes.tac.model.CommentsDialogModel;
import com.wookes.tac.model.CommentsRepliesDialogModel;
import com.wookes.tac.util.SelfUser;

import java.util.ArrayList;
import java.util.Objects;

public class CommentOptions extends Dialog implements View.OnClickListener {

    public Context context;
    private final a a;
    private final Object model;
    private final Object adapter;
    private final int pos;

    public CommentOptions(Context context, a a, Object model, Object adapter, int pos) {
        super(context);
        this.context = context;
        this.a = a;
        this.model = model;
        this.adapter = adapter;
        this.pos = pos;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout_comment_options);
        Objects.requireNonNull(getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        findViewById(R.id.copy).setOnClickListener(this);
        findViewById(R.id.report).setOnClickListener(this);
        SelfUser selfUser = new SelfUser(context);
        long userId = 0;

        if (model instanceof ArrayList<?>) {
            if (((ArrayList<?>) model).get(0) instanceof CommentsDialogModel) {
                userId = ((ArrayList<CommentsDialogModel>) model).get(pos).getUserId();
            } else {
                userId = ((ArrayList<CommentsRepliesDialogModel>) model).get(pos).getUserId();
            }
        }
        if (selfUser.getSelfUser() == null || selfUser.getSelfUser().getUserId() != userId) {
            findViewById(R.id.delete).setVisibility(View.GONE);
        } else {
            findViewById(R.id.report).setVisibility(View.GONE);
        }
        findViewById(R.id.delete).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.copy:
                a.copyComment(model, pos);
                break;
            case R.id.report:
                a.reportComment();
                break;
            case R.id.delete:
                a.deleteComment(model, adapter, pos);
                break;
        }
        dismiss();
    }
}
