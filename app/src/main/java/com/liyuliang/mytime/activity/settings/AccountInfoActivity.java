package com.liyuliang.mytime.activity.settings;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.liyuliang.mytime.R;
import com.liyuliang.mytime.activity.base.SwipeBackActivity;
import com.liyuliang.mytime.activity.loginregister.ResetPasswordActivity;
import com.liyuliang.mytime.activity.normal.CropActivity;
import com.liyuliang.mytime.bean.HeadPortraitResult;
import com.liyuliang.mytime.bean.Result;
import com.liyuliang.mytime.bean.User;
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
import com.liyuliang.mytime.widget.popupwindow.SelectPicturePopupWindow;

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
 * ????????????????????????
 * Created at 2018/11/20 13:37
 *
 * @author LiYuliang
 * @version 1.0
 */

public class AccountInfoActivity extends SwipeBackActivity {

    private Context mContext;
    private ImageView ivUserIcon;
    private int userId;
    private TextView tvUserName, tvGender, tvPhoneNumber, tvMailAddress, tvRegisterTime;
    private static final int GALLERY_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    protected static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 102;
    private String mTempPhotoPath;
    private Uri mDestinationUri;
    private SelectPicturePopupWindow mSelectPicturePopupWindow;
    private OnPictureSelectedListener mOnPictureSelectedListener;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        mContext = this;
        MyToolbar toolbar = findViewById(R.id.myToolbar);
        toolbar.initToolBar(this, toolbar, R.string.AccountInformation, R.drawable.back_white, -1, -1, -1, onClickListener);
        ivUserIcon = findViewById(R.id.ivUserIcon);
        findViewById(R.id.llUserIcon).setOnClickListener(onClickListener);
        findViewById(R.id.llUserName).setOnClickListener(onClickListener);
        findViewById(R.id.llGender).setOnClickListener(onClickListener);
        findViewById(R.id.llPhoneNumber).setOnClickListener(onClickListener);
        findViewById(R.id.llMailAddress).setOnClickListener(onClickListener);
        findViewById(R.id.llModifyPassword).setOnClickListener(onClickListener);

        tvUserName = findViewById(R.id.tvUserName);
        tvGender = findViewById(R.id.tvGender);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvMailAddress = findViewById(R.id.tvMailAddress);
        tvRegisterTime = findViewById(R.id.tvRegisterTime);
        // ??????????????????????????????
        setOnPictureSelectedListener(onPictureSelectedListener);
        mDestinationUri = Uri.fromFile(new File(getExternalFilesDir("Icons"), "cropImage.jpeg"));
        mTempPhotoPath = Environment.getExternalStorageDirectory() + File.separator + "photo.jpeg";
        mSelectPicturePopupWindow = new SelectPicturePopupWindow(mContext, (findViewById(android.R.id.content)));
        mSelectPicturePopupWindow.setOnSelectedListener(selectedListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        user = GsonUtils.parseJSON(SPHelper.getString("User", GsonUtils.convertJSON(new User())), User.class);
        showUserIcon((NetClient.getBaseUrl() + user.getIcon()).replace("\\", "/"));
        userId = user.getId();
        tvUserName.setText(user.getUsername());
        tvGender.setText(user.getGender());
        tvPhoneNumber.setText(user.getPhone());
        tvMailAddress.setText(user.getMail());
        tvRegisterTime.setText(user.getRegisterTime());
    }

    /**
     * ????????????
     *
     * @param photoPath ????????????
     */
    private void showUserIcon(String photoPath) {
        LogUtils.d(TAG, "???????????????" + photoPath);
        SPHelper.save("userIconPath", photoPath);
        if (photoPath != null) {
            RequestOptions options = new RequestOptions().error(R.drawable.photo_user).placeholder(R.drawable.photo_user).dontAnimate().circleCrop();
            Glide.with(this).load(photoPath).apply(options).into(ivUserIcon);
        }
    }

    private final View.OnClickListener onClickListener = (v) -> {
        Intent intent = null;
        if (v.getId() == R.id.toolbarLeft) {
            ActivityController.finishActivity(this);
        } else if (v.getId() == R.id.llUserIcon) {
            // ????????????
            showSelectPicturePopupWindow(findViewById(R.id.ll_root));
        } else if (v.getId() == R.id.llUserName) {
            // ????????????
            intent = new Intent(this, SetUserNameActivity.class);
        } else if (v.getId() == R.id.llGender) {
            // ????????????
            intent = new Intent(this, SetGenderActivity.class);
        } else if (v.getId() == R.id.llPhoneNumber) {
            // ??????????????????
            intent = new Intent(this, SetPhoneNumberActivity.class);
        } else if (v.getId() == R.id.llMailAddress) {
            // ??????????????????
            intent = new Intent(this, SetEmailActivity.class);
        } else if (v.getId() == R.id.llModifyPassword) {
            // ????????????
            openActivity(ResetPasswordActivity.class);
        }
        if (intent != null) {
            intent.putExtra("flag", 1);
            intent.putExtra("user", user);
            startActivity(intent);
        }
    };

