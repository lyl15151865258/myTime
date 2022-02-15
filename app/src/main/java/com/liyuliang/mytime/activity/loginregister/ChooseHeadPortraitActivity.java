package com.liyuliang.mytime.activity.loginregister;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.liyuliang.mytime.R;
import com.liyuliang.mytime.activity.main.MainActivity;
import com.liyuliang.mytime.activity.base.BaseActivity;
import com.liyuliang.mytime.activity.normal.CropActivity;
import com.liyuliang.mytime.bean.HeadPortraitResult;
import com.liyuliang.mytime.bean.User;
import com.liyuliang.mytime.bean.Result;
import com.liyuliang.mytime.constant.ErrorCode;
import com.liyuliang.mytime.contentprovider.SPHelper;
import com.liyuliang.mytime.interfaces.OnPictureSelectedListener;
import com.liyuliang.mytime.network.ExceptionHandle;
import com.liyuliang.mytime.network.NetClient;
import com.liyuliang.mytime.network.NetworkObserver;
import com.liyuliang.mytime.utils.ActivityController;
import com.liyuliang.mytime.utils.BitmapUtils;
import com.liyuliang.mytime.utils.GsonUtils;
import com.liyuliang.mytime.utils.LogUtils;
import com.liyuliang.mytime.utils.NetworkUtil;
import com.liyuliang.mytime.utils.crop.UCrop;
import com.liyuliang.mytime.widget.MyToolbar;
import com.liyuliang.mytime.widget.dialog.MessageDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 选择头像页面
 * Created at 2018/8/4 0004 15:58
 *
 * @author LiYuliang
 * @version 1.0
 */

public class ChooseHeadPortraitActivity extends BaseActivity {

