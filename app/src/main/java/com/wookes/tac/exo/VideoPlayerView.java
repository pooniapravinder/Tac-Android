package com.wookes.tac.exo;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheKeyFactory;
import com.google.android.exoplayer2.upstream.cache.CacheUtil;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.wookes.tac.R;
import com.wookes.tac.cz.ConstantsHome;
import com.wookes.tac.dialog.PostOptionsDialog;
import com.wookes.tac.fragments.CommentsDialog;
import com.wookes.tac.fragments.ShareDialog;
import com.wookes.tac.interfaces.dd;
import com.wookes.tac.model.FeedModel;
import com.wookes.tac.request.AsyncRequest;
import com.wookes.tac.request.RequestData;
import com.wookes.tac.ui.Home;
import com.wookes.tac.ui.LandingUi;
import com.wookes.tac.ui.Login;
import com.wookes.tac.ui.UserProfile;
import com.wookes.tac.urlconfig.URLConfig;
import com.wookes.tac.util.Animations;
import com.wookes.tac.util.CustomCounter;
import com.wookes.tac.util.SelfUser;
import com.wookes.tac.util.ToastDisplay;
import com.wookes.tac.util.c;
import com.wookes.tac.util.e;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class VideoPlayerView extends RecyclerView implements dd, RewardedVideoAdListener {
    private c c;
    private boolean playNext = true, isPaused = false;
    private static final String TAG = "VideoPlayerView";
    private TextView likes, comments;
    private ImageView mediaCoverImage, playbackControl, like, fullscreenExit;
    private View viewHolderParent;
    private FrameLayout mediaContainer, buffering;
    private LinearLayout upperLayout, reactContainer;
    private PlayerView videoSurfaceView;
    private SimpleExoPlayer videoPlayer;
    private ArrayList<FeedModel> feedModels = new ArrayList<>();
    private int videoSurfaceDefaultHeight = 0;
    private int screenDefaultHeight = 0;
    private Context context;
    private int playPosition = -1;
    private boolean isVideoViewAdded;
    private PlaybackState playbackStateEnum = PlaybackState.ON;
    private int currentPlayingPos = 0;
    private ExoCache exoCache;
    private SimpleCache simpleCache;
    private SelfUser selfUser;
    private int adsShown = 0;
    private final HashMap<String, String> map = new HashMap<>();
    private RewardedVideoAd rewardedVideoAd;
    private boolean isLoadedAd;
    private Animatable animatable;
    private FullScreen fullScreenState = FullScreen.OFF;
    private Activity activity;
    private final Handler handler = new Handler();
    private boolean isBuffering = false;
    private final OnClickListener videoViewClickListener = new DoubleClickListener() {
        @Override
        public void onSingleClick(View v) {
            ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);
            togglePlayback();
        }

        @Override
        public void onDoubleClick(View v) {
            performLike();
        }
    };

    public VideoPlayerView(Context context) {
        super(context);
        init(context);
    }

    public VideoPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        c = new c(context);
        activity = ((Activity) context);
        MobileAds.initialize(context, "ca-app-pub-9350254166586365~9234675852");
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
        rewardedVideoAd.setRewardedVideoAdListener(VideoPlayerView.this);
        selfUser = new SelfUser(context);
        simpleCache = ExoCache.getInstance(context);
        exoCache = new ExoCache();
        Display display = ((WindowManager) Objects.requireNonNull(getContext().getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        videoSurfaceDefaultHeight = point.x;
        screenDefaultHeight = point.y;
        videoSurfaceView = new PlayerView(this.context);
        DefaultLoadControl loadControl = new DefaultLoadControl.Builder().setBufferDurationsMs(32 * 1024, 64 * 1024, 1024, 1024).createDefaultLoadControl();
        //Create the player using ExoPlayerFactory
        videoPlayer = new SimpleExoPlayer.Builder(context).setLoadControl(loadControl).build();
        // Disable Player Control
        videoSurfaceView.setUseController(false);
        // Bind the player to the view.
        videoSurfaceView.setPlayer(videoPlayer);
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // There's a special case when the end of the list has been reached.
                    // Need to handle that with this bit of logic
                    playVideo(!recyclerView.canScrollVertically(1));
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        addOnChildAttachStateChangeListener(new OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                if (viewHolderParent != null && viewHolderParent.equals(view)) {
                    resetVideoView();
                }
            }
        });
        videoPlayer.addVideoListener(new VideoListener() {
            @Override
            public void onRenderedFirstFrame() {
                mediaCoverImage.setVisibility(GONE);
                videoSurfaceView.setAlpha(1);
                Log.i("rendered", "rendered");
            }
        });
        videoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case Player.STATE_BUFFERING:
                        isBuffering = true;
                        handler.postDelayed(() -> buffering.setVisibility(isBuffering ? VISIBLE : GONE), 1000);
                        Log.e(TAG, "onPlayerStateChanged: Buffering video.");
                        break;
                    case Player.STATE_ENDED:
                        Log.d(TAG, "onPlayerStateChanged: Video ended.");
                        if (playNext) {
                            if (currentPlayingPos == (feedModels.size() - 1)) videoPlayer.seekTo(0);
                            smoothScrollToPosition(currentPlayingPos + adsShown + 1);
                        } else {
                            videoPlayer.seekTo(0);
                        }
                        break;
                    case Player.STATE_IDLE:
                        videoPlayer.retry();
                        Log.d(TAG, "onPlayerStateChanged: Idle.");
                        break;
                    case Player.STATE_READY:
                        isBuffering = false;
                        Log.e(TAG, "onPlayerStateChanged: Ready to play.");
                        buffering.setVisibility(GONE);
                        if (!isVideoViewAdded) {
                            addVideoView();
                        }
                        for (int i = currentPlayingPos + 1; i <= currentPlayingPos + 3; i++) {
                            if (i <= feedModels.size() - 1 && feedModels.get(i) != null) {
                                preCache(simpleCache, feedModels.get(i).getPost().getUrl(), feedModels.get(i).getPost().getSize());
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public void playVideo(boolean isEndOfList) {
        int targetPosition;
        int adapterPosition = getAdapter() == null ? 0 : getAdapter().getItemViewType(((LinearLayoutManager) Objects.requireNonNull(getLayoutManager())).findFirstVisibleItemPosition());
        adsShown = getRealPosition(((LinearLayoutManager) Objects.requireNonNull(getLayoutManager())).findFirstVisibleItemPosition());
        if (getAdapter() != null && adapterPosition != getAdapter().getItemCount() && getAdapter().getItemViewType(((LinearLayoutManager) Objects.requireNonNull(getLayoutManager())).findFirstVisibleItemPosition() + 5) == ConstantsHome.VIEW_TYPE_ADS) {
            loadRewardedVideoAd();
        }
        if (getAdapter() != null && getAdapter().getItemViewType(((LinearLayoutManager) Objects.requireNonNull(getLayoutManager())).findFirstVisibleItemPosition()) == ConstantsHome.VIEW_TYPE_ADS) {
            if (isLoadedAd) {
                rewardedVideoAd.show();
                isLoadedAd = false;
                playNext = false;
                isPaused = true;
                return;
            } else {
                playNext = true;
                isPaused = false;
                pausePlayer();
            }
        } else {
            playNext = true;
            isPaused = false;
        }
        if (!isEndOfList) {
            int startPosition = ((LinearLayoutManager) Objects.requireNonNull(getLayoutManager())).findFirstVisibleItemPosition();
            int endPosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
            // if there is more than 2 list-items on the screen, set the difference to be 1
            if (endPosition - startPosition > 1) {
                endPosition = startPosition + 1;
            }
            // something is wrong. return.
            if (startPosition < 0 || endPosition < 0) {
                return;
            }
            // if there is more than 1 list-item on the screen
            if (startPosition != endPosition) {
                int startPositionVideoHeight = getVisibleVideoSurfaceHeight(startPosition);
                int endPositionVideoHeight = getVisibleVideoSurfaceHeight(endPosition);
                targetPosition = startPositionVideoHeight > endPositionVideoHeight ? startPosition : endPosition;
            } else {
                targetPosition = startPosition;
            }
        } else {
            targetPosition = feedModels.size() + adsShown - 1;
        }
        final int temptargetPosition = targetPosition - adsShown;
        currentPlayingPos = temptargetPosition;
        Log.d(TAG, "playVideo: target position: " + temptargetPosition);
        // video is already playing so return
        if (temptargetPosition == playPosition) {
            return;
        }
        // set the position of the list-item that is to be played
        playPosition = targetPosition - adsShown;
        if (videoSurfaceView == null) {
            return;
        }
        // remove any old surface views from previously playing videos
        videoSurfaceView.setVisibility(INVISIBLE);
        removeVideoView(videoSurfaceView);
        int currentPosition = targetPosition - ((LinearLayoutManager) Objects.requireNonNull(getLayoutManager())).findFirstVisibleItemPosition();
        View child = getChildAt(currentPosition);
        if (child == null) {
            return;
        }
        PlayerViewHolder holder = (PlayerViewHolder) child.getTag();
        if (holder == null) {
            playPosition = -1;
            return;
        }
        mediaCoverImage = holder.mediaCoverImage;
        like = holder.like;
        likes = holder.likes;
        comments = holder.comments;
        playbackControl = holder.playbackControl;
        viewHolderParent = holder.itemView;
        mediaContainer = holder.mediaContainer;
        buffering = holder.buffering;
        upperLayout = holder.upperLayout;
        reactContainer = holder.reactContainer;
        ImageView fullscreen = holder.fullscreen;
        fullscreenExit = holder.fullscreenExit;
        fullScreenState = c.isFullscreenPlayer() ? FullScreen.ON : FullScreen.OFF;
        setFullscreen();
        fullscreen.setOnClickListener(view -> toggleFullscreen());
        fullscreenExit.setOnClickListener(view -> toggleFullscreen());
        holder.musicName.setSelected(true);
        animatable = ((Animatable) holder.musicIcon.getDrawable());
        animatable.start();
        videoSurfaceView.setResizeMode(feedModels.get(temptargetPosition).getPost().getHeight() >= 960 ? AspectRatioFrameLayout.RESIZE_MODE_ZOOM : AspectRatioFrameLayout.RESIZE_MODE_FIT);
        videoSurfaceView.setPlayer(videoPlayer);
        holder.profilePic.setOnClickListener(view -> showProfile(temptargetPosition));
        holder.userHandle.setOnClickListener(view -> showProfile(temptargetPosition));
        map.put("user", String.valueOf(feedModels.get(temptargetPosition).getUser().getId()));
        if (selfUser.getSelfUser() != null && selfUser.getSelfUser().getUserId() == feedModels.get(temptargetPosition).getUser().getId()) {
            holder.follow.setVisibility(View.GONE);
            holder.unfollow.setVisibility(View.GONE);
        } else {
            if (feedModels.get(temptargetPosition).isFollowing()) {
                holder.follow.setVisibility(View.GONE);
                holder.unfollow.setVisibility(View.VISIBLE);
            } else {
                holder.follow.setVisibility(View.VISIBLE);
                holder.unfollow.setVisibility(View.GONE);
            }
        }
        holder.follow.setOnClickListener(view -> {
            if (selfUser.getSelfUser() == null) {
                context.startActivity(new Intent(context, Login.class));
                return;
            }
            toggleFollowUnfollow(holder, true, feedModels.get(temptargetPosition).getUser().getId());
            new AsyncRequest(context, output -> {
                try {
                    JSONObject jsonObject = new JSONObject(output);
                    if (!jsonObject.has("success")) {
                        toggleFollowUnfollow(holder, false, feedModels.get(temptargetPosition).getUser().getId());
                        ToastDisplay.a(context, jsonObject.getString("error"), 0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, null, new RequestData(URLConfig.FOLLOW_USER, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        });
        holder.unfollow.setOnClickListener(view -> {
            if (selfUser.getSelfUser() == null) {
                context.startActivity(new Intent(context, Login.class));
                return;
            }
            toggleFollowUnfollow(holder, false, feedModels.get(temptargetPosition).getUser().getId());
            new AsyncRequest(context, output -> {
                try {
                    JSONObject jsonObject = new JSONObject(output);
                    if (!jsonObject.has("success")) {
                        toggleFollowUnfollow(holder, true, feedModels.get(temptargetPosition).getUser().getId());
                        ToastDisplay.a(context, jsonObject.getString("error"), 0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, null, new RequestData(URLConfig.UNFOLLOW_USER, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        });
        holder.comment.setOnClickListener(view -> showCommentsDialog(feedModels.get(temptargetPosition)));
        holder.share.setOnClickListener(view -> showSharesDialog(feedModels.get(temptargetPosition)));
        viewHolderParent.setOnClickListener(videoViewClickListener);
        viewHolderParent.setOnLongClickListener(view -> {
            if (!c.isLoggedIn()) return false;
            playNext = false;
            PostOptionsDialog postOptionsDialog = new PostOptionsDialog(context, VideoPlayerView.this, feedModels.get(temptargetPosition));
            postOptionsDialog.show();
            postOptionsDialog.setOnDismissListener(dialogInterface -> playNext = true);
            return false;
        });
        String mediaUrl = feedModels.get(temptargetPosition).getPost().getUrl();
        like.setOnClickListener(v -> performLike());
        CacheDataSourceFactory cacheDataSourceFactory = new CacheDataSourceFactory(simpleCache, new DefaultHttpDataSourceFactory(Util.getUserAgent(context, context.getResources().getString(R.string.app_name))), CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
        @SuppressWarnings("deprecation") MediaSource videoSource = new ExtractorMediaSource(e.a() ? null : Uri.parse(mediaUrl), cacheDataSourceFactory, new DefaultExtractorsFactory(), null, null);
        videoPlayer.prepare(videoSource);
        videoPlayer.setPlayWhenReady(true);
    }

    /**
     * Returns the visible region of the video surface on the screen.
     * if some is cut off, it will return less than the @videoSurfaceDefaultHeight
     */
    private int getVisibleVideoSurfaceHeight(int playPosition) {
        int at = playPosition - ((LinearLayoutManager) Objects.requireNonNull(getLayoutManager())).findFirstVisibleItemPosition();
        Log.d(TAG, "getVisibleVideoSurfaceHeight: at: " + at);
        View child = getChildAt(at);
        if (child == null) {
            return 0;
        }
        int[] location = new int[2];
        child.getLocationInWindow(location);
        if (location[1] < 0) {
            return location[1] + videoSurfaceDefaultHeight;
        } else {
            return screenDefaultHeight - location[1];
        }
    }

    // Remove the old player
    private void removeVideoView(PlayerView videoView) {
        ViewGroup parent = (ViewGroup) videoView.getParent();
        if (parent == null) {
            return;
        }
        int index = parent.indexOfChild(videoView);
        if (index >= 0) {
            parent.removeViewAt(index);
            isVideoViewAdded = false;
            viewHolderParent.setOnClickListener(null);
        }
    }

    private void addVideoView() {
        if (playbackStateEnum == PlaybackState.OFF) {
            togglePlayback();
        }
        mediaContainer.addView(videoSurfaceView);
        upperLayout.bringToFront();
        fullscreenExit.bringToFront();
        isVideoViewAdded = true;
        videoSurfaceView.requestFocus();
        videoSurfaceView.setAlpha(0);
        videoSurfaceView.setVisibility(VISIBLE);
    }

    private void resetVideoView() {
        if (isVideoViewAdded) {
            removeVideoView(videoSurfaceView);
            playPosition = -1;
            videoSurfaceView.setVisibility(INVISIBLE);
            mediaCoverImage.setVisibility(VISIBLE);
        }
    }

    public void releasePlayer() {
        if (videoPlayer != null) {
            videoPlayer.release();
            videoPlayer = null;
        }
        viewHolderParent = null;
    }

    public void resumePlayer() {
        if (animatable != null)
            animatable.start();
        if (playbackStateEnum == PlaybackState.ON && !isPaused) {
            startPlayer();
        }
    }

    public void pausePlayer() {
        if (videoPlayer != null) {
            videoPlayer.setPlayWhenReady(false);
        }
    }

    public void startPlayer() {
        if (videoPlayer != null) {
            videoPlayer.setPlayWhenReady(true);
        }
    }

    private void togglePlayback() {
        if (videoPlayer != null) {
            playbackStateEnum = playbackStateEnum == PlaybackState.ON ? PlaybackState.OFF : PlaybackState.ON;
            playbackControl.bringToFront();
            if (playbackStateEnum == PlaybackState.OFF) {
                pausePlayer();
                playbackControl.startAnimation(Animations.bubblePopupShow());
                playbackControl.setVisibility(VISIBLE);
            } else if (playbackStateEnum == PlaybackState.ON) {
                startPlayer();
                playbackControl.startAnimation(Animations.bubblePopupHide());
                playbackControl.setVisibility(GONE);
            }
        }
    }


    public void setHomeModels(ArrayList<FeedModel> feedModels) {
        this.feedModels = feedModels;
    }

    @Override
    public void onSave() {
        Map<String, String> map = new HashMap<>();
        map.put("post_id", String.valueOf(feedModels.get(currentPlayingPos).getPost().getId()));
        new AsyncRequest(context, output -> {
            try {
                JSONObject root = new JSONObject(output);
                if (root.has("success")) {
                    feedModels.get(currentPlayingPos).getPost().setSaved(!feedModels.get(currentPlayingPos).getPost().isSaved());
                    ToastDisplay.a(context, feedModels.get(currentPlayingPos).getPost().isSaved() ? R.string.added_to_favorites : R.string.removed_from_favorites, 0);
                } else {
                    ToastDisplay.a(context, root.getString("error"), 0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, null, new RequestData(URLConfig.SAVE_POST, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onDelete() {
        Map<String, String> map = new HashMap<>();
        map.put("post_id", String.valueOf(feedModels.get(currentPlayingPos).getPost().getId()));
        new AsyncRequest(context, output -> {
            try {
                JSONObject root = new JSONObject(output);
                if (root.has("success")) {
                    ToastDisplay.a(context, context.getResources().getString(R.string.post_deleted), 0);
                    removeItem(currentPlayingPos);
                } else {
                    ToastDisplay.a(context, root.getString("error"), 0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, null, new RequestData(URLConfig.DELETE_POST, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void removeItem(int position) {
        feedModels.remove(position);
        getAdapter().notifyItemRemoved(position);
        getAdapter().notifyItemRangeChanged(position, feedModels.size());
        pausePlayer();
        new Handler().postDelayed(() -> playVideo(position == feedModels.size() - 1), 500);
    }

    @Override
    public void onReport() {

    }

    @Override
    public void onTurnComments() {
        Map<String, String> map = new HashMap<>();
        map.put("post_id", String.valueOf(feedModels.get(currentPlayingPos).getPost().getId()));
        new AsyncRequest(context, output -> {
            try {
                JSONObject root = new JSONObject(output);
                if (root.has("success")) {
                    feedModels.get(currentPlayingPos).getPost().setAllowComments(!feedModels.get(currentPlayingPos).getPost().isAllowComments());
                    ToastDisplay.a(context, feedModels.get(currentPlayingPos).getPost().isAllowComments() ? R.string.comments_turned_on : R.string.comments_turned_off, 0);
                } else {
                    ToastDisplay.a(context, root.getString("error"), 0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, null, new RequestData(URLConfig.TURN_POST_COMMENTS, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onTurnDownloads() {
        Map<String, String> map = new HashMap<>();
        map.put("post_id", String.valueOf(feedModels.get(currentPlayingPos).getPost().getId()));
        new AsyncRequest(context, output -> {
            try {
                JSONObject root = new JSONObject(output);
                if (root.has("success")) {
                    feedModels.get(currentPlayingPos).getPost().setAllowDownloads(!feedModels.get(currentPlayingPos).getPost().isAllowDownloads());
                    ToastDisplay.a(context, feedModels.get(currentPlayingPos).getPost().isAllowDownloads() ? R.string.downloads_turned_on : R.string.downloads_turned_off, 0);
                } else {
                    ToastDisplay.a(context, root.getString("error"), 0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, null, new RequestData(URLConfig.TURN_POST_DOWNLOADS, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private enum PlaybackState {
        ON, OFF
    }

    public void startupPlayer() {
        new Handler().postDelayed(() -> playVideo(false), 100);
    }

    public void initialPause() {
        new Handler().postDelayed(this::pausePlayer, 200);
    }

    private void performLike() {
        final FeedModel feedModel = feedModels.get(currentPlayingPos);
        performLikeUi(feedModel);
        Map<String, String> map = new HashMap<>();
        map.put("post_id", String.valueOf(feedModel.getPost().getId()));
        new AsyncRequest(context, output -> {
            try {
                JSONObject root = new JSONObject(output);
                if (!root.has("success")) {
                    performLikeUi(feedModel);
                    ToastDisplay.a(context, root.getString("error"), 0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, null, new RequestData(URLConfig.LIKE_POST_URL, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    void performLikeUi(FeedModel feedModel) {
        if (feedModel.getPost().isLiked()) {
            feedModel.getPost().setLiked(false);
            feedModel.getPost().setLikes(feedModel.getPost().getLikes() - 1);
            like.setImageResource(R.drawable.icon_video_like_nor);
        } else {
            feedModel.getPost().setLiked(true);
            feedModel.getPost().setLikes(feedModel.getPost().getLikes() + 1);
            like.setImageResource(R.drawable.icon_video_like_enable);
        }
        likes.setText(String.valueOf(feedModel.getPost().getLikes()));
        like.startAnimation(Animations.bubblePopupShow());
    }

    private void preCache(SimpleCache simpleCache, String url, long size) {
        int oneMB = 1 * 1024 * 1024; // 1 mb pre cache
        long cacheSize = size > oneMB ? oneMB : size;
        if(simpleCache.isCached(url,0, cacheSize)) return;
        Uri videoUri = Uri.parse(url);
        DataSpec dataSpec = new DataSpec(e.a() ? null : videoUri, 0, cacheSize, null);
        CacheKeyFactory defaultCacheKeyFactory = CacheUtil.DEFAULT_CACHE_KEY_FACTORY;
        /*CacheUtil.ProgressListener progressListener = (requestLength, bytesCached, newBytesCached) -> {
            Double downloadPercentage = (bytesCached * 100.0 / requestLength);
        };*/
        DataSource dataSource = new DefaultDataSourceFactory(context, Util.getUserAgent(context, context.getResources().getString(R.string.app_name))).createDataSource();
        Thread thread = new Thread(() -> {
            try {
                exoCache.cacheVideo(simpleCache, dataSpec, defaultCacheKeyFactory, dataSource, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    private void showCommentsDialog(FeedModel feedModel) {
        playNext = false;
        final CommentsDialog commentsDialog = CommentsDialog.newInstance();
        Bundle args = new Bundle();
        args.putSerializable("feedModel", feedModel);
        commentsDialog.setArguments(args);
        commentsDialog.setOnCloseDialog(() -> {
            comments.setText(CustomCounter.format(feedModel.getPost().getComments()));
            commentsDialog.dismiss();
            playNext = true;
        });
        commentsDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), CommentsDialog.TAG);
    }

    private void showSharesDialog(FeedModel feedModel) {
        playNext = false;
        ShareDialog shareDialog = ShareDialog.newInstance();
        Bundle args = new Bundle();
        args.putSerializable("feedModel", feedModel);
        shareDialog.setArguments(args);
        shareDialog.setReference(VideoPlayerView.this);
        shareDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), ShareDialog.TAG);
        shareDialog.setListener(shareDialogListener);
    }

    private final ShareDialog.ShareDialogListener shareDialogListener = () -> playNext = true;

    private void showProfile(int targetPosition) {
        Intent intent = new Intent(context, UserProfile.class);
        intent.putExtra("user", String.valueOf(feedModels.get(targetPosition).getUser().getId()));
        intent.putExtra("user_type", 0);
        context.startActivity(intent);
    }

    private void toggleFollowUnfollow(PlayerViewHolder holder, boolean isFollow, long userId) {
        for (FeedModel feedModel : feedModels) {
            if (feedModel.getUser().getId() == userId) {
                feedModel.setFollowing(isFollow);
            }
        }
        holder.follow.setVisibility(holder.follow.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        holder.unfollow.setVisibility(holder.unfollow.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    private void loadRewardedVideoAd() {
        AdRequest request = new AdRequest.Builder().build();
        rewardedVideoAd.loadAd("ca-app-pub-9350254166586365/4276153759", request);
        if (rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.show();
        }
    }

    private int getRealPosition(int position) {
        return ConstantsHome.ADS_DELTA > 0 ? (position + 1) / (ConstantsHome.ADS_DELTA + 1) : 0;
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        isLoadedAd = true;
        Log.i("adsres", "loaded");
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Log.i("adsres", "opened");
    }

    @Override
    public void onRewardedVideoStarted() {
        Log.i("adsres", "started");
    }

    @Override
    public void onRewardedVideoAdClosed() {
        smoothScrollToPosition(currentPlayingPos + adsShown + 1);
        isLoadedAd = false;
        Log.i("adsres", "closed");
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Log.i("adsres", "left");
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        isLoadedAd = false;
        Log.i("adsres", "failed to loads" + i);
    }

    @Override
    public void onRewardedVideoCompleted() {
        Log.i("adsres", "complete");
    }

    private void toggleFullscreen() {
        fullScreenState = fullScreenState == FullScreen.ON ? FullScreen.OFF : FullScreen.ON;
        c.playerPrefs(fullScreenState == FullScreen.ON);
        setFullscreen();
    }

    private void setFullscreen() {
        if (fullScreenState == FullScreen.OFF) {
            if (activity instanceof LandingUi) {
                Fragment fragment = ((LandingUi) activity).getSupportFragmentManager().findFragmentByTag(Home.class.getName());
                if (fragment != null) {
                    View view = ((Home) fragment).getView();
                    if (view != null) view.findViewById(R.id.appBar).setVisibility(VISIBLE);
                }
                activity.findViewById(R.id.navigation).setVisibility(VISIBLE);
            }
            fullscreenExit.setVisibility(GONE);
            reactContainer.setVisibility(VISIBLE);
        } else {
            if (activity instanceof LandingUi) {
                Fragment fragment = ((LandingUi) activity).getSupportFragmentManager().findFragmentByTag(Home.class.getName());
                if (fragment != null) {
                    View view = ((Home) fragment).getView();
                    if (view != null) view.findViewById(R.id.appBar).setVisibility(GONE);
                }
                activity.findViewById(R.id.navigation).setVisibility(GONE);
            }
            reactContainer.setVisibility(GONE);
            fullscreenExit.setVisibility(VISIBLE);
        }
    }

    private enum FullScreen {
        ON, OFF
    }
}