    private final SelectPicturePopupWindow.OnSelectedListener selectedListener = new SelectPicturePopupWindow.OnSelectedListener() {
        @Override
        public void onSelected(View v, int position) {
            switch (position) {
                case 0:
                    // "??????"??????????????????
                    takePhoto();
                    break;
                case 1:
                    // "???????????????"??????????????????
                    pickFromGallery();
                    break;
                case 2:
                    // "??????"??????????????????
                    mSelectPicturePopupWindow.dismissPopupWindow();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * ???????????????????????????
     */
    private final OnPictureSelectedListener onPictureSelectedListener = (fileUri, bitmap) -> {
        String filePath = fileUri.getEncodedPath();
        String imagePath = Uri.decode(filePath);
        String time = (new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA)).format(new Date());
        //??????.webp??????????????????????????????
        String type = "jpeg";
        //????????????????????????+????????????????????????????????????
        String newFile = getExternalFilesDir("Icons") + "/" + time + "-" + userId + "." + type;
        BitmapUtils.compressPicture(imagePath, newFile);
        uploadUserIcon(new File(newFile));
    };

    /**
     * ?????????????????????
     *
     * @param file ????????????
     */
    private void uploadUserIcon(File file) {
        String descriptionString = String.valueOf(userId);
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), descriptionString);
        // ?????? RequestBody?????????????????????RequestBody
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        // MultipartBody.Part  ??????????????????Key????????????partName??????image
        MultipartBody.Part body = MultipartBody.Part.createFormData("uploadfile", file.getName(), requestFile);
        // ????????????
        Observable<Result> resultObservable = NetClient.getInstance(NetClient.getBaseUrlProject(), false).getZbsApi().uploadUserIcon(description, body);
        resultObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NetworkObserver<Result>(this) {

            @Override
            public void onSubscribe(Disposable d) {
                //??????????????????????????????????????????
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    showToast("???????????????????????????????????????");
                } else {
                    showLoadingDialog(mContext, "?????????", true);
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
                // ???????????????User??????
                user.setIcon(headPortraitResult.getUrl());
                SPHelper.save("User", GsonUtils.convertJSON(user));
                if (result.getCode() == ErrorCode.SUCCESS) {
                    showToast("??????????????????");
                    showUserIcon(photoPath);
                } else if (result.getCode() == ErrorCode.FAIL) {
                    showToast("??????????????????");
                }
            }
        });
    }

    protected void showSelectPicturePopupWindow(View parent) {
        mSelectPicturePopupWindow.showPopupWindow(parent);
    }

    /**
     * ?????????????????????????????????
     *
     * @param l ?????????
     */
    public void setOnPictureSelectedListener(OnPictureSelectedListener l) {
        this.mOnPictureSelectedListener = l;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_READ_ACCESS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromGallery();
                }
                break;
            case REQUEST_STORAGE_WRITE_ACCESS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * ??????????????????
     */
    private void takePhoto() {
        // Permission was added in API Level 16
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    getString(R.string.permission_write_storage_rationale),
                    REQUEST_STORAGE_WRITE_ACCESS_PERMISSION);
        } else {
            mSelectPicturePopupWindow.dismissPopupWindow();
            Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //???????????????????????????????????????????????????????????????
            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mTempPhotoPath)));
            startActivityForResult(takeIntent, CAMERA_REQUEST_CODE);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }

    /**
     * ?????????????????????
     */
    private void pickFromGallery() {
        // Permission was added in API Level 16
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.permission_read_storage_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {
            mSelectPicturePopupWindow.dismissPopupWindow();
            Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
            // ?????????????????????????????????????????????????????????????????????"image/jpeg ??? image/png????????????"
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
                    // ??????????????????
                    File temp = new File(mTempPhotoPath);
                    startCropActivity(Uri.fromFile(temp));
                    overridePendingTransition(R.anim.left_in, R.anim.right_out);
                    break;
                case GALLERY_REQUEST_CODE:
                    // ?????????????????????
                    startCropActivity(data.getData());
                    overridePendingTransition(R.anim.left_in, R.anim.right_out);
                    break;
                case UCrop.REQUEST_CROP:
                    // ??????????????????
                    handleCropResult(data);
                    break;
                case UCrop.RESULT_ERROR:
                    // ??????????????????
                    handleCropError(data);
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * ????????????????????????
     *
     * @param uri ????????????
     */
    public void startCropActivity(Uri uri) {
        UCrop.of(uri, mDestinationUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(512, 512)
                .withTargetActivity(CropActivity.class)
                .start(this);
    }

    /**
     * ??????????????????????????????
     *
     * @param result ?????????
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
            showToast("????????????????????????");
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param result ?????????
     */
    private void handleCropError(Intent result) {
        deleteTempPhotoFile();
        Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            showToast(cropError.getMessage());
        } else {
            showToast("????????????????????????");
        }
    }

    /**
     * ????????????????????????
     */
    private void deleteTempPhotoFile() {
        File tempFile = new File(mTempPhotoPath);
        if (tempFile.exists() && tempFile.isFile()) {
            boolean deleteResult = tempFile.delete();
            if (deleteResult) {
                LogUtils.d("??????????????????");
            }
        }
    }

    /**
     * ????????????
     * ??????????????????????????????????????????????????????
     */
    protected void requestPermission(String permission, String rationale, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(permission)) {
                showAlertDialog(getString(R.string.permission_title_rationale), rationale,
                        (dialog, which) -> requestPermissions(new String[]{permission}, requestCode), getString(R.string.determine));
            } else {
                requestPermissions(new String[]{permission}, requestCode);
            }
        }
    }

    protected void showAlertDialog(String title, String message, DialogInterface.OnClickListener onPositiveButtonClickListener, String positiveText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveText, onPositiveButtonClickListener);
        builder.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mSelectPicturePopupWindow.isShowing()) {
                mSelectPicturePopupWindow.dismissPopupWindow();
                return true;
            } else {
                ActivityController.finishActivity(this);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
