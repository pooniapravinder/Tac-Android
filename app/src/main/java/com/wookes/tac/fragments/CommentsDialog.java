package com.wookes.tac.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wookes.tac.R;
import com.wookes.tac.adapter.CommentsDialogAdapter;
import com.wookes.tac.adapter.MentionTagAdapter;
import com.wookes.tac.interfaces.DialogHandler;
import com.wookes.tac.interfaces.b;
import com.wookes.tac.model.CommentsDialogModel;
import com.wookes.tac.model.CommentsRepliesDialogModel;
import com.wookes.tac.model.FeedModel;
import com.wookes.tac.model.ProfileModel;
import com.wookes.tac.request.AsyncRequest;
import com.wookes.tac.request.RequestData;
import com.wookes.tac.request.ResponseResult;
import com.wookes.tac.urlconfig.URLConfig;
import com.wookes.tac.util.Animations;
import com.wookes.tac.util.CustomCounter;
import com.wookes.tac.util.KeyboardHeightProvider;
import com.wookes.tac.util.SelfUser;
import com.wookes.tac.util.ToastDisplay;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class CommentsDialog extends BottomSheetDialogFragment implements ResponseResult, b, KeyboardHeightProvider.KeyboardHeightObserver {
    private DialogHandler dialogHandler;
    public static final String TAG = "CommentsDialog";
    private RecyclerView recyclerView;
    private TextView noOfComments, replyTo, commentToReply, commentsInfo;
    private Context context;
    private AutoCompleteTextView commentText;
    private ImageView postComment, closeReply, closeDialog;
    private int commentPos;
    private boolean isReply = false;
    private long replyComment = 0;
    private ArrayList<CommentsRepliesDialogModel> commentsRepliesDialogModels;
    private final ArrayList<CommentsDialogModel> commentsDialogModels = new ArrayList<>();
    private CommentsDialogAdapter commentsDialogAdapter;
    private RecyclerView commentReplyRecyclerView;
    private LinearLayout replyLayout;
    private boolean isLoading = true, hasMore = true;
    private KeyboardHeightProvider keyboardHeightProvider;
    private ViewGroup relativeView;
    private float initialY, intialMargin;
    private int initialheightOfEditText;
    private boolean wasNegative;
    private int negativePixelHeight;
    private FeedModel feedModel;

    public static CommentsDialog newInstance() {
        return new CommentsDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_layout_comments, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialog1 -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialog1;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
                BottomSheetBehavior.from(bottomSheet).setPeekHeight(displayMetrics.heightPixels);
            }
        });
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle == null) return;
        feedModel = (FeedModel) bundle.getSerializable("feedModel");
        initView(view);
        getDialog().getWindow().setDimAmount(0);
        View rootView = view.findViewById(R.id.root_view);
        keyboardHeightProvider = new KeyboardHeightProvider(getActivity(), rootView);
        relativeView.post(() -> {
            initialY = relativeView.getY();
            intialMargin = ((ViewGroup.MarginLayoutParams) relativeView.getLayoutParams()).bottomMargin;
            initialheightOfEditText = view.findViewById(R.id.comment_text).getHeight();
        });
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (displayMetrics.heightPixels * .66));
        view.findViewById(R.id.content).setLayoutParams(params);
        rootView.post(() -> keyboardHeightProvider.start());
        if (!feedModel.getPost().isAllowComments()) {
            commentsInfo.setVisibility(View.VISIBLE);
            commentsInfo.setText(R.string.comments_disabled);
            relativeView.setVisibility(View.GONE);
        }
        if (feedModel.getPost().getComments() > 0) {
            noOfComments.setText(String.format(context.getResources().getString(R.string.total_comments), CustomCounter.format(feedModel.getPost().getComments())));
        }
        if (feedModel.getPost().isAllowComments()) {
            commentsDialogModels.add(null);
            commentsDialogAdapter = new CommentsDialogAdapter(context, commentsDialogModels, CommentsDialog.this);
            recyclerView.setAdapter(commentsDialogAdapter);
            initScrollListener();
            Map<String, String> map = new HashMap<>();
            map.put("post_id", String.valueOf(feedModel.getPost().getId()));
            new AsyncRequest(context, CommentsDialog.this, null, new RequestData(URLConfig.RETRIEVE_COMMENTS_URL, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        postComment.setOnClickListener(view1 -> {
            if (TextUtils.isEmpty(commentText.getText().toString())) return;
            Map<String, String> map1 = new HashMap<>();
            ProfileModel selfUserModel = new SelfUser(context).getSelfUser();
            if (selfUserModel == null) {
                ToastDisplay.a(context, R.string.not_logged_in, 0);
                return;
            }
            long identifier = System.currentTimeMillis();
            if (isReply) {
                map1.put("comment_id", String.valueOf(replyComment));
                CommentsRepliesDialogModel commentsRepliesDialogModel = new CommentsRepliesDialogModel(0, selfUserModel.getProfilePic(), 0, selfUserModel.getUserName(), commentText.getText().toString(), 0, 0, 0, false);
                commentsRepliesDialogModel.setPosting(true);
                commentsRepliesDialogModel.setIdentifier(identifier);
                commentsRepliesDialogModels.add(0, commentsRepliesDialogModel);
                if (commentReplyRecyclerView.getAdapter() == null) return;
                commentReplyRecyclerView.getAdapter().notifyItemInserted(0);
                recyclerView.smoothScrollToPosition(commentPos);
            } else {
                commentsInfo.setVisibility(View.GONE);
                CommentsDialogModel commentsDialogModel = new CommentsDialogModel(0, selfUserModel.getProfilePic(), 0, selfUserModel.getUserName(), commentText.getText().toString(), 0, 0, 0, 0, false, new ArrayList<>());
                commentsDialogModel.setPosting(true);
                commentsDialogModel.setIdentifier(identifier);
                commentsDialogModels.add(0, commentsDialogModel);
                commentsDialogAdapter.notifyItemInserted(0);
                recyclerView.smoothScrollToPosition(0);
                map1.put("post_id", String.valueOf(feedModel.getPost().getId()));
            }
            map1.put("comment", commentText.getText().toString());
            InputMethodManager inputManager = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(postComment.getWindowToken(), 0);
            commentText.getText().clear();
            networkRequest(map1, isReply ? URLConfig.COMMENTS_REPLY_URL : URLConfig.POST_COMMENTS_URL, (isSuccess, identifier1, isReply, response) -> {
                if (isReply) {
                    ArrayList<CommentsRepliesDialogModel> commentsRepliesDialogModels = commentsDialogModels.get(commentPos).getCommentsRepliesDialogModel();
                    commentsDialogModels.get(commentPos).setReplies(commentsDialogModels.get(commentPos).getReplies() + 1);
                    for (int i = 0; i < commentsRepliesDialogModels.size(); i++) {
                        if (commentsRepliesDialogModels.get(i).getIdentifier() == identifier1) {
                            if (isSuccess) {
                                onCommentCountChange(true, 1);
                                Type type = new TypeToken<CommentsRepliesDialogModel>() {
                                }.getType();
                                try {
                                    commentsRepliesDialogModels.set(i, new Gson().fromJson(response.getString("data"), type));
                                    if (commentReplyRecyclerView.getAdapter() != null)
                                        commentReplyRecyclerView.getAdapter().notifyItemChanged(i);
                                    recyclerView.smoothScrollToPosition(commentPos);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                commentsRepliesDialogModels.remove(i);
                                if (commentReplyRecyclerView.getAdapter() != null)
                                    commentReplyRecyclerView.getAdapter().notifyItemRemoved(i);
                            }
                            break;
                        }
                    }
                } else {
                    CommentsDialogModel commentsDialogModels2 = commentsDialogModels.get(commentPos);
                    if (commentsDialogModels2.getIdentifier() == identifier1) {
                        if (isSuccess) {
                            onCommentCountChange(true, 1);
                            Type type = new TypeToken<CommentsDialogModel>() {
                            }.getType();
                            try {
                                commentsDialogModels.set(commentPos, new Gson().fromJson(response.getString("data"), type));
                                commentsDialogModels.get(commentPos).setPosting(false);
                                commentsDialogAdapter.notifyItemChanged(commentPos);
                                recyclerView.smoothScrollToPosition(0);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            commentsDialogModels.remove(commentPos);
                            commentsDialogAdapter.notifyItemRemoved(commentPos);
                        }
                    }
                }
            }, identifier, isReply);
            cancelReply();
        });
        closeReply.setOnClickListener(view12 -> cancelReply());
        closeDialog.setOnClickListener(view13 -> getDialog().dismiss());
    }

    private void initView(View view) {
        context = getActivity();
        noOfComments = view.findViewById(R.id.no_of_comments);
        closeDialog = view.findViewById(R.id.close_dialog);
        commentsInfo = view.findViewById(R.id.comments_info);
        replyTo = view.findViewById(R.id.reply_to);
        commentToReply = view.findViewById(R.id.comment_to_reply);
        recyclerView = view.findViewById(R.id.recycler_view);
        commentText = view.findViewById(R.id.comment_text);
        commentText.setAdapter(new MentionTagAdapter(context, commentText, null));
        closeReply = view.findViewById(R.id.close_reply);
        postComment = view.findViewById(R.id.post_comment);
        replyLayout = view.findViewById(R.id.reply_layout);
        relativeView = view.findViewById(R.id.bottomEditor);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
    }

    @Override
    public void onTaskDone(String output) {
        try {
            JSONObject jsonObject = new JSONObject(output);
            Type type = new TypeToken<ArrayList<CommentsDialogModel>>() {
            }.getType();
            commentsDialogModels.remove(0);
            commentsDialogAdapter.notifyItemRemoved(0);
            commentsDialogModels.addAll(new Gson().fromJson(jsonObject.getString("data"), type));
            commentsDialogAdapter.notifyItemRangeInserted(0, commentsDialogModels.size());
            if (commentsDialogModels.size() <= 0) {
                commentsInfo.setVisibility(View.VISIBLE);
                commentsInfo.setText(R.string.first_comment);
            }
            isLoading = false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void reply(CommentsDialogModel commentsDialogModel, String username, String comment) {
        isReply = true;
        replyComment = commentsDialogModel.getCommentId();
        replyTo.setText(String.format(context.getResources().getString(R.string.comment_reply_user_name), username));
        commentToReply.setText(comment);
        commentText.setText(String.format(context.getResources().getString(R.string.comment_reply_user), username));
        commentText.setSelection(commentText.getText().length());
        commentText.requestFocus();
        new Handler().post(() -> {
            InputMethodManager keyboard = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.showSoftInput(commentText, 0);
        });
        Animations.slideUp(replyLayout);
    }

    public void setOnCloseDialog(DialogHandler dialogHandler) {
        this.dialogHandler = dialogHandler;
    }

    @Override
    public void commentReply(int commentPos, String username, String comment, RecyclerView commentReplyRecyclerView) {
        CommentsDialogModel commentsDialogModel = commentsDialogModels.get(commentPos);
        this.commentsRepliesDialogModels = commentsDialogModels.get(commentPos).getCommentsRepliesDialogModel();
        this.commentReplyRecyclerView = commentReplyRecyclerView;
        this.commentPos = commentPos;
        reply(commentsDialogModel, username, comment);
    }

    @Override
    public void onCommentCountChange(boolean isIncreased, long affected) {
        feedModel.getPost().setComments(isIncreased ? feedModel.getPost().getComments() + affected : feedModel.getPost().getComments() - affected);
        noOfComments.setText(String.format(context.getResources().getString(R.string.total_comments), CustomCounter.format(feedModel.getPost().getComments())));
    }

    private void networkRequest(Map<String, String> map, String url, final Callback callback, final long identifier, final boolean isReply) {
        new AsyncRequest(context, output -> {
            try {
                JSONObject root = new JSONObject(output);
                if (!root.has("success")) {
                    ToastDisplay.a(context, root.getString("error"), 0);
                    callback.call(false, identifier, isReply, root);
                    return;
                }
                callback.call(true, identifier, isReply, root);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, null, new RequestData(url, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public interface Callback {
        void call(boolean isSuccess, long identifier, boolean isReply, JSONObject root);
    }

    private void cancelReply() {
        isReply = false;
        replyComment = 0;
        commentText.getText().clear();
        Animations.slideDown(replyLayout);
    }

    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && hasMore) {
                    if (linearLayoutManager != null && commentsDialogModels.size() > 5 && linearLayoutManager.findLastCompletelyVisibleItemPosition() >= commentsDialogModels.size() - 5) {
                        loadData();
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void loadData() {
        int lastData = commentsDialogModels.size() - 1;
        long lastComment = commentsDialogModels.get(lastData).getCommentId();
        long postId = commentsDialogModels.get(lastData).getPostId();
        commentsDialogModels.add(null);
        commentsDialogAdapter.notifyItemInserted(commentsDialogModels.size() - 1);
        Map<String, String> map = new HashMap<>();
        map.put("post_id", String.valueOf(postId));
        map.put("comment_id", String.valueOf(lastComment));
        new AsyncRequest(context, responseResult, null, new RequestData(URLConfig.RETRIEVE_COMMENTS_URL, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private final ResponseResult responseResult = new ResponseResult() {
        @Override
        public void onTaskDone(String output) {
            try {
                JSONObject jsonObject = new JSONObject(output);
                Type type = new TypeToken<ArrayList<CommentsDialogModel>>() {
                }.getType();
                commentsDialogModels.remove(commentsDialogModels.size() - 1);
                commentsDialogAdapter.notifyItemRemoved(commentsDialogAdapter.getItemCount());
                int prevSize = commentsDialogAdapter.getItemCount();
                ArrayList<CommentsDialogModel> newCommentsDialogModels = new Gson().fromJson(jsonObject.getString("data"), type);
                if (newCommentsDialogModels.size() <= 0) hasMore = false;
                commentsDialogModels.addAll(newCommentsDialogModels);
                commentsDialogAdapter.notifyItemRangeInserted(prevSize, commentsDialogModels.size() - prevSize);
                isLoading = false;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        float y = initialY;
        int settledHeight = height;
        settledHeight = Math.abs(settledHeight);
        if (height < 0) {
            getView().findViewById(R.id.comment_text).clearFocus();
            wasNegative = true;
            negativePixelHeight = settledHeight;
        }
        if (settledHeight == 0) {
            if (getView().findViewById(R.id.comment_text).getHeight() != initialheightOfEditText) {
                y += (initialheightOfEditText - (getView().findViewById(R.id.comment_text).getHeight()));
            }

            relativeView.setY(initialY);
            relativeView.requestLayout();
        } else {
            if (wasNegative && settledHeight != negativePixelHeight) {
                y = initialY - (settledHeight + negativePixelHeight) + intialMargin;
            } else if (wasNegative) {
                y = initialY;
            } else {
                y = initialY - settledHeight;
            }

            if (getView().findViewById(R.id.comment_text).getHeight() != initialheightOfEditText) {
                y -= ((getView().findViewById(R.id.comment_text).getHeight()) - initialheightOfEditText);
            }
        }
        relativeView.setY(y);
        relativeView.requestLayout();
    }

    @Override
    public void onPause() {
        super.onPause();
        keyboardHeightProvider.setKeyboardHeightObserver(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        keyboardHeightProvider.setKeyboardHeightObserver(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        keyboardHeightProvider.close();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        dialogHandler.onClose();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

}