    private Context mContext;
    private int userId;
    private ImageView ivUserIcon;
    /**
     * 相册选图标记
     */
    private static final int GALLERY_REQUEST_CODE = 0;
    /**
     * 相机拍照标记
     */
    private static final int CAMERA_REQUEST_CODE = 1;
    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    protected static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 102;
    /**
     * 拍照临时图片
     */
    private String mTempPhotoPath;
    /**
     * 剪切后图像文件
     */
    private Uri mDestinationUri;
    private OnPictureSelectedListener mOnPictureSelectedListener;
    /**
     * 标记是否已经上传头像
     */
    private boolean isUploadedIcon = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_head_portrait);
        mContext = this;
        MyToolbar toolbar = findViewById(R.id.myToolbar);
        toolbar.initToolBar(this, toolbar, R.string.SetHeadPortrait, -1, -1, -1, R.string.Finish, onClickListener);
        User user = GsonUtils.parseJSON(SPHelper.getString("User", GsonUtils.convertJSON(new User())), User.class);
        userId = user.getId();
        ivUserIcon = findViewById(R.id.ivUserIcon);
        findViewById(R.id.btnTakePhoto).setOnClickListener(onClickListener);
        findViewById(R.id.btnSelectFromAlbum).setOnClickListener(onClickListener);
        mDestinationUri = Uri.fromFile(new File(getExternalFilesDir("Icons"), "cropImage.jpeg"));
        mTempPhotoPath = Environment.getExternalStorageDirectory() + File.separator + "photo.jpeg";
        setOnPictureSelectedListener(onPictureSelectedListener);
    }

    private final View.OnClickListener onClickListener = (v) -> {
        if (v.getId() == R.id.toolbarRight) {
            if (isUploadedIcon) {
                // 头像设置完毕
                openActivity(MainActivity.class);
                ActivityController.finishActivity(ChooseHeadPortraitActivity.this);
            } else {
                showToast(R.string.UploadHeadPortrait);
            }
        } else if (v.getId() == R.id.btnTakePhoto) {
            takePhoto();
        } else if (v.getId() == R.id.btnSelectFromAlbum) {
            pickFromGallery();
        }
    };

    /**
     * 图片裁剪完成的监听
     */
    private final OnPictureSelectedListener onPictureSelectedListener = (fileUri, bitmap) -> {
        String filePath = fileUri.getEncodedPath();
        String imagePath = Uri.decode(filePath);
        String time = (new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA)).format(new Date());
        //以“.webp”格式作为图片扩展名
        String type = "webp";
        //将本软件的包路径+文件名拼接成图片绝对路径
        User user = GsonUtils.parseJSON(SPHelper.getString("User", GsonUtils.convertJSON(new User())), User.class);
        String newFile = getExternalFilesDir("Icons") + "/" + user.getId() + "_" + time + "." + type;
        BitmapUtils.compressPicture(imagePath, newFile);
        uploadUserIcon(new File(newFile));
    };

    /**
     * 上传或更新头像
     *
     * @param file 头像文件
     */
    private void uploadUserIcon(File file) {
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userId));
        // 创建 RequestBody，用于封装构建RequestBody
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        // MultipartBody.Part  和后端约定好Key，这里的partName是用image
        MultipartBody.Part body = MultipartBody.Part.createFormData("uploadfile", file.getName(), requestFile);
        // 执行请求
        Observable<Result> resultObservable = NetClient.getInstance(NetClient.getBaseUrlProject(), false).getZbsApi().uploadUserIcon(description, body);
        resultObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NetworkObserver<Result>(this) {

            @Override
            public void onSubscribe(Disposable d) {
                //接下来可以检查网络连接等操作
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    showToast(R.string.NetworkUnavailable);
                } else {
                    showLoadingDialog(mContext, getString(R.string.uploading), true);
                }
            }

            @Override
            public void onError(ExceptionHandle.ResponseThrowable responseThrowable) {
                cancelDialog();
                showToast(responseThrowable.message);
            }

            @Override
            public void onNext(@NonNull Result result) {
                cancelDialog();
                super.onNext(result);
                HeadPortraitResult headPortraitResult = GsonUtils.parseJSON(GsonUtils.convertJSON(result.getData()), HeadPortraitResult.class);
                String photoPath = (NetClient.getBaseUrl() + headPortraitResult.getUrl()).replace("\\", "/");
                // 更新存储的User对象
                User user = GsonUtils.parseJSON(SPHelper.getString("User", GsonUtils.convertJSON(new User())), User.class);
                user.setIcon(headPortraitResult.getUrl());
                SPHelper.save("User", GsonUtils.convertJSON(user));
                if (result.getCode() == ErrorCode.SUCCESS) {
                    showToast(R.string.UploadSuccess);
                    showUserIcon(photoPath);
                    isUploadedIcon = true;
                } else if (result.getCode() == ErrorCode.FAIL) {
                    showToast(R.string.UnknownError);
                }
            }
        });
    }

    /**
     * 加载头像
     *
     * @param photoPath 头像路径
     */
    private void showUserIcon(String photoPath) {
        LogUtils.d(TAG, "图片路径：" + photoPath);
        SPHelper.save("userIconPath", photoPath);
        if (photoPath != null) {
            RequestOptions options = new RequestOptions()
                    .error(R.drawable.photo_user)
                    .placeholder(R.drawable.photo_user)
                    .dontAnimate();
            options = options.optionalTransform(new CircleCrop());
            Glide.with(this).load(photoPath).apply(options).into(ivUserIcon);
        }
    }

    /**
     * 设置图片选择的回调监听
     *
     * @param l 监听器
     */
    public void setOnPictureSelectedListener(OnPictureSelectedListener l) {
        this.mOnPictureSelectedListener = l;
    }

    /**
     * 打开相机拍照
     */
    private void takePhoto() {
        // Permission was added in API Level 16
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    getString(R.string.permission_write_storage_rationale),
                    REQUEST_STORAGE_WRITE_ACCESS_PERMISSION);
        } else {
            Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //下面这句指定调用相机拍照后的照片存储的路径
            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mTempPhotoPath)));
            startActivityForResult(takeIntent, CAMERA_REQUEST_CODE);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }

    /**
     * 从相册选择照片
     */
    private void pickFromGallery() {
        // Permission was added in API Level 16
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.permission_read_storage_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
            // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(pickIntent, GALLERY_REQUEST_CODE);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:
                    // 调用相机拍照
                    File temp = new File(mTempPhotoPath);
                    startCropActivity(Uri.fromFile(temp));
                    overridePendingTransition(R.anim.left_in, R.anim.right_out);
                    break;
                case GALLERY_REQUEST_CODE:
                    // 直接从相册获取
                    startCropActivity(data.getData());
                    overridePendingTransition(R.anim.left_in, R.anim.right_out);
                    break;
                case UCrop.REQUEST_CROP:
                    // 裁剪图片结果
                    handleCropResult(data);
                    break;
                case UCrop.RESULT_ERROR:
                    // 裁剪图片错误
                    handleCropError(data);
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri 图片路径
     */
    public void startCropActivity(Uri uri) {
        UCrop.of(uri, mDestinationUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(512, 512)
                .withTargetActivity(CropActivity.class)
                .start(this);
    }

    /**
     * 处理剪切成功的返回值
     *
     * @param result 返回值
     */
    private void handleCropResult(Intent result) {
        deleteTempPhotoFile();
        final Uri resultUri = UCrop.getOutput(result);
        if (null != resultUri && null != mOnPictureSelectedListener) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mOnPictureSelectedListener.onPictureSelected(resultUri, bitmap);
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        } else {
            showToast(R.string.CutError);
        }
    }

    /**
     * 处理剪切失败的返回值
     *
     * @param result 返回值
     */
    private void handleCropError(Intent result) {
        deleteTempPhotoFile();
        Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            showToast(cropError.getMessage());
        } else {
            showToast(R.string.CutError);
        }
    }

    /**
     * 删除拍照临时文件
     */
    private void deleteTempPhotoFile() {
        File tempFile = new File(mTempPhotoPath);
        if (tempFile.exists() && tempFile.isFile()) {
            boolean deleteResult = tempFile.delete();
            if (deleteResult) {
                LogUtils.d("文件删除成功");
            }
        }
    }

    /**
     * 请求权限
     * 如果权限被拒绝过，则提示用户需要权限
     */
    protected void requestPermission(String permission, String rationale, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(permission)) {
                showAlertDialog(permission, rationale, requestCode);
            } else {
                requestPermissions(new String[]{permission}, requestCode);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void showAlertDialog(String permission, String rationale, int requestCode) {
        MessageDialog messageDialog = new MessageDialog(mContext, rationale);
        messageDialog.setButtonText(getString(R.string.Continue));
        messageDialog.setCancelable(false);
        messageDialog.setOnDialogClickListener(() -> {
            requestPermissions(new String[]{permission}, requestCode);
        });
        messageDialog.show();
    }
}