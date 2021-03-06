package com.liyuliang.mytime.fragment.homepage;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;
import com.liyuliang.mytime.BuildConfig;
import com.liyuliang.mytime.R;
import com.liyuliang.mytime.activity.main.MainActivity;
import com.liyuliang.mytime.activity.base.BaseFragment;
import com.liyuliang.mytime.activity.loginregister.LoginRegisterActivity;
import com.liyuliang.mytime.activity.settings.AccountInfoActivity;
import com.liyuliang.mytime.activity.settings.SetLanguageActivity;
import com.liyuliang.mytime.activity.version.QrCodeShareActivity;
import com.liyuliang.mytime.activity.version.VersionsActivity;
import com.liyuliang.mytime.bean.Result;
import com.liyuliang.mytime.bean.User;
import com.liyuliang.mytime.bean.VersionInfo;
import com.liyuliang.mytime.bean.VersionResult;
import com.liyuliang.mytime.constant.ApkInfo;
import com.liyuliang.mytime.constant.ErrorCode;
import com.liyuliang.mytime.contentprovider.SPHelper;
import com.liyuliang.mytime.interfaces.DownloadProgress;
import com.liyuliang.mytime.network.ExceptionHandle;
import com.liyuliang.mytime.network.NetClient;
import com.liyuliang.mytime.network.NetworkObserver;
import com.liyuliang.mytime.service.DownloadService;
import com.liyuliang.mytime.utils.ActivityController;
import com.liyuliang.mytime.utils.ApkUtils;
import com.liyuliang.mytime.utils.FileUtil;
import com.liyuliang.mytime.utils.GsonUtils;
import com.liyuliang.mytime.utils.LogUtils;
import com.liyuliang.mytime.widget.dialog.SelectDialog;
import com.liyuliang.mytime.widget.dialog.UpgradeVersionDialog;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * ????????????
 * Created at 2019/1/21 13:57
 *
 * @author Li Yuliang
 * @version 1.0
 */

public class SettingsFragment extends BaseFragment {

    private Context mContext;
    private MainActivity mainActivity;
    private ImageView ivUserIcon;
    private TextView tvUserName;
    // ????????????????????????
    private String latestVersionName, latestVersionMD5, latestVersionLog, apkDownloadPath, latestFileName;
    private int myVersionCode, latestVersionCode;

    private static final int GET_UNKNOWN_APP_SOURCES = 2;
    protected static final int INSTALL_PACKAGES_REQUEST_CODE = 103;

    public static DownloadProgress downloadProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mainActivity = (MainActivity) getActivity();
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ivUserIcon = view.findViewById(R.id.ivUserIcon);
        tvUserName = view.findViewById(R.id.tvUserName);

        LinearLayout llUserInfo = view.findViewById(R.id.llUserInfo);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        llUserInfo.setLayoutParams(new LinearLayout.LayoutParams(screenWidth, screenWidth * 9 / 16));

        view.findViewById(R.id.llPersonalInformation).setOnClickListener(onClickListener);
        view.findViewById(R.id.llLanguageSettings).setOnClickListener(onClickListener);
        view.findViewById(R.id.llVersion).setOnClickListener(onClickListener);
        view.findViewById(R.id.llUpdate).setOnClickListener(onClickListener);
        view.findViewById(R.id.llShare).setOnClickListener(onClickListener);
        view.findViewById(R.id.ll_login_account).setOnClickListener(onClickListener);
        view.findViewById(R.id.ll_sign_out).setOnClickListener(onClickListener);

        downloadProgress = new DownloadProgress() {
            @Override
            public void downloadStart() {
                mainActivity.runOnUiThread(() -> showToast("??????????????????"));
            }

            @Override
            public void downloadFinish() {
                mainActivity.runOnUiThread(() -> checkIsAndroidO());
            }
        };
        searchNewVersion(false);

