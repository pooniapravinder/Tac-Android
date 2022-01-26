package com.wookes.tac.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.wookes.tac.R;
import com.wookes.tac.request.AsyncRequest;
import com.wookes.tac.request.RequestData;
import com.wookes.tac.request.ResponseResult;
import com.wookes.tac.ui.d.cropoverlay.CropOverlayView;
import com.wookes.tac.ui.d.cropoverlay.edge.Edge;
import com.wookes.tac.ui.d.cropoverlay.utils.ImageViewUtil;
import com.wookes.tac.ui.d.cropoverlay.utils.Utils;
import com.wookes.tac.ui.d.customcropper.CropperView;
import com.wookes.tac.ui.h.PublishUtil;
import com.wookes.tac.ui.j.PublishProfilePhoto;
import com.wookes.tac.urlconfig.URLConfig;
import com.wookes.tac.util.StatusBar;
import com.wookes.tac.util.ToastDisplay;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class ProfilePhoto extends AppCompatActivity {

    private final int IMAGE_MAX_SIZE = 1024 * 10;
    protected ImageView imgImage;
    private float minScale = 1f;
    private RelativeLayout relativeImage;
    private ImageView cropDone;
    private Toolbar toolbar;
    private CropperView cropperView;
    private CropOverlayView cropOverlayView;
    private File mFileTemp;
    private String savedPath;
    private Uri mSaveUri = null;
    private Uri mImageUri = null;
    private ContentResolver mContentResolver;
    private Context context;
    private LinearLayout saving;
    private boolean signUpFlow;
    private PublishProfilePhoto publishProfilePhoto;
    private boolean disabledButton = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StatusBar.setStatusBarNavigationBarWhite(getWindow());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_image_crop);
        findViews();
        initViews();
        toolbar.setNavigationOnClickListener(v -> super.onBackPressed());
        createTempFile();
        showCropping();
        savedPath = mFileTemp + "/" + System.currentTimeMillis() + ".jpg";
        mSaveUri = Utils.getImageUri(savedPath);
        mImageUri = Utils.getImageUri(getIntent().getExtras().getString("imageUri"));
        signUpFlow = getIntent().getExtras().getBoolean("signUpFlow");
        init();
    }

    public void initViews() {
        mContentResolver = getContentResolver();
        context = this;
        publishProfilePhoto = new PublishProfilePhoto(context, profilePhotoListener);
        makeLayoutSquare();
        cropperView.addListener(() -> new Rect((int) Edge.LEFT.getCoordinate(), (int) Edge.TOP.getCoordinate(), (int) Edge.RIGHT.getCoordinate(), (int) Edge.BOTTOM.getCoordinate()));
    }

    public void findViews() {
        relativeImage = findViewById(R.id.relativeImage);
        cropperView = findViewById(R.id.cropperView);
        cropOverlayView = findViewById(R.id.cropOverlayView);
        imgImage = findViewById(R.id.imgImage);
        cropDone = findViewById(R.id.doneCrop);
        saving = findViewById(R.id.saving);
        toolbar = findViewById(R.id.toolbar);
        cropDone.setOnClickListener(view -> {
            if (disabledButton) return;
            disabledButton = true;
            saveAndUploadImage();
        });
    }

    public void makeLayoutSquare() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
        relativeImage.setLayoutParams(params);
    }

    public void hideCropping() {
        imgImage.setVisibility(View.VISIBLE);
        cropperView.setVisibility(View.GONE);
        findViewById(R.id.image_overlay).setVisibility(View.GONE);
    }

    public void showCropping() {
        cropperView.setVisibility(View.VISIBLE);
        cropOverlayView.setVisibility(View.GONE);
        findViewById(R.id.image_overlay).setVisibility(View.VISIBLE);
    }

    public void createTempFile() {
        mFileTemp = new File(context.getCacheDir() + "/profile_pic");
        if (!mFileTemp.exists()) {
            if (!mFileTemp.mkdirs()) {
                Log.i("ProfilePic", "Failed to create directory for thumbnails");
            }
        }
    }

    private void init() {
        showCropping();
        Bitmap b = getBitmap(mImageUri);
        Drawable bitmap = new BitmapDrawable(getResources(), b);
        int h = bitmap.getIntrinsicHeight();
        int w = bitmap.getIntrinsicWidth();
        final float cropWindowWidth = Edge.getWidth();
        final float cropWindowHeight = Edge.getHeight();
        if (h <= w) {
            minScale = (cropWindowHeight + 1f) / h;
        } else {
            minScale = (cropWindowWidth + 1f) / w;
        }
        cropperView.setMaximumScale(minScale * 9);
        cropperView.setMediumScale(minScale * 6);
        cropperView.setMinimumScale(minScale);
        cropperView.setImageDrawable(bitmap);
        cropperView.setScale(minScale);
    }

    private Bitmap getBitmap(Uri uri) {
        InputStream in;
        Bitmap returnedBitmap;
        try {
            in = mContentResolver.openInputStream(uri);
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            if (in != null) {
                in.close();
            }
            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            in = mContentResolver.openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(in, null, o2);
            if (in != null) {
                in.close();
            }
            returnedBitmap = bitmap;
            return returnedBitmap;
        } catch (IOException ignored) {
        }
        return null;
    }

    private void saveAndUploadImage() {
        boolean saved = saveOutput();
        imgImage.setImageBitmap(getBitmap(mSaveUri));
        if (saved) {
            disabledButton = true;
            hideCropping();
            cropDone.setVisibility(View.GONE);
            saving.setVisibility(View.VISIBLE);
            publishProfilePhoto.startUpload(savedPath);
        } else {
            Log.i("SaveImage", "Unable to save image" + this.getClass());
        }
    }

    private boolean saveOutput() {
        Bitmap croppedImage = getCroppedImage();
        if (mSaveUri != null) {
            try (OutputStream outputStream = mContentResolver.openOutputStream(mSaveUri)) {
                if (outputStream != null) {
                    croppedImage.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
        croppedImage.recycle();
        return true;
    }

    private Bitmap getCurrentDisplayedImage() {
        Bitmap result = Bitmap.createBitmap(cropperView.getWidth(), cropperView.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(result);
        cropperView.draw(c);
        return result;
    }

    public Bitmap getCroppedImage() {
        Bitmap mCurrentDisplayedBitmap = getCurrentDisplayedImage();
        Rect displayedImageRect = ImageViewUtil.getBitmapRectCenterInside(mCurrentDisplayedBitmap, cropperView);

        // Get the scale factor between the actual Bitmap dimensions and the
        // displayed dimensions for width.
        float actualImageWidth = mCurrentDisplayedBitmap.getWidth();
        float displayedImageWidth = displayedImageRect.width();
        float scaleFactorWidth = actualImageWidth / displayedImageWidth;

        // Get the scale factor between the actual Bitmap dimensions and the
        // displayed dimensions for height.
        float actualImageHeight = mCurrentDisplayedBitmap.getHeight();
        float displayedImageHeight = displayedImageRect.height();
        float scaleFactorHeight = actualImageHeight / displayedImageHeight;

        // Get crop window position relative to the displayed image.
        float cropWindowX = Edge.LEFT.getCoordinate() - displayedImageRect.left;
        float cropWindowY = Edge.TOP.getCoordinate() - displayedImageRect.top;
        float cropWindowWidth = Edge.getWidth();
        float cropWindowHeight = Edge.getHeight();

        // Scale the crop window position to the actual size of the Bitmap.
        float actualCropX = cropWindowX * scaleFactorWidth;
        float actualCropY = cropWindowY * scaleFactorHeight;
        float actualCropWidth = cropWindowWidth * scaleFactorWidth;
        float actualCropHeight = cropWindowHeight * scaleFactorHeight;

        // Crop the subset from the original Bitmap.
        return Bitmap.createBitmap(mCurrentDisplayedBitmap, (int) actualCropX, (int) actualCropY, (int) actualCropWidth, (int) actualCropHeight);
    }

    private PublishProfilePhoto.MyProfilePhotoListener profilePhotoListener = new PublishProfilePhoto.MyProfilePhotoListener() {

        @Override
        public void onUploadStart() {

        }

        @Override
        public void onUploadProgress(int progress) {

        }

        @Override
        public void onUploadFailed(String error) {
            runOnUiThread(() -> {
                disabledButton = false;
                cropDone.setVisibility(View.VISIBLE);
                saving.setVisibility(View.GONE);
                ToastDisplay.a(context, error, 0);
            });
        }

        @Override
        public void onUploadSuccess(String response, String authKey, String auth) {
            Map<String, String> map = new HashMap<>();
            map.put("profile_photo", PublishUtil.parseObject(101, authKey, auth).getKey());
            new AsyncRequest(context, new ResponseResult() {
                @Override
                public void onTaskDone(String output) {
                    try {
                        JSONObject root = new JSONObject(output);
                        if (!root.has("success")) {
                            return;
                        }
                        runOnUiThread(() -> ToastDisplay.a(context, context.getResources().getString(R.string.profile_photo_update_success), 0));
                        if (signUpFlow) {
                            Intent intent = new Intent(context, LandingUi.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            setResult(Activity.RESULT_OK, new Intent());
                        }
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onTaskFailed(Context context) {
                    disabledButton = false;
                    cropDone.setVisibility(View.VISIBLE);
                    saving.setVisibility(View.GONE);
                    ToastDisplay.a(context, R.string.profile_photo_update_failed, 0);
                }
            }, null, new RequestData(URLConfig.UPLOAD_PROFILE_IMAGE, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    };
}