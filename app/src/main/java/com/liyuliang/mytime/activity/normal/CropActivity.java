package com.liyuliang.mytime.activity.normal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;

import com.liyuliang.mytime.R;
import com.liyuliang.mytime.activity.base.SwipeBackActivity;
import com.liyuliang.mytime.utils.ActivityController;
import com.liyuliang.mytime.utils.crop.UCrop;
import com.liyuliang.mytime.utils.crop.util.BitmapLoadUtils;
import com.liyuliang.mytime.utils.crop.view.CropImageView;
import com.liyuliang.mytime.utils.crop.view.GestureCropImageView;
import com.liyuliang.mytime.utils.crop.view.OverlayView;
import com.liyuliang.mytime.utils.crop.view.TransformImageView;
import com.liyuliang.mytime.utils.crop.view.UCropView;
import com.liyuliang.mytime.widget.MyToolbar;

import java.io.OutputStream;

/**
 * 照片裁剪Activity
 * Created by LiYuliang on 2017/7/10 0010.
 *
 * @author LiYuliang
 * @version 2017/10/27
 */

public class CropActivity extends SwipeBackActivity {

    private static final String TAG = "CropActivity";
    private UCropView mUCropView;
    private GestureCropImageView mGestureCropImageView;
    private OverlayView mOverlayView;
    private Uri mOutputUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        MyToolbar toolbar = findViewById(R.id.myToolbar);
        toolbar.initToolBar(this, toolbar, R.string.CropImage, R.drawable.back_white, -1, -1, R.string.Finish, onClickListener);
        initViews();
    }

    private final View.OnClickListener onClickListener = (v) -> {
        if (v.getId() == R.id.toolbarLeft) {
            ActivityController.finishActivity(this);
        } else if (v.getId() == R.id.toolbarRight || v.getId() == R.id.crop_act_save_fab) {
            cropAndSaveImage();
        }
    };

    protected void initViews() {
        mUCropView = findViewById(R.id.weixin_act_ucrop);
        findViewById(R.id.crop_act_save_fab).setOnClickListener(onClickListener);
        mGestureCropImageView = mUCropView.getCropImageView();
        mOverlayView = mUCropView.getOverlayView();
        initCropView();
        initEvents();
    }

    /**
     * 初始化裁剪View
     */
    private void initCropView() {
        // 设置允许缩放
        mGestureCropImageView.setScaleEnabled(true);
        // 设置禁止旋转
        mGestureCropImageView.setRotateEnabled(false);
        // 设置外部阴影颜色
        mOverlayView.setDimmedColor(Color.parseColor("#AA000000"));
        // 设置周围阴影是否为椭圆(如果false则为矩形)
        mOverlayView.setOvalDimmedLayer(true);
        // 设置显示裁剪边框
        mOverlayView.setShowCropFrame(true);
        // 设置不显示裁剪网格
        mOverlayView.setShowCropGrid(false);
        final Intent intent = getIntent();
        setImageData(intent);
    }

    private void setImageData(Intent intent) {
        Uri inputUri = intent.getParcelableExtra(UCrop.EXTRA_INPUT_URI);
        mOutputUri = intent.getParcelableExtra(UCrop.EXTRA_OUTPUT_URI);

        if (inputUri != null && mOutputUri != null) {
            try {
                mGestureCropImageView.setImageUri(inputUri);
            } catch (Exception e) {
                setResultException(e);
                ActivityController.finishActivity(this);
            }
        } else {
            setResultException(new NullPointerException("Both input and output Uri must be specified"));
            ActivityController.finishActivity(this);
        }

        // 设置裁剪宽高比
        if (intent.getBooleanExtra(UCrop.EXTRA_OUTPUT_CROP_ASPECT_RATIO, false)) {
            float aspectRatioX = intent.getFloatExtra(UCrop.EXTRA_ASPECT_RATIO_X, 0);
            float aspectRatioY = intent.getFloatExtra(UCrop.EXTRA_ASPECT_RATIO_Y, 0);

            if (aspectRatioX > 0 && aspectRatioY > 0) {
                mGestureCropImageView.setTargetAspectRatio(aspectRatioX / aspectRatioY);
            } else {
                mGestureCropImageView.setTargetAspectRatio(CropImageView.SOURCE_IMAGE_ASPECT_RATIO);
            }
        }

        // 设置裁剪的最大宽高
        if (intent.getBooleanExtra(UCrop.EXTRA_MAX_SIZE_SET, false)) {
            int maxSizeX = intent.getIntExtra(UCrop.EXTRA_MAX_SIZE_X, 0);
            int maxSizeY = intent.getIntExtra(UCrop.EXTRA_MAX_SIZE_Y, 0);
            if (maxSizeX > 0 && maxSizeY > 0) {
                mGestureCropImageView.setMaxResultImageSizeX(maxSizeX);
                mGestureCropImageView.setMaxResultImageSizeY(maxSizeY);
            } else {
                Log.w(TAG, "EXTRA_MAX_SIZE_X and EXTRA_MAX_SIZE_Y must be greater than 0");
            }
        }
    }

    protected void initEvents() {
        mGestureCropImageView.setTransformImageListener(mImageListener);
    }

    private void cropAndSaveImage() {
        OutputStream outputStream = null;
        try {
            final Bitmap croppedBitmap = mGestureCropImageView.cropImage();
            if (croppedBitmap != null) {
                outputStream = getContentResolver().openOutputStream(mOutputUri);
                croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                croppedBitmap.recycle();
                setResultUri(mOutputUri, mGestureCropImageView.getTargetAspectRatio());
                ActivityController.finishActivity(this);
            } else {
                setResultException(new NullPointerException("CropImageView.cropImage() returned null."));
            }
        } catch (Exception e) {
            setResultException(e);
            ActivityController.finishActivity(this);
        } finally {
            BitmapLoadUtils.close(outputStream);
        }
    }

    private final TransformImageView.TransformImageListener mImageListener = new TransformImageView.TransformImageListener() {
        @Override
        public void onRotate(float currentAngle) {
//            setAngleText(currentAngle);
        }

        @Override
        public void onScale(float currentScale) {
//            setScaleText(currentScale);
        }

        @Override
        public void onLoadComplete() {
            Animation fadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.crop_fade_in);
            fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mUCropView.setVisibility(View.VISIBLE);
                    mGestureCropImageView.setImageToWrapCropBounds();
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            mUCropView.startAnimation(fadeInAnimation);
        }

        @Override
        public void onLoadFailure(@NonNull Exception e) {
            e.printStackTrace();
            setResultException(e);
            ActivityController.finishActivity(CropActivity.this);
        }

    };

    private void setResultUri(Uri uri, float resultAspectRatio) {
        setResult(RESULT_OK, new Intent()
                .putExtra(UCrop.EXTRA_OUTPUT_URI, uri)
                .putExtra(UCrop.EXTRA_OUTPUT_CROP_ASPECT_RATIO, resultAspectRatio));
    }

    private void setResultException(Throwable throwable) {
        setResult(UCrop.RESULT_ERROR, new Intent().putExtra(UCrop.EXTRA_ERROR, throwable));
    }
}