        return view;
    }

    @Override
    public void lazyLoad() {

    }

    @Override
    public void onResume() {
        super.onResume();
        User user = GsonUtils.parseJSON(SPHelper.getString("User", GsonUtils.convertJSON(new User())), User.class);
        showUserIcon((NetClient.getBaseUrl() + user.getIcon()).replace("\\", "/"));
        // ????????????
        myVersionCode = ApkUtils.getVersionCode(mContext);
        tvUserName.setText(user.getUsername());
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
        if (v.getId() == R.id.llPersonalInformation) {
            // ????????????
            mainActivity.openActivity(AccountInfoActivity.class);
        } else if (v.getId() == R.id.llLanguageSettings) {
            // ????????????
            mainActivity.openActivity(SetLanguageActivity.class);
        } else if (v.getId() == R.id.llVersion) {
            // ????????????
            mainActivity.openActivity(VersionsActivity.class);
        } else if (v.getId() == R.id.llUpdate) {
            // ????????????
            searchNewVersion(true);
        } else if (v.getId() == R.id.llShare) {
            // ????????????
            mainActivity.openActivity(QrCodeShareActivity.class);
        } else if (v.getId() == R.id.ll_login_account) {
            // ????????????
            Intent intent1 = new Intent(mainActivity, LoginRegisterActivity.class);
            intent1.setAction("SwitchAccount");
            startActivity(intent1);
            ActivityController.finishActivity(mainActivity);
            mainActivity.overridePendingTransition(R.anim.left_in, R.anim.right_out);
        } else if (v.getId() == R.id.ll_sign_out) {
            // ????????????
            showExitDialog();
        }
    };

    /**
     * ????????????????????????
     */
    private void showExitDialog() {
        SelectDialog selectDialog = new SelectDialog(mContext, getString(R.string.warning_exit));
        selectDialog.setButtonText(getString(R.string.Cancel), getString(R.string.Continue));
        selectDialog.setCancelable(false);
        selectDialog.setOnDialogClickListener(new SelectDialog.OnDialogClickListener() {
            @Override
            public void onOKClick() {
                // ??????????????????
                ActivityController.exit(mainActivity);
            }

            @Override
            public void onCancelClick() {

            }
        });
        selectDialog.show();
    }

    /**
     * ??????????????????
     */
    public void searchNewVersion(boolean showToast) {
        JsonObject params = new JsonObject();
        params.addProperty("apkTypeId", ApkInfo.APK_TYPE_ID_MyTime);

        Observable<Result> resultObservable = NetClient.getInstance(NetClient.getBaseUrlProject(), true).getZbsApi().searchNewVersion(params);
        resultObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NetworkObserver<Result>(mainActivity) {

            @Override
            public void onSubscribe(Disposable d) {
                showLoadingDialog(mContext, "?????????????????????", false);
            }

            @Override
            public void onError(ExceptionHandle.ResponseThrowable responseThrowable) {
                cancelDialog();
            }

            @Override
            public void onNext(@NonNull Result result) {
                super.onNext(result);
                cancelDialog();
                VersionResult versionResult = GsonUtils.parseJSON(GsonUtils.convertJSON(result.getData()), VersionResult.class);
                if (result.getCode() == ErrorCode.SUCCESS) {
                    VersionInfo versionInfo = versionResult.getVersionInfo();
                    latestVersionName = versionInfo.getVersionName();
                    latestVersionMD5 = versionInfo.getMd5Value();
                    latestVersionLog = versionInfo.getVersionLog();
                    apkDownloadPath = versionInfo.getVersionUrl().replace("\\", "/");
                    latestFileName = versionInfo.getVersionFileName();
                    latestVersionCode = versionInfo.getVersionCode();
                    if (myVersionCode < latestVersionCode) {
                        showDialogUpdate();
                    } else {
                        if (showToast) {
                            showToast("?????????????????????????????????");
                        }
                    }
                } else if (result.getCode() == ErrorCode.FAIL) {
                    if (showToast) {
                        showToast("????????????????????????");
                    }
                }
            }
        });
    }

    /**
     * ??????????????????????????????
     */
    private void showDialogUpdate() {
        UpgradeVersionDialog upgradeVersionDialog = new UpgradeVersionDialog(mContext);
        upgradeVersionDialog.setCancelable(false);
        ((TextView) upgradeVersionDialog.findViewById(R.id.tv_versionLog)).setText(latestVersionLog);
        ((TextView) upgradeVersionDialog.findViewById(R.id.tv_currentVersion)).setText(ApkUtils.getVersionName(mContext));
        ((TextView) upgradeVersionDialog.findViewById(R.id.tv_latestVersion)).setText(latestVersionName);
        upgradeVersionDialog.setOnDialogClickListener(new UpgradeVersionDialog.OnDialogClickListener() {
            @Override
            public void onOKClick() {
                downloadApk(apkDownloadPath);
            }

            @Override
            public void onCancelClick() {
                upgradeVersionDialog.dismiss();
            }
        });
        upgradeVersionDialog.show();
    }

    /**
     * ?????????????????????
     */
    public void downloadApk(String downloadUrl) {
        final int DOWNLOAD_APK_ID = 10;
        // ?????????????????????????????????????????????
        if (isServiceRunning(DownloadService.class.getName())) {
            showToast("??????????????????????????????");
        } else {
            Intent intent = new Intent(mainActivity, DownloadService.class);
            Bundle bundle = new Bundle();
            bundle.putString("download_url", downloadUrl);
            bundle.putInt("download_id", DOWNLOAD_APK_ID);
            bundle.putString("download_file", latestFileName);
            intent.putExtras(bundle);
            mainActivity.startService(intent);
        }
    }

    /**
     * ??????????????????????????????.
     *
     * @param className ?????????????????????
     * @return true ????????? false ????????????
     */
    private boolean isServiceRunning(String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mainActivity.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    /**
     * ??????apk
     *
     * @param file ???????????????apk
     */
    private void installApk(File file) {
        //????????????????????????????????????????????????MD5??????
        LogUtils.d(TAG, "???????????????" + file.getAbsolutePath());
        if (file.isFile() && latestVersionMD5.equals(FileUtil.getFileMD5(file))) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri apkUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileProvider", file);//???AndroidManifest??????android:authorities???
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//???????????????????????????????????????????????????Uri??????????????????
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            } else {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }
            startActivity(intent);
        } else {
            showToast("File error");
        }
    }

    /**
     * Android8.0??????????????????????????????????????????,??????????????????
     */
    private void checkIsAndroidO() {
        LogUtils.d(TAG, "??????Android8.0?????????????????????");
        File file = new File(ApkInfo.APP_ROOT_PATH + ApkInfo.DOWNLOAD_DIR, latestFileName);
        if (file.exists()) {
            LogUtils.d(TAG, "???????????????????????????");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LogUtils.d(TAG, "Android??????????????????Android8.0????????????????????????????????????");
                boolean b = mContext.getPackageManager().canRequestPackageInstalls();
                if (b) {
                    LogUtils.d(TAG, "???????????????????????????????????????????????????");
                    installApk(file);
                } else {
                    //???????????????????????????????????????
                    LogUtils.d(TAG, "??????????????????????????????????????????????????????");
                    requestPermissions(new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, INSTALL_PACKAGES_REQUEST_CODE);
                }
            } else {
                LogUtils.d(TAG, "Android????????????Android8.0???????????????");
                installApk(file);
            }
        } else {
            LogUtils.d(TAG, "??????????????????????????????");
            showToast("???????????????");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == INSTALL_PACKAGES_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkIsAndroidO();
            } else {
                //  Android8.0??????????????????????????????????????????
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LogUtils.d(TAG, "??????????????????????????????????????????");
                    Uri packageURI = Uri.parse("package:" + mContext.getPackageName());
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                    startActivityForResult(intent, GET_UNKNOWN_APP_SOURCES);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GET_UNKNOWN_APP_SOURCES) {// ????????????????????????????????????????????????
                checkIsAndroidO();
            }
        }
    }


}
