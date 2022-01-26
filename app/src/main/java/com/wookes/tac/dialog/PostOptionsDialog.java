
package com.wookes.tac.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.wookes.tac.R;
import com.wookes.tac.interfaces.dd;
import com.wookes.tac.model.FeedModel;
import com.wookes.tac.util.SelfUser;

import java.util.Objects;

public class PostOptionsDialog extends Dialog implements View.OnClickListener {

    public Context context;
    private final dd dd;
    private final FeedModel feedModel;

    public PostOptionsDialog(Context context, dd dd, FeedModel feedModel) {
        super(context);
        this.context = context;
        this.dd = dd;
        this.feedModel = feedModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout_post_options);
        SelfUser selfUser = new SelfUser(context);
        Objects.requireNonNull(getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (selfUser.getSelfUser() == null || selfUser.getSelfUser().getUserId() != feedModel.getUser().getId()) {
            findViewById(R.id.delete).setVisibility(View.GONE);
            findViewById(R.id.turn_comments).setVisibility(View.GONE);
            findViewById(R.id.turn_downloads).setVisibility(View.GONE);
        } else {
            findViewById(R.id.report).setVisibility(View.GONE);
        }
        TextView save = findViewById(R.id.save);
        TextView turnComments = findViewById(R.id.turn_comments);
        TextView turnDownloads = findViewById(R.id.turn_downloads);
        save.setText(feedModel.getPost().isSaved() ? R.string.remove_from_favorites : R.string.add_to_favorites);
        turnComments.setText(feedModel.getPost().isAllowComments() ? R.string.turn_off_comments : R.string.turn_on_comments);
        turnDownloads.setText(feedModel.getPost().isAllowDownloads() ? R.string.turn_off_downloads : R.string.turn_on_downloads);
        save.setOnClickListener(this);
        findViewById(R.id.delete).setOnClickListener(this);
        findViewById(R.id.report).setOnClickListener(this);
        turnComments.setOnClickListener(this);
        turnDownloads.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                dd.onSave();
                break;
            case R.id.delete:
                dd.onDelete();
                break;
            case R.id.report:
                dd.onReport();
                break;
            case R.id.turn_comments:
                dd.onTurnComments();
                break;
            case R.id.turn_downloads:
                dd.onTurnDownloads();
                break;
        }
        dismiss();
    }
}
